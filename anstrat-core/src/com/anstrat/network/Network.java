package com.anstrat.network;

import java.io.Serializable;
import java.util.List;

import com.anstrat.network.NetworkMessage.Command;
import com.anstrat.network.NetworkWorker.INetworkCallback;
import com.badlogic.gdx.Gdx;

public class Network implements INetworkCallback {

	private INetworkResponseHandler listener;
	private NetworkSessionWorker worker;
	
	public Network(String host, int port, User storedLogin){
		this.worker = new NetworkSessionWorker(new GameSocket(host, port), this, storedLogin);
	}
	
	public void setListener(INetworkResponseHandler listener){
		this.listener = listener;
	}
    
	@Override
	public void messageReceived(NetworkMessage message) {
		Command command = message.getCommand();
		final List<Serializable> payload = message.getPayload();
		
		try{
			switch(message.getCommand()){
				case ACCEPT_LOGIN: {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							long userID = (Long) payload.get(0);
							listener.loginAccepted(userID);
						}
					});
					
					break;
				}
				case DENY_LOGIN: {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							String reason = (String) payload.get(0);
							listener.loginDenied(reason);
						}
					});
					
					break;
				}
				case USER_CREDENTIALS: {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							long userID = (Long) payload.get(0);
							String password = (String) payload.get(1);
							listener.userCredentials(userID, password);
						}
					});
					
					break;
				}
				case DISPLAY_NAME_CHANGED: {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							String name = (String) payload.get(0);
							listener.displayNameChanged(name);
						}
					});
					
					break;
				}
				case DISPLAY_NAME_CHANGE_REJECTED: {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							String reason = (String) payload.get(0);
							listener.displayNameChanged(reason);
						}
					});
					
					break;
				}
				case GAME_STATE_CORRUPTED: {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							//listener.gameStateCorrupted()
						}
					});
					
					break;
				}
				case GAME_STARTED: {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							//listener.gameStarted()
						}
					});
					
					break;
				}
				case SEND_COMMAND: {
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							//listener.command()
						}
					});
					
					break;
				}
				default: {
					Gdx.app.log("Network", String.format("Received unknown server command '%s'.", message.getCommand()));
				}
			}
		}
		catch(ClassCastException e){
			Gdx.app.log("Network", String.format("Unexpected payload type in message '%s'. Reason: '%s'.", command, e.getMessage()));
		}
		catch(IndexOutOfBoundsException e){
			Gdx.app.log("Network", String.format("Missing payload in message '%s'. Reason: '%s'.", command, e.getMessage()));
		}
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
	
    // Network API
    
    public void setDisplayName(String name){
    	throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void requestGameUpdate(long gameID, int currentCommandNr, int stateChecksum){
    	throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void requestRandomGame(){
    	throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void sendCommand(long gameID, int commandNr){
    	throw new UnsupportedOperationException("Not implemented yet.");
    }
}
