package com.anstrat.server.events;

import java.net.InetSocketAddress;

public class ClientAuthenticatedEvent {
	private InetSocketAddress client;
	private long userID;

	public ClientAuthenticatedEvent(InetSocketAddress client, long userID) {
		this.client = client;
		this.userID = userID;
	}

	public InetSocketAddress getClient() {
		return client;
	}
	
	public long getUserID(){
		return this.userID;
	}
}
