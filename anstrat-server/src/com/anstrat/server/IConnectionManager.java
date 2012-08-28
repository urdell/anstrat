package com.anstrat.server;

import java.net.InetSocketAddress;
import java.net.Socket;

import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.server.db.User;

public interface IConnectionManager {
	void addConnection(Socket socket);
	boolean sendMessage(InetSocketAddress address, NetworkMessage message);
	boolean sendMessage(long userID, NetworkMessage message);
	void linkUserToAddress(User user, InetSocketAddress address);
	
	/**
	 * @return the matching userID or <code>null</code> if none was found
	 */
	Long getUserID(InetSocketAddress address);
}
