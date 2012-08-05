package com.anstrat.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public final class DatabaseSchema {
	
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
					"randomSeed BIGINT, " +
					"map BYTEA, " +
					"createdAt TIMESTAMP DEFAULT (now() AT TIME ZONE 'UTC'))");
			
			// Users
			s.executeUpdate("CREATE TABLE Users(" +
					"id BIGSERIAL PRIMARY KEY, " +
					"displayName VARCHAR(20) UNIQUE, " + 	// Can be null
					"password BYTEA, " +					// password + salt
					"createdAt TIMESTAMP DEFAULT (now() AT TIME ZONE 'UTC'))");						
			
			// PlaysIn
			s.executeUpdate("CREATE TABLE PlaysIn(" +
					"gameID BIGSERIAL, " +
					"userID BIGSERIAL, " +
					"playerIndex INT, " +
					"team INT, " +
					"god INT, " +
					"PRIMARY KEY (gameID, userID), " +
					"FOREIGN KEY(gameID) REFERENCES Games(id), " +
					"FOREIGN KEY(userID) REFERENCES Users(id))");
			
			// Turns
			/*
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
			*/
			// Default maps
			/*
			s.executeUpdate("CREATE TABLE DefaultMaps(" +
					"mapID BIGINT PRIMARY KEY, " +
					"map BYTEA)");
			*/
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
}
