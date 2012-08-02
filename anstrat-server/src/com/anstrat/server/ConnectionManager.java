package com.anstrat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import com.anstrat.network.NetworkMessage;
import com.anstrat.server.util.Logger;

public class ConnectionManager implements IConnectionManager, ClientWorker.IClientWorkerCallback {

	private static final Logger logger = Logger.getGlobalLogger();
	private final ServerMessageHandler messageHandler;
	private final ConcurrentHashMap<InetSocketAddress, ClientWorker> connections;
	private final ConcurrentHashMap<Long, InetSocketAddress> authenticatedConnections;
	private final ConcurrentHashMap<InetSocketAddress, Long> addressToUserID;
	
	public ConnectionManager(){
		this.messageHandler = new ServerMessageHandler(this);
		this.connections = new ConcurrentHashMap<InetSocketAddress, ClientWorker>();
		this.authenticatedConnections = new ConcurrentHashMap<Long, InetSocketAddress>();
		this.addressToUserID = new ConcurrentHashMap<InetSocketAddress, Long>();
	}
	
	@Override
	public void connectionClosed(InetSocketAddress client, Throwable cause) {
		// Remove connection
		this.authenticatedConnections.remove(client);
		this.addressToUserID.remove(client);
		this.connections.remove(client);
		logger.info("%d clients online.", this.connections.size());
	}

	@Override
	public void sendMessage(InetSocketAddress address, NetworkMessage message) {
		ClientWorker worker = this.connections.get(address);
		
		if(worker != null){
			worker.sendMessage(message);
		}
		else{
			logger.info("Failed to send %s to %s, client is not connected", message.getCommand(), address);
		}
		
	}

	@Override
	public void sendMessage(long userID, NetworkMessage message) {
		InetSocketAddress client = this.authenticatedConnections.get(userID);
		
		if(client != null){
			sendMessage(client, message);
		}
		else{
			logger.info("Failed to send %s to user '%d', could not determine client address.", message.getCommand(), userID);
		}
	}

	@Override
	public void linkUserToAddress(long userID, InetSocketAddress address) {
		this.authenticatedConnections.put(userID, address);
		this.addressToUserID.remove(address);
		logger.info("Authenticated client %s as user '%d'.", address, userID);
	}

	@Override
	public void addConnection(Socket socket) {
		// Create a ClientWorker to handle the incoming request
		// TODO: Use a thread pool rather than creating a new thread for each request
		try{
			ClientWorker worker = new ClientWorker(socket, this, messageHandler);
			this.connections.put(worker.getClientAddress(), worker);
			
			int numClients = this.connections.size();
			logger.info("%s connected.", worker.getClientAddress());
			logger.info("%d client%s online.", numClients, numClients > 1 ? "s" : "");
			new Thread(worker).start();
		}
		catch(IOException ioe){
			logger.info("Failed to initialize connection to %s.", socket.getInetAddress());
		}
	}

	@Override
	public long getUserID(InetSocketAddress address) {
		Long id = addressToUserID.get(address);
		return id != null ? id : -1;
	}
}
