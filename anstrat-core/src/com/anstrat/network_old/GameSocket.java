package com.anstrat.network_old;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.anstrat.menu.MainMenu;
import com.anstrat.menu.NetworkDependentTracker;
import com.badlogic.gdx.Gdx;

/**
 * Encapsulates a socket and its streams.
 */
public class GameSocket {
	
	private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private IConnectionLostListener listener;
    private InetSocketAddress endpoint;
    
    private Runnable connectSucceded = new Runnable() {
		@Override
		public void run() {
			//NetworkDependentTracker.enableNetworkButtons();
			MainMenu.getInstance().updateGamesList();
		}
	};

	private Runnable connectFailed = new Runnable() {
		@Override
		public void run() {
			//NetworkDependentTracker.disableNetworkButtons();
		}
	};

    public GameSocket(String host, int port, IConnectionLostListener listener){
    	endpoint = new InetSocketAddress(host, port);
    	this.listener = listener;
    }

    /**
     * Initializes a connection with its underlying streams.
     * @return true if the connection was successfully established, otherwise false.
     */
	public synchronized void connect(){
		if(isConnected()) return;
		
		Gdx.app.postRunnable(connectFailed);
		
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
        	Gdx.app.postRunnable(connectSucceded);
		}
		catch(Exception e){
			Gdx.app.log("GameSocket", String.format("Failed to connect to %s:%d due to '%s'.", endpoint.getHostName(), endpoint.getPort(), e.getMessage()));
			
			close();
		}
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
			listener.connectionLost(e);
			
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
			listener.connectionLost(e);
			throw e;
		}
	}
	
	public boolean isConnected(){
		return socket != null;
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
}
