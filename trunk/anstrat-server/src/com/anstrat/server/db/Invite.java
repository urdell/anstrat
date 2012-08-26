package com.anstrat.server.db;

import com.anstrat.network.protocol.GameOptions;


public class Invite {
	
	public enum Status {PENDING, ACCEPTED, REJECTED};
	
	public final long inviteID;
	public final long sender;
	public final long receiver;
	public final Status status;
	public final Long gameID;
	public final GameOptions options;
	
	public Invite(long inviteID, long sender, long receiver, Status status, Long gameID, GameOptions options) {
		this.inviteID = inviteID;
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
		this.gameID = gameID;
		this.options = options;
	}
}
