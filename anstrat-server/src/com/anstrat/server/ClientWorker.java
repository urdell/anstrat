package com.anstrat.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.anstrat.network.NetworkMessage;
import com.anstrat.server.util.Logger;

/**
 * A class for maintaining the connection to a single client. <br>
 * Reads and sends messages to the client and it's socket.
 * @author jay
 *
 */
public class ClientWorker implements Runnable {
	
	private static final Logger logger = Logger.getGlobalLogger();
    private static final int SOCKET_TIMEOUT = 600000; //milliseconds
    
	private final Socket socket;
	private final InetSocketAddress source;
	private final ObjectOutputStream out;
    private final ObjectInputStream in;
    
	private final IClientWorkerCallback callback;
	private final ServerMessageHandler handler;
	
    /**
     * @param socket The socket containing the connection in question.
     * @throws IOException
     */
    public ClientWorker(Socket socket, IClientWorkerCallback callback, ServerMessageHandler handler) throws IOException{
		if(!socket.isConnected()){
			throw new IllegalArgumentException("Can't serve a closed connection!");
		}
    	
    	this.socket = socket;
		this.callback = callback;
		this.handler = handler;
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
    
    public synchronized void sendMessage(NetworkMessage message){
    	if(socket.isClosed()) return;
    	
    	try{
    		out.writeObject(message);
    		logger.info("Sent '%s' to %s.", message.getCommand(), source);
    	}
    	catch(IOException ioe){
    		logger.info("Failed to send a '%s' message to %s.", message.getCommand(), source);
    	}
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
				
				close();
			}
			catch(ClassNotFoundException cnfe){
				logger.info("Received erroneous message from %s: %s", source, cnfe.getCause());
				close();
			}
			catch(Exception e){
				logger.info("Unknown error for %s: %s.", source, e.getLocalizedMessage());
				close();
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
    	
    	callback.connectionClosed(source, cause);
    }
    
    private void close(){
    	close(null);
    }
    
    public static interface IClientWorkerCallback {
    	public void connectionClosed(InetSocketAddress client, Throwable cause);
    }
}
