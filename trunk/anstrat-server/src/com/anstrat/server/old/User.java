package com.anstrat.server.old;

/**
 * Container class for user-related information.
 * @author jay
 *
 */
@Deprecated
public class User {
	
	private String username, displayedName;
	private byte[] encryptedPassword; // the encrypted password with the salt appended
	private long userId;

	/**
	 * Default constructor.
	 * @param userId The user's sequence (id) number.
	 * @param username The user's username.
	 * @param displayedName The user's displayed name.
	 */
	public User(long userId, String username, String displayedName, byte[] encryptedPassword) {
		this.userId = userId;
		this.username = username;
		this.displayedName = displayedName;
		this.encryptedPassword = encryptedPassword;
	}

	public String getUsername() {
		return username;
	}
	
	public String getDisplayedName()
	{
		return displayedName;
	}
	
	public long getUserId()
	{
		return userId;
	}
	
	public byte[] getEncryptedPassword()
	{
		return encryptedPassword;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof User)
			return ((User) o).getUsername().equalsIgnoreCase(username);
		return false;
	}
}