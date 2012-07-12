package com.anstrat.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchema {
	
	public static void initializeDB(){
		drop();
		create();
		seed();
	}
	
	public static boolean create(){
		Statement s = null;
		Connection c = null;
		
		try {
			c = DatabaseHelper.getConnection();
			s = c.createStatement();
			
			// Games
			s.executeUpdate("CREATE TABLE Games(" +
					"id BIGSERIAL PRIMARY KEY, " +
					"initialState BYTEA, " +
					"randomSeed BIGINT, " +
					"turnLimit BIGINT, " +
					"timestamp TIMESTAMP)");
			
			// Users
			s.executeUpdate("CREATE TABLE Users(" +
					"id BIGSERIAL PRIMARY KEY, " +
					"username VARCHAR(20) UNIQUE, " +
					"displayedName VARCHAR(20) UNIQUE, " +
					"password BYTEA)");	// password + salt
			
			// PlaysIn
			s.executeUpdate("CREATE TABLE PlaysIn(" +
					"gameID BIGINT, " +
					"userID BIGINT, " +
					"playerId INT, " +
					"PRIMARY KEY (gameID, userID), " +
					"FOREIGN KEY(gameID) REFERENCES Games(id), " +
					"FOREIGN KEY(userID) REFERENCES Users(id))");
			
			// Turns
			s.executeUpdate("CREATE TABLE Turns(" +
					"gameID BIGINT, " +
					"userID BIGINT, " +
					"turnNo INT, " +
					"timestamp TIMESTAMP, " +
					"commands BYTEA, " +
					"stateChecksum INT," +
					"PRIMARY KEY (gameId, userId, turnNo), " +
					"FOREIGN KEY(gameID) REFERENCES Games(id), " +
					"FOREIGN KEY(userID) REFERENCES Users(id))");
			
			// Default maps
			s.executeUpdate("CREATE TABLE DefaultMaps(" +
					"mapID BIGINT PRIMARY KEY, " +
					"map BYTEA)");
			
			s.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally{
			DatabaseHelper.closeStmt(s);
			DatabaseHelper.closeConn(c);
		}
	}
	
	public static boolean drop(){
		Statement s = null;
		Connection c = null;
		
		try {
			c = DatabaseHelper.getConnection();
			s = c.createStatement();
			s.executeUpdate("DROP TABLE IF EXISTS PlaysIn");
			s.executeUpdate("DROP TABLE IF EXISTS Turns");
			s.executeUpdate("DROP TABLE IF EXISTS Games");
			s.executeUpdate("DROP TABLE IF EXISTS Users");
			s.executeUpdate("DROP TABLE IF EXISTS DefaultMaps");
			s.close();
			
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally{
			DatabaseHelper.closeStmt(s);
			DatabaseHelper.closeConn(c);
		}
	}
	
	public static void seed(){
		// User accounts
		String[] names = {"erik", "johnny", "kalle", "tomas", "andreas", "anton"};
		
		for(String name : names){
			String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
			DatabaseHelper.createUser(name, capitalizedName + "1", "Viking" + capitalizedName);
		}
	}
}
