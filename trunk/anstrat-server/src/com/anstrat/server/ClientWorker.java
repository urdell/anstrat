package com.anstrat.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.server.events.ClientDisconnectedEvent;
import com.anstrat.server.events.Event;
import com.anstrat.server.messageHandlers.ServerMessageHandler;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;

/**
 * A class for maintaining the connection to a single client. <br>
 * Reads and sends messages to the client and it's socket.
 * @author jay
 *
 */
public class ClientWorker implements Runnable {
	
    private static final int SOCKET_TIMEOUT = 600000; //milliseconds
    
	private final Socket socket;
	private final InetSocketAddress source;
	private final ObjectOutputStream out;
    private final ObjectInputStream in;
    
	@Inject
	private Logger logger;
	
    @Inject
	private ServerMessageHandler handler;
	
    public ClientWorker(Socket socket) throws IOException{
    	if(!socket.isConnected()){
			throw new IllegalArgumentException("Can't serve a closed connection!");
		}
    	
    	this.socket = socket;
		this.source = new InetSocketAddress(socket.getInetAddress(), socket.getPort());
		
		// Needless to keep listening forever. The socket must die if the communication fails.
		socket.setSoTimeout(SOCKET_TIMEOUT);
		this.out = new ObjectOutputStream(socket.getOutputStream());
		
		// Must flush to avoid deadlocks in initializing object input streams.
		this.out.flush();
		this.in = new ObjectInputStream(socket.getInputStream());
    }
    
    public InetSocketAddress getClientAddress(){
    	return source;
    }
    
    public synchronized boolean sendMessage(NetworkMessage message){
    	if(socket.isClosed()) return false;
    	
    	try{
    		out.writeObject(message);
    		logger.info("Sent '%s' to %s.", message.getCommand(), source);
    		return true;
    	}
    	catch(IOException ioe){
    		logger.info("Failed to send a '%s' message to %s.", message.getCommand(), source);
    		close(ioe);
    	}
    	
    	return false;
    }
    
    /**
     * A blocking method reading incoming messages.
     */
    public void run(){
		while(!socket.isClosed()){
			Object obj = null;		
			
			try{
				// Blocking operation until EOF or similar encountered.
				obj = in.readObject();
			}
			catch(IOException ioe){
				if(ioe instanceof EOFException){
					logger.info("%s disconnected [EOF].", source);
				}
				else if(ioe instanceof SocketTimeoutException){
					logger.info("%s disconnected [Timed out].", source);
				}
				else if(ioe instanceof SocketException){
					logger.info("%s disconnected [SE].", source);
				}
				else{
					logger.info("General I/O failure for %s: %s", source, ioe.getCause());
				}
				
				close(ioe);
			}
			catch(ClassNotFoundException cnfe){
				logger.info("Received erroneous message from %s: %s", source, cnfe.getCause());
				close(cnfe);
			}
			catch(Exception e){
				logger.info("Unknown error for %s: %s.", source, e.getLocalizedMessage());
				close(e);
			}
			
			if(obj != null){
				if(obj instanceof NetworkMessage){
					NetworkMessage message = (NetworkMessage) obj;
					logger.info("Received %s from %s.", message.getCommand(), source);
					handler.handleMessage(source, message);
				}
				else{
					logger.info("%s sent malformed network input (not a NetworkMessage).", source);
				}
			}
		}
	}
    
    /**
     * Closes the underlying connection.
     * @param cause the cause of the close, if due to an exception, can be null.
     */
    public synchronized void close(Throwable cause){
    	if(socket.isClosed()) return;
    	
    	try{
    		out.flush();
    	}
    	catch(Exception e){}

    	try{
    		socket.close();
    	}
    	catch(Exception e){}
    	
    	Event.post(new ClientDisconnectedEvent(source, cause));
    }
}
