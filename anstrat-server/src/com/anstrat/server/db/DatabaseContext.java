package com.anstrat.server.db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseContext {
	
	private static final String FILENAME = "db.conf";
	
	private final String databasePassword;
	private final String databaseUser;
	private final String connectionString;
	
	public DatabaseContext(){
		// Load database settings from file
		Properties p = new Properties();
		
    	try{
    		p.load(new FileInputStream(FILENAME));
    		
    		String host = getProperty(p, "db.host");
    		String database = getProperty(p, "db.name");
    		int port = Integer.parseInt(getProperty(p, "db.port"));
    		String databaseURL = String.format("//%s:%d/%s", host, port, database);
    		
    		connectionString = String.format("jdbc:%s:%s", "postgresql", databaseURL);
    		databaseUser = getProperty(p, "db.user");
    		databasePassword = getProperty(p, "db.password");
    	} 
    	catch (Throwable t) {
    		throw new IllegalStateException(String.format("Failed to load database settings from %s: %s", FILENAME, t.getMessage()));
        }
	}
	
	private String getProperty(Properties p, String key){
		String value = p.getProperty(key);
		if(value == null) throw new IllegalArgumentException(String.format("Missing configuration value: %s", key));
		return value;
	}
	
	public Connection getConnection(){
		return getConnection(true);
	}
	
	public Connection getConnection(boolean autocommit){
		try {
			Class.forName("org.postgresql.Driver");
			
			Connection c = DriverManager.getConnection(connectionString, databaseUser, databasePassword);
			c.setAutoCommit(autocommit);
			return c;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
}
