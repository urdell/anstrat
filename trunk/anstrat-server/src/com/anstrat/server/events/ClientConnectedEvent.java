package com.anstrat.server.events;

import java.net.InetSocketAddress;

public class ClientConnectedEvent {
	private InetSocketAddress client;

	public ClientConnectedEvent(InetSocketAddress client) {
		this.client = client;
	}

	public InetSocketAddress getClient() {
		return client;
	}
}
