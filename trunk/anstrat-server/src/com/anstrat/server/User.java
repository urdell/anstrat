package com.anstrat.server;

/**
 * Container class for user-related information.
 * @author jay
 *
 */
public class User {
	
	private String username, displayedName;
	private long userId;

	/**
	 * Default constructor.
	 * @param userId The user's sequence (id) number.
	 * @param username The user's username.
	 * @param displayedName The user's displayed name.
	 */
	public User(long userId, String username, String displayedName) {
		this.userId = userId;
		this.username = username;
		this.displayedName = displayedName;
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
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof User)
			return ((User) o).getUsername().equalsIgnoreCase(username);
		return false;
	}
}