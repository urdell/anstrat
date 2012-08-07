package com.anstrat.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;
import com.anstrat.server.util.Password;
import com.anstrat.server.util.Serialization;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;

/**
 * Contains methods for interacting with the database
 * @author eriter
 *
 */
public class DatabaseManager implements IDatabaseService {

	@Inject
	private Logger logger;
	
	@Inject
	private DatabaseContext context;
	
	@Override
	public User createUser(String password){
		Connection conn = null;
		PreparedStatement insertuser = null;
		ResultSet idnr = null;
		
		try{
			byte[] encryptedPassword = Password.generateDatabaseBlob(password);
			conn = context.getConnection();
			
			insertuser = conn.prepareStatement("INSERT INTO Users(id, password) VALUES(DEFAULT, ?) RETURNING id");
			insertuser.setBytes(1, encryptedPassword);
			idnr = insertuser.executeQuery();
			
			// Retrieve the auto generated user id
			idnr.next();
			long userID = idnr.getLong("id");
			
			return new User(userID, null, encryptedPassword);
		}
		catch(SQLException e){
			logger.exception(e, "SQLException when creating a user.");
		}
		finally{
			closeResSet(idnr);
			closeStmt(insertuser);
			closeConn(conn);
		}

		return null;
	}
	
	@Override
	public Map<Long, User> getUsers(long... userIDs){
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<Long, User> map = Maps.newHashMap();
		
		try{
			conn = context.getConnection();
			pst = conn.prepareStatement("SELECT * FROM USERS WHERE id = ANY(?)");
			pst.setArray(1, conn.createArrayOf("integer", Longs.asList(userIDs).toArray()));
			rs = pst.executeQuery();
			
			// Retrieve result
			while(rs.next()){
				long userID = rs.getLong("id");
				String displayName = rs.getString("displayName");
				byte[] encryptedPassword = rs.getBytes("password");
				
				map.put(userID, new User(userID, displayName, encryptedPassword));
			}
		}
		catch(SQLException e){
			logger.exception(e, "SQLException when retrieving users: [%s].", Joiner.on(", ").join(Longs.asList(userIDs)));
		}
		finally{
			closeResSet(rs);
			closeStmt(pst);
			closeConn(conn);
		}
		
		// An error occurred.
		return map;
	}

	@Override
	public Player[] getPlayers(long gameID) {
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet res = null;
		
		try{
			conn = context.getConnection();
			pst = conn.prepareStatement("SELECT userID, playerIndex, team, god, displayName FROM PlaysIn p JOIN Users u ON p.userID = u.id WHERE gameID = ?");
			
			pst.setLong(1, gameID);
			res = pst.executeQuery();
			
			// Retrieve player information
			List<Player> players = Lists.newArrayList();
			while(res.next()){
				long userID = res.getLong("userID");
				int playerIndex = res.getInt("playerIndex");
				int team = res.getInt("team");
				int god = res.getInt("god");
				String displayName = res.getString("displayName");
				
				players.add(new Player(userID, playerIndex, team, god, displayName));
			}
			
			return players.toArray(new Player[players.size()]);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			closeResSet(res);
			closeStmt(pst);
			closeConn(conn);
		}
		
		// An error occurred.
		return null;
	}
	
	public Command[] getCommands(long gameID, int greaterThanOrEqualToCommandNr){
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet res = null;
		
		try{
			conn = context.getConnection();
			pst = conn.prepareStatement("SELECT command FROM Commands WHERE gameID = ? AND commandNr >= ? ORDER BY commandNr ASC");
			pst.setLong(1, gameID);
			pst.setInt(2, greaterThanOrEqualToCommandNr);
			res = pst.executeQuery();
			
			// Get commands
			List<Command> commands = Lists.newArrayList();
			while(res.next()){
				Command command = Serialization.deserialize(res.getBytes("command"));
				commands.add(command);
			}
			
			return commands.toArray(new Command[commands.size()]);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			closeResSet(res);
			closeStmt(pst);
			closeConn(conn);
		}
		
		// An error occurred.
		return null;
	}
	
	@Override
	public Long createGame(GameSetup game){
		Connection conn = null;
		PreparedStatement pst = null;
		PreparedStatement insertPlayer = null;
		ResultSet idnr = null;
		
		try{
			conn = context.getConnection(false);
			
			// Create game
			pst = conn.prepareStatement("INSERT INTO Games(id, randomSeed, map) VALUES(DEFAULT, ?, ?) RETURNING id");
			pst.setLong(1, game.randomSeed);
			pst.setBytes(2, Serialization.serialize(game.map));
			
			idnr = pst.executeQuery();
			
			// Get generated gameID
			idnr.next();
			long gameID = idnr.getLong("id");

			// Create batch of player inserts
			 insertPlayer = conn.prepareStatement("INSERT INTO PlaysIn(gameID, userID, playerIndex, team, god) VALUES(?, ?, ?, ?, ?)");

			for(int i = 0; i < game.players.length; i++){
				GameSetup.Player player = game.players[i];
				insertPlayer.setLong(1, gameID);
				insertPlayer.setLong(2, player.userID);
				insertPlayer.setInt(3, i);
				insertPlayer.setInt(4, player.team);
				insertPlayer.setInt(5, player.god);
				insertPlayer.addBatch();
			}
			
			insertPlayer.executeBatch();
			conn.commit();
			
			return gameID;
		}
		catch(SQLException e){
			logger.exception(e, "SQLException when creating game.");
		}
		finally{
			closeResSet(idnr);
			closeStmt(pst);
			closeStmt(insertPlayer);
			closeConn(conn);
		}
		
		// An error occurred.
		return null;
	}
	
	@Override
	public DisplayNameChangeResponse setDisplayName(long userID, String name){
		Connection conn = null;
		PreparedStatement pst = null;
		
		try{
			conn = context.getConnection();
			pst = conn.prepareStatement("UPDATE Users SET displayName = ? WHERE id = ?");
			pst.setString(1, name);
			pst.setLong(2, userID);
			pst.executeUpdate();
			
			return DisplayNameChangeResponse.SUCCESS;
		}
		catch(SQLException e){
			// See documentation for psql error codes, http://www.postgresql.org/docs/9.1/static/errcodes-appendix.html
			// 23505 = unique constraint violation
			if(e.getSQLState().equals("23505")){
				return DisplayNameChangeResponse.FAIL_NAME_EXISTS;
			}
				
			logger.exception(e, "Unexpected SQLException when setting display name of '%d' to '%s'.", userID, name);
		}
		finally{
			closeStmt(pst);
			closeConn(conn);
		}
		
		return DisplayNameChangeResponse.FAIL_ERROR;
	}
	
	@Override
	public boolean createCommand(long gameID, int commandNr, Command command) {
		Connection conn = null;
		PreparedStatement insert = null;
		
		try{
			byte[] serializedCommand = Serialization.serialize(command);
			conn = context.getConnection();
			
			insert = conn.prepareStatement("INSERT INTO Commands(gameID, commandNr, command) VALUES(?, ?, ?)");
			insert.setLong(1, gameID);
			insert.setInt(2, commandNr);
			insert.setBytes(3, serializedCommand);
			insert.executeUpdate();
			
			return true;
		}
		catch(SQLException e){
			logger.exception(e, "SQLException when creating commad.");
		}
		finally{
			closeStmt(insert);
			closeConn(conn);
		}
		
		return false;
	}
	
	// Helpers
	
	/**
	 * Closes the Connection in a robust manner.
	 * @param conn The connection to close.
	 */
	private void closeConn(Connection conn){
		if(conn != null){
			try{
				conn.close();
			}
			catch (SQLException e) {
				logger.exception(e, "Could not close Connection.");
			}
			catch(Throwable t){
				logger.exception(t, "Unexpected exception on closing Connection.");
			}
		}
	}
	
	/**
	 * Closes the ResultSet in a robust manner.
	 * @param rs The ResultSet to close.
	 */
	public void closeResSet(ResultSet rs){
		if(rs != null){
			try{
				rs.close();
			}
			catch (SQLException e) {
				logger.exception(e, "Could not close ResultSet.");
			}
			catch(Throwable t){
				logger.exception(t, "Unexpected exception on closing ResultSet.");
			}
		}
	}
	
	/**
	 * Closes the Statement in a robust manner.
	 * Applicable for both Statements and PreparedStatements.
	 * @param pst The Statement to close.
	 */
	public void closeStmt(Statement pst){
		if(pst != null){
			try{
				pst.close();
			}
			catch (SQLException e) {
				logger.exception(e, "Could not close Statement.");
			}
			catch(Throwable t){
				logger.exception(t, "Unexpected exception on closing Statement.");
			}
		}
	}
}
