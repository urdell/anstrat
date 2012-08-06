package com.anstrat.server.db;

public class Player {
	
	public final long userID;
	public final int playerIndex;
	public final int team;
	public final int god;
	public final String displayName;
	
	public Player(long userID, int playerIndex, int team, int god, String displayName) {
		this.userID = userID;
		this.playerIndex = playerIndex;
		this.team = team;
		this.god = god;
		this.displayName = displayName;
	}
}
