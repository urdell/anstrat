package com.anstrat.networkv2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.Gdx;

/**
 * Encapsulates a socket and its streams.
 */
public class GameSocket {
	
	private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private InetSocketAddress endpoint;
    
    private Collection<IConnectionListener> listeners = new ArrayList<IConnectionListener>();

    public GameSocket(String host, int port){
    	this.endpoint = new InetSocketAddress(host, port);
    }

    public void addListener(IConnectionListener listener){
    	this.listeners.add(listener);
    }
    
	public synchronized boolean connect(){
		if(socket != null) return true;
		
		try{
			Gdx.app.log("GameSocket", String.format("Connecting to %s:%d...", endpoint.getHostName(), endpoint.getPort()));
			socket = new Socket();
			socket.connect(endpoint, 10000);
			
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			
			// The problem: Setting a timeout ignores keep-alive, but not setting a timeout
			// means the call below can deadlock if the server never answers.
			// Workaround: Set timeout and then remove it when header is read (not sure if this actually works)
			socket.setSoTimeout(5000);
			in = new ObjectInputStream(socket.getInputStream()); // Blocks until a header is read from endpoint
        	socket.setSoTimeout(0);
        	socket.setKeepAlive(true);
			
        	Gdx.app.log("GameSocket", String.format("Successfully connected to %s:%d", endpoint.getHostName(), endpoint.getPort()));
        	
        	for(IConnectionListener listener : listeners){
        		listener.connectionEstablished();
        	}
        	
        	return true;
		}
		catch(Exception e){
			Gdx.app.log("GameSocket", String.format("Failed to connect to %s:%d due to '%s'.", endpoint.getHostName(), endpoint.getPort(), e.getMessage()));
			
			close();
		}
		
		return false;
	}
	
	public void sendObject(Serializable obj) throws IOException {
		
		ObjectOutputStream stream = out;
		if(stream == null) throw new IOException("Attemped write without being connected.");
		
		try{
			stream.writeObject(obj);
			stream.flush();
		}
		catch(IOException e){
			close();
			
        	for(IConnectionListener listener : listeners){
        		listener.connectionLost(e);
        	}
			
			throw e;
		}
	}
	
	public Object readObject() throws IOException, ClassNotFoundException {
		
		ObjectInputStream stream = in;		
		if(stream == null) throw new IOException("Attemped read without being connected.");
		
		try {			
			// Blocks
			return stream.readObject();
			
		} catch (IOException e) {
			close();
			
        	for(IConnectionListener listener : listeners){
        		listener.connectionLost(e);
        	}
        	
			throw e;
		}
	}
	
	public void close(){
		try{
			if(socket != null) socket.close();
		} catch (IOException e){
			Gdx.app.log("GameSocket", String.format("Error when closing socket due to '%s'.", e.getMessage()));
		}
		
		socket = null;
	}
	
	public String getHost(){
		return this.endpoint.getHostName();
	}
	
	public int getPort(){
		return this.endpoint.getPort();
	}
	
	public static interface IConnectionListener {
		public void connectionLost(Throwable cause);
		public void connectionEstablished();
	}
}
