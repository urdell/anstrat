package com.anstrat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.anstrat.server.util.Logger;

/**
 * The main server class.
 * Handles connection creation and removal.
 * @author jay
 *
 */
public class MainServer {
	
	private static final Logger logger = Logger.getGlobalLogger();
	private static final int DEFAULT_PORT = 25406;
	
	private final ConnectionManager connectionManager;
	
	/**
	 * @param args first argument will be treated as the port to listen to.
	 */
	public static void main(String[] args){
		MainServer ms = new MainServer();
		ms.listen(ms.choosePort(args));
	}
	
	private MainServer(){
		this.connectionManager = new ConnectionManager();
		
	}
	
	private int choosePort(String[] args){
		if(args.length == 0){
			logger.info("No port specified, using default port %d.", DEFAULT_PORT);
			return DEFAULT_PORT;
		}
		else{
			try{
				return Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e){
				throw new IllegalArgumentException(String.format("Given port '%s' is not a integer in range 0-65535.", args[0]));
			}
		}
	}

	public void listen(int port){
		ServerSocket incomingConnections = null;
		
		try{
			incomingConnections = new ServerSocket(port);
			logger.info("Started listening on port %d.", port);
		}
		catch(IOException ioe){
			logger.error("Couldn't create listener socket on port %d.", port);
		}	
		
		while(true){
			
			try {
				Socket socket = incomingConnections.accept();
				connectionManager.addConnection(socket);
			}
			catch(IOException ioe){
				logger.info("Failed to accept incoming client socket.");
			}
		}
	}
}