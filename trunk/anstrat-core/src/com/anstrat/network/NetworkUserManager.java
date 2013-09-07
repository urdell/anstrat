package com.anstrat.network;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.anstrat.core.Main;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.network.protocol.NetworkMessage.Command;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;


/**
 * Encapsulates the NetworkWorker, making sure a user always exists and is logged in.
 * @author Erik
 *
 */
class NetworkUserManager extends NetworkWorker implements GameSocket.IConnectionListener {
	private User user;
	private boolean loggedIn;
	private Object lock = new Object();
	private Runnable loginCallback, connectionLostCallback;
	private INetworkResponseListener listener;
	
	// Messages waiting for register/login to succeed
	private Queue<NetworkMessage> pending = new LinkedList<NetworkMessage>();
	private Runnable newUserCredentialsCallback;
	
	public NetworkUserManager(GameSocket socket, final INetworkCallback callback, final FileHandle storedLoginFile) {
		super(socket);
		
		setCallback(new NetworkWorker.INetworkCallback() {
			@Override
			public void messageReceived(NetworkMessage message) {

				// Intercept authentication messages
				List<Serializable> payload = message.getPayload();
				Command command = message.getCommand();
				
				switch(command){
					case ACCEPT_LOGIN: {
						onLoggedIn();
						break;
					}
					case DENY_LOGIN: {
						String reason = (String) payload.get(0);
						
						synchronized(lock){
							loggedIn = false;
						}
						
						if(reason.contains("version"))
							Popup.showGenericPopup("Version update required", reason);
						
						Gdx.app.log("NetworkUserManager", String.format("Login denied due to: %s", reason));
						break;
					}
					case USER_CREDENTIALS: {
						long userID = (Long) payload.get(0);
						String password = (String) payload.get(1);
						
						synchronized(lock){
							user = new User(userID, password, null);
						}
						
						user.toFile(storedLoginFile);
						Gdx.app.log("NetworkUserManager", String.format("Received new user with id '%d' from server.", userID));
						if(newUserCredentialsCallback != null) newUserCredentialsCallback.run();
						onLoggedIn();
						
						break;
					
					}
					case DISPLAY_NAME_CHANGED: {
						final String name = (String) payload.get(0);
						
						synchronized(lock){
							user = new User(user.userID, user.password, name);
						}
						
						user.toFile(storedLoginFile);
						Gdx.app.log("NetworkUserManager", String.format("Received new display name '%s' from server.", name));
						
						if(listener != null){
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									listener.displayNameChanged(name);
								}
							});
						}
						
						break;
					}
					case DISPLAY_NAME_CHANGE_REJECTED: {
						final String reason = (String) payload.get(0);
						
						if(listener != null){
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									listener.displayNameChangeRejected(reason);
								}
							});
						}
						
						break;
					}
					default: {
						// Delegate all other commands
						callback.messageReceived(message);
					}
				}
				
			}
		});

		socket.addListener(this);
		user = User.fromFile(storedLoginFile);
		System.out.println(user);
	}
	
	// Create a new NetworkUserManager from a previous instance
	public NetworkUserManager(GameSocket socket,
							  INetworkCallback callback,
							  FileHandle storedLoginFile,
							  NetworkUserManager oldInstance) {
		this(socket, callback, storedLoginFile);
		
		if (oldInstance != null) {
			this.pending = oldInstance.pending;
			this.outgoing = oldInstance.outgoing;
			this.listener = oldInstance.listener;
			this.loginCallback = oldInstance.loginCallback;
			this.connectionLostCallback = oldInstance.connectionLostCallback;
			this.newUserCredentialsCallback = oldInstance.newUserCredentialsCallback;
		}
	}
	
	public void setListener(INetworkResponseListener listener){
		this.listener = listener;
	}

	public void setOnLoggedInCallback(Runnable callback){
		this.loginCallback = callback;
	}
	
	public void setOnNewUserCredentials(Runnable callback){
		this.newUserCredentialsCallback = callback;
	}
	
	public void sendMessage(NetworkMessage message){
		// Prevent messages from being sent until we're logged in
		synchronized (lock){
			Queue<NetworkMessage> queue = loggedIn ? outgoing : pending;
			queue.add(message);
			
			if(loggedIn){
				Gdx.app.log("NetworkUserManager", String.format("Added %s to outgoing queue.", message.getCommand()));
			}
			else{
				Gdx.app.log("NetworkUserManager", String.format("Added %s to pending queue.", message.getCommand()));
			}
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		Gdx.app.log("NetworkUserManager", String.format("Connection lost due to: %s", cause));
		synchronized(lock){
			this.loggedIn = false;
		}
		
		if(connectionLostCallback != null) connectionLostCallback.run();
	}

	@Override
	public void connectionEstablished() {
		synchronized (lock){
			if(user == null){
				// Request new user from server
				outgoing.add(new NetworkMessage(Command.CREATE_NEW_USER));
			}
			else{
				// Login
				outgoing.add(new NetworkMessage(Command.LOGIN, user.userID, user.password, Main.versionNr));
			}
		}
	}
	
	public boolean isLoggedIn(){
		return loggedIn;
	}
	
	public void resetLogin(){
		synchronized(lock) {
			user = null;
			loggedIn = false;
			pending.clear();
		}
		
		connectionEstablished();
	}
	
	public User getUser(){
		return this.user;
	}
	
	private void onLoggedIn(){
		synchronized(lock){
			loggedIn = true;
			
			// Send pending messages
			while(!pending.isEmpty()){
				outgoing.add(pending.poll());
			}
		}
		
		if(loginCallback != null) loginCallback.run();
	}

	public void setConnectionLostCallback(Runnable callback) {
		this.connectionLostCallback = callback;
	}
}
