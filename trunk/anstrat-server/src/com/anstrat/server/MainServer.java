package com.anstrat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.anstrat.server.db.DatabaseContext;
import com.anstrat.server.db.DatabaseManager;
import com.anstrat.server.db.IDatabaseService;
import com.anstrat.server.messageHandlers.ServerMessageHandler;
import com.anstrat.server.util.ClientWorkerFactory;
import com.anstrat.server.util.DependencyInjector;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;

public class MainServer {
	
	private static final int DEFAULT_PORT = 25406;
	
	@Inject
	private Logger logger;
	
	@Inject
	private IConnectionManager connectionManager;
	
	/**
	 * @param args first argument will be treated as the port to listen to.
	 * @throws IOException 
	 */
	public static void main(String[] args){
		DependencyInjector injector = new DependencyInjector(MainServer.class.getPackage().getName());
		
		injector.bind(IConnectionManager.class, ConnectionManager.class);
		injector.bind(ServerMessageHandler.class, ServerMessageHandler.class);
		injector.bind(Logger.class, Logger.class);
		injector.bind(ClientWorkerFactory.class, ClientWorkerFactory.class);
		injector.bind(IDatabaseService.class, DatabaseManager.class);
		injector.bind(DatabaseContext.class, DatabaseContext.class);
		
		MainServer server = injector.get(MainServer.class);
		server.listen(server.choosePort(args));
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