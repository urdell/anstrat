package com.anstrat.server.db;

/**
 * Container class for user-related information.
 * @author jay
 *
 */
public class User {
	
	private String displayedName;
	private byte[] encryptedPassword; // the encrypted password with the salt appended
	private long userID;

	public User(long userID, String displayedName, byte[] encryptedPassword) {
		this.userID = userID;
		this.displayedName = displayedName;
		this.encryptedPassword = encryptedPassword;
	}
	
	public String getDisplayedName(){
		return displayedName;
	}
	
	public long getUserID(){
		return userID;
	}
	
	public byte[] getEncryptedPassword(){
		return encryptedPassword;
	}
	
	@Override
	public boolean equals(Object o){
		if(o != null && o instanceof User)
			return ((User) o).userID == userID;
		return false;
	}
}
