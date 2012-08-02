package com.anstrat.server.old;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


import com.anstrat.network_old.NetworkMessage;
import com.anstrat.server.matchmaking.GameMatcher;
import com.anstrat.server.util.Logger;

/**
 * The main server class.
 * Handles connection creation and removal. Keeps track of authentication.
 * @author jay
 *
 */
@Deprecated
public class MainServer {
	
	private static final Logger logger = Logger.getGlobalLogger();
	private HashMap<String, PlayerSocket> users = new HashMap<String,PlayerSocket>();
	private GameMatcher matcher;
	private ServerMessageHandler handler;
	
	private static final int DEFAULT_PORT = 25406;
	
	/**
	 * Creates and starts up a server instance.
	 * @param args Unused.
	 */
	public static void main(String[] args)
	{
		//Logger.getLogger(MainServer.class).addHandler(handler)
		MainServer ms = new MainServer();
		ms.listen(ms.choosePort(args));
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
	
	/**
	 * Default constructor.
	 * Initializes a NetworkMessage handler and a log file.
	 */
	public MainServer()
	{
		matcher = new GameMatcher();
		handler = new ServerMessageHandler(this,matcher);
		logger.info("Main server started.");
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
			try{
				Socket socket = incomingConnections.accept();
				addConnection(socket);
			}
			catch(IOException ioe){
				logger.info("Failed to open client socket.");
			}
		}
	}
	
	/**
	 * Adds the new incoming connection to the list of connections.
	 * @param socket The socket to add the connection for.
	 */
	public void addConnection(Socket socket){
		try {
			PlayerSocket playerSocket = new PlayerSocket(this, socket);
			Connection conn = new Connection(socket.getInetAddress(), socket.getPort());
			playerSocket.setConnection(conn);
			
			synchronized(users){
				if(!users.containsKey(conn.toString()))
				{
					users.put(conn.toString(), playerSocket);
					logger.info("%s connected. (%d clients online).", conn, users.size());
				}
				else{
					// TODO: Is this even possible?
					logger.info("%s tried to connect, was already connected!", conn);
				}
			}
		} 
		catch(IOException ioe){
			logger.info("Failed to initialize connection for %s.", socket.getInetAddress());
		}	
	}
	
	/**
	 * Removes the connection for the socket in question.
	 * @param ps The socket that was disconnected for whatever reason.
	 */
	public void removeConnection(PlayerSocket ps){
		if(ps.isLoggedIn())
		{
			logoutHalp(ps, false);
		}
		else{
			synchronized(users){
				users.remove(ps.getConnection().toString());
			}
		}
		
		logger.info(users.size()+" clients online.");
	}
	
	/**
	 * Checks if the corresponding user is already logged in.
	 * @param username The username to match against.
	 * @return Whether the user is already logged in.
	 */
	public boolean isLoggedIn(String username){
		if(username == null) return false;
		
		ArrayList<String> userlist = null;
		
		synchronized(users){
			userlist = new ArrayList<String>(users.keySet());
		}
		
		for(String user : userlist){
			if(username.equalsIgnoreCase(user)){
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Authenticates the socket as the given user.
	 * @param ps The socket to authenticate.
	 * @param user The username to authenticate as.
	 * @param dispName The displayedName to authenticate as.
	 */
	public void login(PlayerSocket ps, String user, String dispName){
		logger.info("%s logged in as '%s'.", ps.getConnection(), dispName);
		
		synchronized(users){
			users.remove(ps.getConnection().toString());
			users.put(user, ps);
		}
	}
	
	/**
	 * Deauthenticates the socket.
	 * @param ps The socket to deauthenticate.
	 */
	public void logout(PlayerSocket ps, boolean stillAlive)
	{
		if(ps.isLoggedIn())
		{
			logoutHalp(ps, stillAlive);
		}
		else{
			logger.info("%s sent LOGOUT, was not logged in.", ps.getUser().getUsername());
		}
	}
	
	// TODO OMG UGLY BAD bugfix.
	public void logoutHalp(PlayerSocket ps, boolean stillAlive){
		
		logger.info("Attempting to remove '%s'.", ps.getUsername());
		
		synchronized(matcher.lock){
			matcher.removeUserFromLists(ps.getUser().getUserId());
		}
		
		synchronized(users){
			users.remove(ps.getUsername());
			
			if(stillAlive){
				users.put(ps.getConnection().toString(), ps);
			}
		}
		
		ps.setUser(null);
	}
	
	/**
	 * Required for cross-object functionality. PlayerSockets should not know of the MessageHandler's existence.
	 * @param ps The socket the message was received from.
	 * @param nm The message received.
	 */
	public void handleMessage(PlayerSocket ps, NetworkMessage nm)
	{
		handler.handleMessage(ps, nm);
	}
	
	/**
	 * Attempts to find an active socket for the given user.
	 * @param username The username to check against.
	 * @return A PlayerSocket if one was found, null otherwise.
	 */
	public PlayerSocket getSocketForUser(String username)
	{
		if(username == null) throw new IllegalArgumentException("username can't be null!");
		PlayerSocket sock = null;
		
		synchronized(users){
			sock = users.get(username);
		}
		
		return sock;
	}
}