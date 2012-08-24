package com.anstrat.network.protocol;

import java.io.Serializable;


public class Invite implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Status {PENDING, ACCEPTED, REJECTED};
	
	public final long sender;
	public final long receiver;
	public final Status status;
	public final Long gameID;
	public final GameOptions options;
	
	public Invite(long sender, long receiver, Status status, Long gameID, GameOptions options) {
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
		this.gameID = gameID;
		this.options = options;
	}
}
