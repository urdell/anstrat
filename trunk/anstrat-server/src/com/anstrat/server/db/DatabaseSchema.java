package com.anstrat.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.anstrat.server.util.DependencyInjector;
import com.anstrat.server.util.DependencyInjector.Inject;


public class DatabaseSchema {
	
	@Inject
	private DatabaseContext context;
	
	public boolean create(){
		Statement s = null;
		Connection c = null;
		
		try {
			c = context.getConnection();
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
			
			// Commands
			s.executeUpdate("CREATE TABLE Commands(" +
					"gameID BIGSERIAL, " +
					"commandNr INT, " +
					"command BYTEA, " +
					"FOREIGN KEY(gameID) REFERENCES Games(id), " +
					"PRIMARY KEY(gameID, commandNr))");
			
			// Invites
			s.executeUpdate("CREATE TYPE InviteStatus AS ENUM('PENDING', 'ACCEPTED', 'REJECTED')");
			s.executeUpdate("CREATE TABLE Invites(" +
					"id BIGSERIAL PRIMARY KEY, " +
					"senderID BIGSERIAL, " +
					"receiverID BIGSERIAL, " +
					"gameOptions BYTEA, " +
					"status VARCHAR(50), " +
					"gameID BIGSERIAL, " +
					"FOREIGN KEY(senderID) REFERENCES Users(id), " +
					"FOREIGN KEY(receiverID) REFERENCES Users(id))");
			
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
			if(c != null){
				try{
					c.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public void seed(){
		
	}
	
	public void drop(){
		Statement s = null;
		Connection c = null;
		
		try {
			c = context.getConnection();
			s = c.createStatement();
			s.executeUpdate("DROP TABLE IF EXISTS PlaysIn CASCADE");
			s.executeUpdate("DROP TABLE IF EXISTS Turns CASCADE");
			s.executeUpdate("DROP TABLE IF EXISTS Games CASCADE");
			s.executeUpdate("DROP TABLE IF EXISTS Users CASCADE");
			s.executeUpdate("DROP TABLE IF EXISTS DefaultMaps CASCADE");
			s.executeUpdate("DROP TABLE IF EXISTS Commands CASCADE");
			s.executeUpdate("DROP TABLE IF EXISTS Invites CASCADE");
			s.executeUpdate("DROP TYPE IF EXISTS InviteStatus");
			s.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if(c != null){
				try{
					c.close();
				} catch (SQLException e){
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Connects to the PostgreSQL db and initializes its tables and data.
	 */
	public static void main(String[] args){
		DependencyInjector injector = new DependencyInjector(DatabaseSchema.class.getPackage().getName());
		injector.bind(DatabaseContext.class, DatabaseContext.class);
		
		// Completely resets the database
		DatabaseSchema schema = injector.get(DatabaseSchema.class);
		schema.drop();
		schema.create();
		schema.seed();
	}
}
