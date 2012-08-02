package com.anstrat.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
			conn.setAutoCommit(false);
			
			insertuser = conn.prepareStatement("INSERT INTO Users(password) VALUES(?)");
	
			insertuser.setBytes(1, encryptedPassword);
			insertuser.executeUpdate();
			
			// Retrieve the auto generated user id
			seqnr = conn.createStatement();
			idnr = seqnr.executeQuery("SELECT last_value FROM Users_id_seq");
			idnr.next();
			
			conn.commit();
			return new User(idnr.getLong(1), null, encryptedPassword);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		finally{
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			DatabaseHelper.closeStmt(insertuser);
			DatabaseHelper.closeResSet(idnr);
			DatabaseHelper.closeStmt(seqnr);
			DatabaseHelper.closeConn(conn);
		}

		return null;
	}
	
	public static User getUser(long userID){
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try{
			conn = DatabaseHelper.getConnection();
			pst = conn.prepareStatement("SELECT * FROM USERS WHERE id = ?");
			pst.setLong(1, userID);
			
			rs = pst.executeQuery();
			
			if(rs.next()){
				long dbid = rs.getLong("id");
				String dbdisplayedName = rs.getString("displayName");
				byte[] encryptedPassword = rs.getBytes("password");
				
				return new User(dbid, dbdisplayedName, encryptedPassword);
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
		return null;
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
