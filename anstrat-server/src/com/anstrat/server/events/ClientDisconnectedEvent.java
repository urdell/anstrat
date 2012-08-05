package com.anstrat.server.events;

import java.net.InetSocketAddress;

public class ClientDisconnectedEvent {
	private InetSocketAddress client;
	private Throwable cause;

	public ClientDisconnectedEvent(InetSocketAddress client) {
		this.client = client;
	}

	public ClientDisconnectedEvent(InetSocketAddress client, Throwable cause){
		this(client);
		this.cause = cause;
	}
	
	public InetSocketAddress getClient() {
		return client;
	}
	
	/**
	 * @return the cause of the disconnection, can be <code>null</code>.
	 */
	public Throwable getCause(){
		return this.cause;
	}
}
