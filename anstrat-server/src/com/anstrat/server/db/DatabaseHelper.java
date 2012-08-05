package com.anstrat.server.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public final class DatabaseHelper {
	
	private static final String DATABASE_URL = "//127.0.0.1:5432/anstratdb";
	private static final String DATABASE_USER = "anstrat";
	private static final String DATABASE_PASSWORD = "Anstrat101lol!";
	
	public static Connection getConnection(boolean autocommit){
		try {
			Class.forName("org.postgresql.Driver");
			
			Connection c = DriverManager.getConnection(
					String.format("jdbc:%s:%s", "postgresql", DATABASE_URL),
					DATABASE_USER, DATABASE_PASSWORD);
			
			c.setAutoCommit(autocommit);
			return c;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Connection getConnection(){
		return getConnection(true);
	}
	
	/**
	 * Closes the Connection in a robust manner.
	 * @param conn The connection to close.
	 */
	public static void closeConn(java.sql.Connection conn){
		if(conn != null){
			try{
				conn.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Closes the ResultSet in a robust manner.
	 * @param rs The ResultSet to close.
	 */
	public static void closeResSet(ResultSet rs){
		if(rs != null){
			try{
				rs.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Closes the Statement in a robust manner.
	 * Applicable for both Statements and PreparedStatements.
	 * @param pst The Statement to close.
	 */
	public static void closeStmt(Statement pst){
		if(pst != null){
			try{
				pst.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static byte[] objectToByteArray(Serializable object){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			
			return baos.toByteArray();
			
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Connects to the PostgreSQL db and initializes its tables and data.
	 */
	public static void main(String[] args){
		DatabaseSchema.initializeDB();
	}
}
