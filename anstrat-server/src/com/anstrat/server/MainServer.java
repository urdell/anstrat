package com.anstrat.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import com.anstrat.network.NetworkMessage;

/**
 * The main server class.
 * Handles connection creation and removal. Keeps track of authentication.
 * @author jay
 *
 */
public class MainServer {
	
	private File logFile;
	private final Object lock = new Object();
	private HashMap<String,PlayerSocket> users = new HashMap<String,PlayerSocket>();
	private GameMatcher matcher;
	private ServerMessageHandler handler;
	private SimpleDateFormat timestamp = new SimpleDateFormat("[dd MMM 'kl' HH:mm]: ");
	
	private static final int DEFAULT_PORT = 25406;
	
	/**
	 * Creates and starts up a server instance.
	 * @param args Unused.
	 */
	public static void main(String[] args)
	{
		MainServer ms = new MainServer();
		ms.listen(ms.choosePort(args));
	}
	
	private int choosePort(String[] args){
		
		if(args.length == 0){
			logln("No port specified, using default port %d.", DEFAULT_PORT);
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
		matcher = new GameMatcher(this);
		handler = new ServerMessageHandler(this,matcher);
		createLog();
	}

	/**
	 * Attempts to create a log file into the supposedly locally existing "logs" folder.
	 */
	public void createLog()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH;mm;ss");
		new File("logs").mkdir();
		logFile = new File("logs","Server log "+sdf.format(new Date())+".log");
		
		try{
			if(!logFile.exists()){
				logFile.createNewFile();
				logln("Main server started.");
			}
		}
		catch(IOException ioe){
			System.err.println("Couldn't create log file...");
			ioe.printStackTrace();
			logFile = null;
		}
	}

	/**
	 * Logs a given message.
	 * @param message The message to log.
	 * @param formatArgs Formatting options for the message.
	 */
	public synchronized void log(String message, Object... formatArgs)
	{
		if(formatArgs.length > 0){
			message = String.format(message, formatArgs);
		}
		
		message = timestamp.format(new Date()) + message;
		System.out.print(message);
		if(logFile != null)
		{
			try
			{
				FileWriter fw = new FileWriter(logFile,true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.append(message);
				bw.close();
			}
			catch(FileNotFoundException fnfe)
			{
				System.err.println("Log file not found.");
			}
			catch(IOException ioe)
			{
				System.err.println("Error writing to log file.");
			}
		}
		else
			System.err.println("Error: log file doesn't exist.");
	}

	/**
	 * Helper method, simply adds a newline after the message and logs it.
	 * @param message The message to log.
	 * @param formatArgs Formatting options for the message.
	 */
	public void logln(String message, Object... formatArgs)
	{
		log(message+"\n", formatArgs);
	}

	/**
	 * Listens for incoming connections and handles them accordingly.
	 */
	public void listen(int port)
	{
		ServerSocket incomingConnections = null;
		
		try
		{
			incomingConnections = new ServerSocket(port);
			logln("Started listening on port %d.", port);
		}
		catch(IOException ioe)
		{
			logln("Couldn't create listener socket on port %d.", port);
		}	
		
		while(incomingConnections != null)
		{
			try
			{
				Socket socket = incomingConnections.accept();
				addConnection(socket);
			}
			catch(IOException ioe)
			{
				logln("Failed to open client socket.");
			}
		}
	}
	
	/**
	 * Adds the new incoming connection to the list of connections.
	 * @param socket The socket to add the connection for.
	 */
	public void addConnection(Socket socket)
	{
		synchronized(lock)
		{
			try {
				PlayerSocket playerSocket = new PlayerSocket(this, socket);
				Connection conn = new Connection(socket.getInetAddress(), socket.getPort());
				playerSocket.setConnection(conn);
				
				if(!users.containsKey(conn.toString()))
				{
					users.put(conn.toString(), playerSocket);
					logln(conn+" connected ("+users.size()+" clients online).");
				}
				else
					logln(conn+" tried to connect, was already connected!");
			} 
			catch(IOException ioe){
				logln("Failed to initialize connection for "+socket.getInetAddress()+".");
			}	
		}
	}
	
	/**
	 * Removes the connection for the socket in question.
	 * @param ps The socket that was disconnected for whatever reason.
	 */
	public void removeConnection(PlayerSocket ps)
	{
		synchronized(lock)
		{
			if(ps.isLoggedIn())
			{
				logoutHalp(ps, false);
				users.remove(ps.getUsername());
			}
			else
				users.remove(ps.getConnection().toString());
			
			logln(users.size()+" clients online.");
		}
	}
	
	/**
	 * Checks if the corresponding user is already logged in.
	 * @param username The username to match against.
	 * @return Whether the user is already logged in.
	 */
	public boolean isLoggedIn(String username)
	{
		if(username == null) return false;
		
		synchronized(lock)
		{
			ArrayList<String> userlist = new ArrayList<String>(users.keySet());
			
			for(String user : userlist){
				if(username.equalsIgnoreCase(user)){
					return true;
				}
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
	public void login(PlayerSocket ps, String user, String dispName)
	{
		logln(ps.getConnection()+" logged in as "+dispName+".");
		synchronized(lock)
		{
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
			synchronized(lock)
			{
				logoutHalp(ps, stillAlive);
			}
		}
		else
			logln(ps.getNetworkName()+" sent LOGOUT, was not logged in.");
	}
	
	// TODO OMG UGLY BAD bugfix.
	public void logoutHalp(PlayerSocket ps, boolean stillAlive)
	{
		logln("Attempting to remove "+ps.getUsername()+".");
		synchronized(matcher.lock)
		{
			matcher.removeUserFromLists(ps.getUser().getUserId());
		}
		users.remove(ps.getUsername());
		if(stillAlive)
			users.put(ps.getConnection().toString(), ps);
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
		
		synchronized(lock)
		{
			System.out.println("Searching for socket for user: " + username);
			System.out.println("Users keys: " + users.keySet());
			sock = users.get(username);
		}
		
		return sock;
	}
}