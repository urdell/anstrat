package com.anstrat.network;

import com.anstrat.network.NetworkWorker.INetworkCallback;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.network.protocol.NetworkMessage.Command;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Network implements INetworkCallback {

	private INetworkResponseListener listener;
	private NetworkUserManager worker;
	private NetworkMessageParser parser;
	
	public Network(String host, int port, FileHandle storedLoginFile){
		this.worker = new NetworkUserManager(new GameSocket(host, port), this, storedLoginFile);
		this.parser = new NetworkMessageParser();
	}
	
	public void setListener(INetworkResponseListener listener){
		this.listener = listener;
		this.worker.setListener(listener);
	}
	
	public boolean isLoggedIn(){
		return worker.isLoggedIn();
	}
    
	public User getUser(){
		return worker.getUser();
	}
	
	public void setLoginCallback(Runnable callback){
		this.worker.setOnLoggedInCallback(callback);
	}
	
	public void setConnectionLostCallback(Runnable callback){
		this.worker.setConnectionLostCallback(callback);
	}
	
	public void setNewUserCredentialsCallback(Runnable callback){
		this.worker.setOnNewUserCredentials(callback);
	}
	
	@Override
	public void messageReceived(final NetworkMessage message) {
		if(listener == null){
			Gdx.app.log("Network", String.format("Received message %s but no listener has been set, message ignored.", message.getCommand()));
			return;
		}
		
		// Delegate parsing of messages
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				parser.parseMessage(message, listener);
			}
		});
	}
	
	public void start(){
		worker.start();
	}
	
	public void stop(){
		worker.stop();
	}
	
	public void resume(){
		Gdx.app.log("Network", "Resumed network.");
		worker.resume();
	}
	
	public void pause(){
		Gdx.app.log("Network", "Paused network.");
		worker.pause();
	}
	
	public void resetLogin(){
		worker.resetLogin();
	}
	
    // Network API
    
    public void setDisplayName(String name){
    	worker.sendMessage(new NetworkMessage(Command.SET_DISPLAY_NAME, name));
    }
    
    public void requestGameUpdate(long gameID, int currentCommandNr){
    	worker.sendMessage(new NetworkMessage(Command.REQUEST_GAME_UPDATE, gameID, currentCommandNr));
    }
    
    public void requestRandomGame(GameOptions options){
    	worker.sendMessage(new NetworkMessage(Command.REQUEST_RANDOM_GAME, options));
    }
    
    public void sendCommand(long gameID, int commandNr, com.anstrat.command.Command command){
    	worker.sendMessage(new NetworkMessage(Command.SEND_COMMAND, gameID, commandNr, command));
    }
    
    public void resign(long gameID, int playerID){
    	worker.sendMessage(new NetworkMessage(Command.RESIGN, gameID, playerID));
    }
    
    public void invitePlayerByName(String playerName, GameOptions options){
    	worker.sendMessage(new NetworkMessage(Command.INVITE_PLAYER_NAME, playerName, options));
    }
    
    public void invitePlayerByID(long userID, GameOptions options){
    	worker.sendMessage(new NetworkMessage(Command.INVITE_PLAYER_ID, userID, options));
    }
    
    public void acceptInvite(long inviteID, int team){
    	worker.sendMessage(new NetworkMessage(Command.INVITE_ACCEPT, inviteID, team));
    }
    
    public void declineInvite(long inviteID){
    	worker.sendMessage(new NetworkMessage(Command.INVITE_DECLINE, inviteID));
    }
}
