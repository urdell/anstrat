package com.anstrat.server.db;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import com.anstrat.server.old.User;
import com.anstrat.server.util.Password;

/**
 * First draft of something that saves something to the database.
 * @author eriter
 *
 */
public abstract class DatabaseHelper {

	public enum DatabaseType {
		SQLite("sqlite", "org.sqlite.JDBC"), 
		PostgreSQL("postgresql", "org.postgresql.Driver");
		
		public final String handle, classpath;
		private DatabaseType(String handle, String classpath){
			this.handle = handle;
			this.classpath = classpath;
		}
	}
	
	public static int getTurnChecksum(long gameID, int turnNo)
	{
		Connection conn = null;
		PreparedStatement stm = null;
		ResultSet result = null;
		
		try
		{
			conn = connect(DatabaseType.PostgreSQL);
			stm = conn.prepareStatement("SELECT stateChecksum FROM Turns WHERE turnNo = ? AND gameId = ?");
			stm.setInt(1, turnNo);
			stm.setLong(2, gameID);
			
			result = stm.executeQuery();
			result.next();
			return result.getInt(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeResSet(result);
			closeStmt(stm);
			closeConn(conn);
		}

		return -1;
	}
	
	/**
	 * Attempts to create a user in the database.
	 * @param username The username to be inserted.
	 * @param password The password to be inserted (will be salted and hashed).
	 * @param displayedName The displayedName to be inserted.
	 * @return the newly created user, or null if something went wrong.
	 */
	public static User createUser(String username, String password, String displayedName)
	{
		Connection conn = null;
		PreparedStatement insertuser = null;
		Statement seqnr = null;
		ResultSet idnr = null;
		
		try
		{
			byte[] encryptedPassword = Password.generateDatabaseBlob(password);
			
			conn = connect(DatabaseType.PostgreSQL);
			conn.setAutoCommit(false);
			
			insertuser = conn.prepareStatement("INSERT INTO Users(username,password,displayedName) VALUES(?,?,?)");
			insertuser.setString(1, username.toLowerCase(Locale.ENGLISH));
			insertuser.setBytes(2, encryptedPassword);
			insertuser.setString(3, displayedName);
			insertuser.executeUpdate();
			
			closeStmt(insertuser);
			
			seqnr = conn.createStatement();
			idnr = seqnr.executeQuery("SELECT last_value FROM Users_id_seq");
			idnr.next();
			
			conn.commit();
			
			return new User(idnr.getLong(1), username, displayedName, encryptedPassword);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			closeResSet(idnr);
			closeStmt(seqnr);
			closeConn(conn);
		}

		return null;
	}
	
	/**
	 * Attempts to create a game in the database.
	 * @param players List of users to be used.
	 * @param numPlayers Number of participants in the game.
	 * @param timeLimit Time limit to be used.
	 * @param randSeed Random seed to be used.
	 * @return List with gameInfo if successful, otherwise null.
	 */
	public static List<Serializable> createGame(com.anstrat.geography.Map map, User[] players, int numPlayers, long timeLimit, long randSeed){
		java.sql.Connection conn = null;
		PreparedStatement pst = null;
		Statement seqnr = null;
		ResultSet idnr = null;
		ByteArrayOutputStream baos = null;
		
		try{
			conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			conn.setAutoCommit(false);
			
			baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(map);
			oos.flush();
			oos.close();
			
			//TODO init proper state
			pst = conn.prepareStatement("INSERT INTO Games(initialState, randomSeed, turnLimit, timestamp) VALUES(?,?,?,?)");
			pst.setBytes(1, baos.toByteArray());
			pst.setLong(2, randSeed);
			pst.setLong(3, timeLimit);
			pst.setTimestamp(4, new Timestamp(new Date().getTime()));
			
			pst.executeUpdate();
			
			DatabaseHelper.closeStmt(pst);
			
			// Get generated gameID
			seqnr = conn.createStatement();
			idnr = seqnr.executeQuery("SELECT last_value FROM Games_id_seq");
			idnr.next();
			
			conn.commit();
			
			long gameId = idnr.getLong(1);
			
			List<Serializable> gameInfo = new ArrayList<Serializable>();
			
			gameInfo.add(gameId);
			gameInfo.add(randSeed);
			gameInfo.add(timeLimit);
			
			pst = conn.prepareStatement("INSERT INTO PlaysIn(gameId, userId, playerId) VALUES(?,?,?)");
			
			// Add participants
			for(int i = 0; i < numPlayers; i++){
				long userId = players[i].getUserId();
				
				gameInfo.add(userId);
				gameInfo.add(players[i].getDisplayedName());
				gameInfo.add(i);	// playerID
				
				pst.setLong(1, gameId);
				pst.setLong(2, userId);
				pst.setInt(3, i);
				
				pst.executeUpdate();
				conn.commit();
			}
			
			return gameInfo;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try
			{
				baos.close();
			}
			catch(Exception e)
			{
				// Close enough.
			}
			
			DatabaseHelper.closeStmt(pst);
			DatabaseHelper.closeResSet(idnr);
			DatabaseHelper.closeStmt(seqnr);
			DatabaseHelper.closeConn(conn);
		}
		
		return null;
	}
	
	private static final String DATABASE_URL = "//127.0.0.1:5432/anstratdb";
	//private static final String DATABASE_URL = "//129.16.21.61:5432/anstratdb";
	private static final String DATABASE_USER = "anstrat";
	private static final String DATABASE_PASSWORD = "Anstrat101lol!";
	
	/**
	 * Returns a connection for the databasetype given.
	 * @param type The DatabaseType to be used.
	 * @return The connection, or null in case of failure.
	 */
	public static Connection connect(DatabaseType type){
		try {
			Class.forName(type.classpath);
			Connection con = DriverManager.getConnection(
					String.format("jdbc:%s:%s", type.handle, DATABASE_URL),
					DATABASE_USER, DATABASE_PASSWORD);
			
			return con;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Checks whether an username is already taken.
	 * @param username The username to be checked.
	 * @return Whether the username was taken.
	 */
	public static boolean usernameTaken(String username)
	{
		java.sql.Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try
		{
			conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			
			pst = conn.prepareStatement("SELECT * FROM USERS WHERE username = ?");
			// Should conistently use lower case usernames on the server side.
			pst.setString(1, username.toLowerCase(Locale.ENGLISH));
			
			rs = pst.executeQuery();
			
			if(rs.next())
				return true;
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
		
		return false;
	}
	
	/**
	 * Checks whether an displayedName is already taken.
	 * @param username The displayedName to be checked.
	 * @return Whether the displayedName was taken.
	 */
	public static boolean displayednameTaken(String displayedName)
	{
		java.sql.Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try
		{
			conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			
			pst = conn.prepareStatement("SELECT * FROM USERS WHERE displayedName = ?");
			pst.setString(1, displayedName);
			
			rs = pst.executeQuery();
			
			if(rs.next())
				return true;
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
		
		return false;
	}
	
	/**
	 * Attempts to load an user with the given username from the database.
	 * @param username The user's supposed username.
	 * @return An User if successful, null otherwise.
	 */
	public static User getUser(String username)
	{
		java.sql.Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try
		{
			conn = DatabaseHelper.connect(DatabaseType.PostgreSQL);
			
			pst = conn.prepareStatement("SELECT * FROM USERS WHERE username = ?");
			pst.setString(1, username.toLowerCase(Locale.ENGLISH));
			
			rs = pst.executeQuery();
			
			if(rs.next())
			{
				long dbid = rs.getLong("id");
				String dbusername = rs.getString("username");
				String dbdisplayedName = rs.getString("displayedName");
				byte[] encryptedPassword = rs.getBytes("password");
				
				return new User(dbid, dbusername, dbdisplayedName, encryptedPassword);
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
		
		// An error occurred.
		return null;
	}
	
	/**
	 * Closes the Connection in a robust manner.
	 * @param conn The connection to close.
	 */
	public static void closeConn(java.sql.Connection conn)
	{
		if(conn!=null)
		{
			try
			{
				conn.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Closes the ResultSet in a robust manner.
	 * @param rs The ResultSet to close.
	 */
	public static void closeResSet(ResultSet rs)
	{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Closes the Statement in a robust manner.
	 * Applicable for both Statements and PreparedStatements.
	 * @param pst The Statement to close.
	 */
	public static void closeStmt(Statement pst)
	{
		if(pst!=null)
		{
			try
			{
				pst.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	static Connection getConnection(){
		return connect(DatabaseType.PostgreSQL);
	}
	
	/**
	 * Connects to the PostgreSQL db and initializes its tables and data.
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException{	
		DatabaseSchema.initializeDB();
	}
}
