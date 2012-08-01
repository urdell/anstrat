package com.anstrat.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.anstrat.network.NetworkMessage;
import com.anstrat.server.util.Logger;

/**
 * A class containing a Socket and some extra functionality, including its own thread to read incoming messages.
 * @author jay
 *
 */
public class PlayerSocket implements Runnable {
	
	private MainServer server;
	private Socket socket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private Connection connection;
    private static final Logger logger = Logger.getGlobalLogger();
    private static final int SOCKET_TIMEOUT = 600000; //milliseconds
    
    // Used for authentication checks.
    private User user = null;
    
    /**
     * Default constructor.
     * @param server Used to access server functionality and logging.
     * @param socket The socket containing the connection in question.
     * @throws IOException
     */
    public PlayerSocket(MainServer server, Socket socket) throws IOException
    {
    	this.server = server;
		this.socket = socket;
		// Needless to keep listening forever. The socket must die if the communication fails.
		socket.setSoTimeout(SOCKET_TIMEOUT);
		this.out = new ObjectOutputStream(socket.getOutputStream());
		// Must flush to avoid deadlocks in initializing input streams.
		this.out.flush();
		this.in = new ObjectInputStream(socket.getInputStream());
		new Thread(this).start();
    }
    
    public Connection getConnection()
    {
    	return connection;
    }
    
    public void setConnection(Connection conn)
    {
    	this.connection = conn;
    }
    
    public String getUsername()
    {
    	return user==null?null:user.getUsername();
    }
    
    public User getUser()
    {
    	return user;
    }
    
    public boolean isLoggedIn()
    {
    	return user != null;
    }
    
    public void setUser(User user)
    {
    	this.user = user;
    }
    
    public String getNetworkName()
    {
    	return user==null?connection.toString():user.getDisplayedName();
    }
    
    /**
     * Used to send messages to sockets, killing the socket when encountering a failure.
     * @param nm The message to be sent.
     */
    public synchronized void sendMessage(NetworkMessage nm)
    {
    	try
    	{
    		out.writeObject(nm);
    		logger.info("Sent a '%s' message to %s.", nm.getCommand(), getNetworkName());
    	}
    	catch(IOException ioe)
    	{
    		logger.info("Failed to send a '%s' message to %s.", nm.getCommand(), getNetworkName());
    		kill();
    	}
    }
    
    /**
     * The thread reading incoming messages.
     */
    public void run()
	{
		while(socket != null)
		{
			Object obj = null;		
			
			try
			{
				// Blocking operation until EOF or similar encountered.
				obj = in.readObject();
			}
			catch(EOFException e)
			{
				logger.info(getNetworkName()+" disconnected [EOF].");
				kill();
			}
			catch(SocketTimeoutException e)
			{
				logger.info(getNetworkName()+" disconnected [Timed out].");
				kill();
			}
			catch(SocketException e)
			{
				logger.info(getNetworkName()+" disconnected [SE].");
				kill();
			}
			catch(ClassNotFoundException cnfe)
			{
				logger.info("Received erroneous message from "+getNetworkName()+": "+cnfe.getCause());
			}
			catch(IOException ioe)
			{
				logger.info("I/O failure for "+getNetworkName()+": "+ioe.getCause());
				ioe.printStackTrace();
				kill();
			}
			catch(Exception e)
			{
				logger.info("Unknown error for "+getNetworkName()+": "+e.getLocalizedMessage());
				kill();
			}
			
			if(obj != null)
			{
				if(obj instanceof NetworkMessage)
					server.handleMessage(this, (NetworkMessage) obj);
				else
				{
					logger.info(connection+" sent malformed network input (not a NetworkMessage).");
				}
			}
		}
	}
    
    /**
     * Robust method to kill off the socket when it is disconnected manually, or when an error is encountered.
     */
    private void kill()
    {
    	server.removeConnection(this);
    	
    	try{
    		in.close();
    	}
    	catch(Exception e){}
    	
    	try{
    		out.flush();
    	}
    	catch(Exception e){}
    	
    	try{
    		out.close();
    	}
    	catch(Exception e){}
    	
    	try{
    		socket.close();
    	}
    	catch(Exception e){}
    	
    	socket = null;
    	
    	logger.info("Removed connection "+connection+".");
    }
    
    public InetAddress getInetAddress()
    {
    	return socket.getInetAddress();
    }
    
    @Override
    public boolean equals(Object o){
    	
    	if(o != null && o instanceof PlayerSocket)
    		return connection.equals(((PlayerSocket) o).connection);
    	return false;
    }
    
    @Override
    public int hashCode(){
    	return connection.hashCode() * 17;
    }
}
