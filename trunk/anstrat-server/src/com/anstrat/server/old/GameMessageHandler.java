package com.anstrat.server.old;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


import com.anstrat.command.Command;
import com.anstrat.command.EndTurnCommand;
import com.anstrat.network_old.NetworkMessage;
import com.anstrat.server.matchmaking.GameMatcher;
import com.anstrat.server.old.DatabaseHelper.DatabaseType;
import com.anstrat.server.util.Logger;

/**
 * All game-related NetworkMessage handling is delegated to this class.
 * @author jay
 *
 */
@Deprecated
public class GameMessageHandler {
	
	private MainServer server;
	private GameMatcher matcher;
	private static final Logger logger = Logger.getGlobalLogger();
	private static final long TIMELIMIT_MAX = 604800000l; // One week
	private static final long TIMELIMIT_MIN = 604800000l; // One week
	
	/**
	 * This constructor should always be used.
	 * @param server Required to handle some functionality and logging.
	 */
	public GameMessageHandler(MainServer server, GameMatcher matcher)
	{
		this.server = server;
		this.matcher = matcher;
	}
	
	/**
	 * Sent by clients to indicate they've finished their turn.
	 * @param gameId The id for the game in question.
	 * @param turnNo The turn's sequence number.
	 * @param commands The list of commands used in the turn.
	 * @param socket The socket the turn was received from.
	 * @param checkTurn true if whether the turn has expired already should be checked.
	 */
	public void endTurn(long gameId, int turnNo, Queue<Command> commands, User user, boolean checkTurn)
	{	
		int userPlayerId = getPlayerId(gameId, user.getUserId());
		
		if(userPlayerId != -1){
			// Check if the user's turn has already expired
			if(checkTurn){
				boolean ended = checkTurnEnded(gameId, user);
				logger.info("Checking turn ended for game '%d': ended=%s", gameId, ended);
			
				if(ended){
					PlayerSocket socket = server.getSocketForUser(user.getUsername());
					if(socket != null) socket.sendMessage(new NetworkMessage("TURN_EXPIRED"));
					return;
				}
			}
			
			// Create checksum
			int stateChecksum = turnNo == 1 ? 0 : DatabaseHelper.getTurnChecksum(gameId, turnNo - 1);
			stateChecksum += commands.hashCode();
			
			java.sql.Connection conn = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			ByteArrayOutputStream baos = null;
			
			try
			{
				conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
				pst = conn.prepareStatement("INSERT INTO Turns(gameID,userID,turnNo,timestamp,commands,stateChecksum) VALUES(?,?,?,?,?,?)");
				
				baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(commands);
				oos.flush();
				oos.close();
				
				pst.setLong(1, gameId);
				pst.setLong(2, user.getUserId());
				pst.setInt(3, turnNo);
				pst.setTimestamp(4, new Timestamp(new Date().getTime()));
				pst.setBytes(5, baos.toByteArray());
				pst.setInt(6, stateChecksum);
				
				pst.executeUpdate();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DatabaseHelper.closeResSet(rs);
				DatabaseHelper.closeStmt(pst);
				DatabaseHelper.closeConn(conn);
				try {
					baos.close();
				} catch (IOException e) {
					// Close enough.
				}
			}
		}
		else{
			logger.info("Unauthorized access attempt by "+user.getUsername()+": endTurn");
		}
	}
	
	/**
	 * Polls the server for its default map list.
	 * @param socket The socket the request came from.
	 */
	public void pollDefaultMaps(PlayerSocket socket)
	{
		java.sql.Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try
		{
			conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			
			pst = conn.prepareStatement("SELECT * FROM DefaultMaps");
			rs = pst.executeQuery();
			
			if(!rs.next())
			{
				socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "No maps were found."));
			}
			else
			{
				List<Serializable> defaultMaps = new ArrayList<Serializable>();
				
				do
				{
					Long mapId = rs.getLong("mapId");
					byte[] mapBytes = rs.getBytes("map");
					ByteArrayInputStream bais = new ByteArrayInputStream(mapBytes);
					ObjectInputStream ois = new ObjectInputStream(bais);
					com.anstrat.geography.Map map = (com.anstrat.geography.Map) ois.readObject();
					
					defaultMaps.add(mapId);
					defaultMaps.add(map);
					
					ois.close();
				}
				while(rs.next());
				
				socket.sendMessage(new NetworkMessage("DEFAULT_MAPS", defaultMaps));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseHelper.closeResSet(rs);
			DatabaseHelper.closeStmt(pst);
			DatabaseHelper.closeConn(conn);
		}
		
		socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "An unknown error occurred. Please try again later."));
	}
	
	public void hostCustomGame(long nonce, long timeLimit, String gameName, String password, com.anstrat.geography.Map map, PlayerSocket socket)
	{
		if(timeLimit < TIMELIMIT_MIN)
			socket.sendMessage(new NetworkMessage("GAME_HOST_FAILURE", (Long) nonce, "Time limit needs to be at least "+TIMELIMIT_MIN/1000+" seconds."));
		else if(timeLimit > TIMELIMIT_MAX)
			socket.sendMessage(new NetworkMessage("GAME_HOST_FAILURE", (Long) nonce, "Time limit cannot be longer than "+TIMELIMIT_MAX/1000+" seconds."));
		else
		{
			matcher.hostCustomGame(nonce, timeLimit, gameName, password, map, socket);
		}
	}
	
	public void hostDefaultGame(long nonce, long timeLimit, String gameName, String password, long mapId, PlayerSocket socket)
	{
		// TODO imp, low prio
	}
	
	public void hostDefaultRandomGame(long nonce, long timeLimit, String gameName, String password, PlayerSocket socket)
	{
		//TODO implement, low priority
	}
	
	public void joinGame(Long nonce, String gameName, String password, PlayerSocket socket)
	{
		matcher.joinGame(nonce, gameName, password, socket);
	}
	
	public void joinRandomGame(long nonce, long minTimeLimit, long maxTimeLimit, int accept_flags, PlayerSocket socket)
	{
		if(minTimeLimit < TIMELIMIT_MIN)
			socket.sendMessage(new NetworkMessage("GAME_HOST_FAILURE", (Long) nonce, "Time limit needs to be at least "+TIMELIMIT_MIN/1000+" seconds."));
		else if(maxTimeLimit > TIMELIMIT_MAX)
			socket.sendMessage(new NetworkMessage("GAME_HOST_FAILURE", (Long) nonce, "Time limit cannot be longer than "+TIMELIMIT_MAX/1000+" seconds."));
		else
		{
			matcher.joinRandomGame(nonce, minTimeLimit, maxTimeLimit, accept_flags, socket);
		}
	}
	
	public void hostRandomGame(int width, int height, long nonce, long timeLimit, String gameName, String password, PlayerSocket socket)
	{
		matcher.hostRandomGame(width, height, nonce, timeLimit, gameName, password, socket);
	}
	
	public void mapGenerated(String gameName, com.anstrat.geography.Map map, PlayerSocket socket)
	{
		matcher.mapGenerated(gameName, map);
	}
	
	/**
	 * Used to return data for players polling for new turns.
	 * @param gameId The id of the game polled.
	 * @param expectedTurn The sequence number of the first turn the client has not seen yet.
	 * @param socket The socket the request was received from.
	 */
	public void pollTurns(long gameId, int expectedTurn, PlayerSocket socket)
	{
		//server.logln("Polling turns from game '%d' starting from turn '%d'.", gameId, expectedTurn);
		User user = socket.getUser();
		
		// TODO: Currently does not check that the user requesting turns are actually a participant of the game
		// Can use getPlayerId(gameId, user.getUserId()) != -1 to check for that. 
		if(user != null){
			// Check if the user's turn has already expired
			// and if it has, inserts an EndTurnCommand
			boolean ended = checkTurnEnded(gameId, user);
			// server.logln("Checking turn ended for game '%d': ended=%s", gameId, ended);
			if(ended){
				if(socket != null) socket.sendMessage(new NetworkMessage("TURN_EXPIRED"));
			}
			
			long userID = user.getUserId();
			
			TurnList turn = getTurns(gameId, userID, expectedTurn);
			
			if(turn == null){
				logger.error("An error occurred while polling turns >= %d from game %d.", expectedTurn, gameId);
			}
			else if(!turn.turns.isEmpty()){
				socket.sendMessage(new NetworkMessage("TURNS", gameId, expectedTurn, turn.timestamp, turn.stateChecksum, (Serializable)turn.turns));
			}
		}
		else{
			logger.info("Unauthorized access attempt by "+socket.getConnection()+": pollTurn");
		}
	}
	
	/**
	 * Used to fetch turns from the database.
	 * @param gameID The id of the game to check for.
	 * @param userID The id of the user requesting turns.
	 * @param startTurn The number of the first expected turn. (There is no need to return turns the client has already seen.)
	 * @return The {@link TurnList} found, or <code>null</code>
	 */
	private TurnList getTurns(long gameID, long userID, int startTurn){
		java.sql.Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try
		{
			conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			pst = conn.prepareStatement("SELECT commands, timestamp, stateChecksum FROM Turns WHERE gameId = ? AND turnNo >= ? ORDER BY turnNo");
			pst.setLong(1, gameID);
			pst.setInt(2, startTurn);
			
			rs = pst.executeQuery();
			
			Queue<Queue<Command>> turns = new LinkedList<Queue<Command>>();
			
			// TODO: error handling
			Timestamp lastTimestamp = null;
			int lastStateChecksum = 0;
			
			while(rs.next()){
				
				lastTimestamp = rs.getTimestamp("timestamp");
				lastStateChecksum = rs.getInt("stateChecksum");
				byte[] commands = rs.getBytes("commands");
				ByteArrayInputStream bais = new ByteArrayInputStream(commands);
				ObjectInputStream ois = new ObjectInputStream(bais);
				@SuppressWarnings("unchecked")
				Queue<Command> turn = (Queue<Command>) ois.readObject();
				turns.add(turn);
				
				ois.close();
			}
			
			//TODO output thwarted
			//server.logln("Polling turns >= %d for gameID = %d, found %d turns.", startTurn, gameID, turns.size());
			
			return new TurnList(lastStateChecksum, turns, lastTimestamp);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
			DatabaseHelper.closeResSet(rs);
			DatabaseHelper.closeStmt(pst);
			DatabaseHelper.closeConn(conn);
		}
		
		return null;
	}
	
	
	/**
	 * Queries the database for the user's playerID in the given game.
	 * @param gameId the id of the game to check
	 * @param userId the id of the user to check
	 * @return the user's playerId in the given game, or -1 if he's not a participant in the game.
	 */
	public int getPlayerId(long gameId, long userId){
		
		java.sql.Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			
			pst = conn.prepareStatement("SELECT playerId FROM PlaysIn WHERE gameId = ? AND userId = ?");
			pst.setLong(1, gameId);
			pst.setLong(2, userId);
			
			rs = pst.executeQuery();
			
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseHelper.closeResSet(rs);
			DatabaseHelper.closeStmt(pst);
			DatabaseHelper.closeConn(conn);
		}
		
		return -1;
	}
	
	public void removeRequest(long nonce, PlayerSocket ps)
	{
		matcher.removeRequest(nonce, ps);
	}
	
	/**
	 * Checks if the turn has ended, if so, returns true and inserts<br>
	 * an EndTurnCommand for the given gameId.
	 * @param gameID
	 * @param user
	 * @return
	 */
	private boolean checkTurnEnded(long gameID, User user){
		java.sql.Connection conn = null;
		PreparedStatement turnPst = null;
		PreparedStatement gamePst = null;
		ResultSet turnRs = null;
		ResultSet gameRs = null;
		
		try {
			Timestamp timestamp = null;	// Will be set below
			int turnNo = 0;				// Will keep it's value 0 if not set below
			long turnLimit;				// Will be set below
			
			conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			
			// Query Turns for the last turn of this game
			turnPst = conn.prepareStatement("SELECT t.timestamp, turnNo, turnlimit FROM Turns t, Games WHERE id = gameId AND gameId = ? ORDER BY turnNo DESC");
			turnPst.setMaxRows(1);
			turnPst.setLong(1, gameID);
			turnRs = turnPst.executeQuery();
			
			if(turnRs.next()){
				// At least one turn for this game exists
				timestamp = turnRs.getTimestamp("timestamp");
				turnNo = turnRs.getInt("turnNo");
				turnLimit = turnRs.getLong("turnlimit");
			}
			else{
				// No turns for this game exists yet, check game start timestamp instead
				gamePst = conn.prepareStatement("SELECT timestamp, turnlimit FROM Games WHERE id = ?");
				gamePst.setLong(1, gameID);
				gameRs = gamePst.executeQuery();
				
				if(gameRs.next()){
					timestamp = gameRs.getTimestamp("timestamp");
					turnLimit = gameRs.getLong("turnlimit");
				}
				else{
					logger.info("Attempted to check turn ended for a game that doesn't exist.");
					return false;
				}
			}
			
			// Check if turn has expired
			Date now = new Date();
			Date turnEnds = new Date(timestamp.getTime() + turnLimit);
			
			if(now.after(turnEnds)){
				// Turn has expired, insert an EndTurnCommand
				Queue<Command> commands = new LinkedList<Command>();
				commands.add(new EndTurnCommand(user.getUserId()));
				endTurn(gameID, turnNo + 1, commands, user, false);
				
				return true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DatabaseHelper.closeResSet(turnRs);
			DatabaseHelper.closeStmt(turnPst);
			DatabaseHelper.closeResSet(gameRs);
			DatabaseHelper.closeStmt(gamePst);
			DatabaseHelper.closeConn(conn);
		}
		
		return false;
	}
	
	private static class TurnList {
		
		public final int stateChecksum;
		public final Queue<Queue<Command>> turns;
		public final Timestamp timestamp;
		
		public TurnList(int stateChecksum, Queue<Queue<Command>> turns, Timestamp timestamp){
			this.stateChecksum = stateChecksum;
			this.turns = turns;
			this.timestamp = timestamp;
		}
	}
}