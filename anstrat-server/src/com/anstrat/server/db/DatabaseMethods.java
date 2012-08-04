package com.anstrat.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.anstrat.network.protocol.GameSetup;
import com.anstrat.server.util.Password;

/**
 * Contains methods for interacting with the database
 * @author eriter
 *
 */
public class DatabaseMethods {

	public static User createUser(String password){
		Connection conn = null;
		PreparedStatement insertuser = null;
		Statement seqnr = null;
		ResultSet idnr = null;
		
		try{
			byte[] encryptedPassword = Password.generateDatabaseBlob(password);
			
			conn = DatabaseHelper.getConnection();
			
			insertuser = conn.prepareStatement("INSERT INTO Users(id, password) VALUES(DEFAULT, ?) RETURNING id");
			insertuser.setBytes(1, encryptedPassword);
			idnr = insertuser.executeQuery();
			
			// Retrieve the auto generated user id
			idnr.next();
			long userID = idnr.getLong("id");
			
			return new User(userID, null, encryptedPassword);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			DatabaseHelper.closeStmt(insertuser);
			DatabaseHelper.closeResSet(idnr);
			DatabaseHelper.closeStmt(seqnr);
			DatabaseHelper.closeConn(conn);
		}

		return null;
	}
	
	public static Map<Long, User> getUsers(Long... userIDs){
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		HashMap<Long, User> map = new HashMap<Long, User>();
		
		try{
			conn = DatabaseHelper.getConnection();
			pst = conn.prepareStatement("SELECT * FROM USERS WHERE id = ANY(?)");
			pst.setArray(1, conn.createArrayOf("integer", userIDs));
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
			e.printStackTrace();
		}
		finally{
			DatabaseHelper.closeResSet(rs);
			DatabaseHelper.closeStmt(pst);
			DatabaseHelper.closeConn(conn);
		}
		
		// An error occurred.
		return map;
	}
	
	public static long createGame(GameSetup game){
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet idnr = null;
		
		try{
			conn = DatabaseHelper.getConnection(false);
			
			// Create game
			pst = conn.prepareStatement("INSERT INTO Games(id, randomSeed, map) VALUES(DEFAULT, ?, ?) RETURNING id");
			pst.setLong(1, game.randomSeed);
			pst.setBytes(2, DatabaseHelper.objectToByteArray(game.map));
			
			idnr = pst.executeQuery();
			idnr.next();
			
			long gameID = idnr.getLong("id");
			
			// Add players to game
			for(int i = 0; i < game.players.length; i++){
				GameSetup.Player player = game.players[i];
				
				PreparedStatement insertPlayer = conn.prepareStatement("INSERT INTO PlaysIn(gameID, userID, playerIndex, team, god) VALUES(?, ?, ?, ?, ?)");
				insertPlayer.setLong(1, gameID);
				insertPlayer.setLong(2, player.userID);
				insertPlayer.setInt(3, i);
				insertPlayer.setInt(4, player.team);
				insertPlayer.setInt(5, player.god);
				
				insertPlayer.executeUpdate();
				insertPlayer.close();
			}
			
			conn.commit();
			return gameID;
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			DatabaseHelper.closeResSet(idnr);
			DatabaseHelper.closeStmt(pst);
			DatabaseHelper.closeConn(conn);
		}
		
		// An error occurred.
		return -1;
	}
	
	public enum DisplayNameChangeResponse {SUCCESS, FAIL_NAME_EXISTS, FAIL_ERROR}
	
	public static DisplayNameChangeResponse setDisplayName(long userID, String name){
		Connection conn = null;
		PreparedStatement pst = null;
		
		try{
			conn = DatabaseHelper.getConnection();
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
			else{
				// Something unexpected went wrong
				e.printStackTrace();
			}
		}
		finally{
			DatabaseHelper.closeStmt(pst);
			DatabaseHelper.closeConn(conn);
		}
		
		return DisplayNameChangeResponse.FAIL_ERROR;
	}
}
