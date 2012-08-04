package com.anstrat.server;

import java.net.InetSocketAddress;

public interface IClientEventListener {

	void clientConnected(InetSocketAddress address);
	void clientDisconnected(InetSocketAddress address);
	void clientAuthenticated(InetSocketAddress address, long userID);
}
