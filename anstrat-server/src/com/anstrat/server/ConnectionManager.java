package com.anstrat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.server.events.ClientAuthenticatedEvent;
import com.anstrat.server.events.ClientConnectedEvent;
import com.anstrat.server.events.ClientDisconnectedEvent;
import com.anstrat.server.events.Event;
import com.anstrat.server.util.ClientWorkerFactory;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

public class ConnectionManager implements IConnectionManager {

	@Inject
	private Logger logger;
	
	@Inject
	private ClientWorkerFactory factory;
	
	private final Map<InetSocketAddress, ClientWorker> connections;
	private final BiMap<Long, InetSocketAddress> authenticatedConnections;
	
	public ConnectionManager(){
		this.connections = Collections.synchronizedMap(new HashMap<InetSocketAddress, ClientWorker>());
		BiMap<Long, InetSocketAddress> map = HashBiMap.create();
		authenticatedConnections = Maps.synchronizedBiMap(map);
		
		// Subscribe to events
		Event.register(this);
	}
	
	@Subscribe
	public void connectionClosed(ClientDisconnectedEvent event) {
		// Remove connection
		this.authenticatedConnections.remove(event.getClient()); // Does not necessarily exist in this map
		this.connections.remove(event.getClient());
		logger.info("%d clients online.", this.connections.size());
	}

	@Override
	public boolean sendMessage(InetSocketAddress address, NetworkMessage message) {
		ClientWorker worker = this.connections.get(address);
		
		if(worker != null){
			return worker.sendMessage(message);
		}
		else{
			logger.info("Could not send %s to %s, client is not connected", message.getCommand(), address);
		}
		
		return false;
	}

	@Override
	public boolean sendMessage(long userID, NetworkMessage message) {
		InetSocketAddress client = this.authenticatedConnections.get(userID);
		
		if(client != null){
			return sendMessage(client, message);
		}
		else{
			logger.info("Could not send %s to user '%d', could not determine client address.", message.getCommand(), userID);
		}
		
		return false;
	}

	@Override
	public void linkUserToAddress(long userID, InetSocketAddress address) {
		this.authenticatedConnections.forcePut(userID, address);
		logger.info("Authenticated %s as user '%d'.", address, userID);
		Event.post(new ClientAuthenticatedEvent(address, userID));
	}

	@Override
	public void addConnection(Socket socket) {
		// Create a ClientWorker to handle the incoming request
		// TODO: Use a thread pool rather than creating a new thread for each request
		try{
			//ClientWorker worker = new ClientWorker(socket, messageHandler);
			ClientWorker worker = factory.create(socket);
			this.connections.put(worker.getClientAddress(), worker);
			
			int numClients = this.connections.size();
			logger.info("%s connected.", worker.getClientAddress());
			logger.info("%d client%s online.", numClients, numClients > 1 ? "s" : "");
			
			new Thread(worker).start();
			
			Event.post(new ClientConnectedEvent(worker.getClientAddress()));
		}
		catch(IOException ioe){
			logger.info("Failed to initialize connection to %s.", socket.getInetAddress());
		}
	}

	@Override
	public Long getUserID(InetSocketAddress address) {
		return authenticatedConnections.inverse().get(address);
	}
}
