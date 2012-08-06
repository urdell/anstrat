package com.anstrat.network;

import java.io.Serializable;
import java.util.List;

import com.anstrat.network.NetworkWorker.INetworkCallback;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.network.protocol.NetworkMessage.Command;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Network implements INetworkCallback {

	private INetworkResponseListener listener;
	private NetworkUserManager worker;
	
	public Network(String host, int port, FileHandle storedLoginFile){
		this.worker = new NetworkUserManager(new GameSocket(host, port), this, storedLoginFile);
	}
	
	public void setListener(INetworkResponseListener listener){
		this.listener = listener;
	}
    
	public long getUserID(){
		return worker.getUser().userID;
	}
	
	@Override
	public void messageReceived(NetworkMessage message) {
		Command networkCommand = message.getCommand();
		final List<Serializable> payload = message.getPayload();
		
		try{
			switch(networkCommand){
				
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
					final long gameID = (Long) payload.get(0);
					final GameSetup game = (GameSetup) payload.get(1);
					
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run(){
							listener.gameStarted(gameID, game);
						}
					});
					
					break;
				}
				case SEND_COMMAND: {
					final long gameID = (Long) payload.get(0);
					final int commandNr = (Integer) payload.get(1);
					final com.anstrat.command.Command command = (com.anstrat.command.Command) payload.get(2);
					
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							listener.command(gameID, commandNr, command);
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
			Gdx.app.log("Network", String.format("Unexpected payload type in message '%s'. Reason: '%s'.", networkCommand, e.getMessage()));
		}
		catch(IndexOutOfBoundsException e){
			Gdx.app.log("Network", String.format("Missing payload in message '%s'. Reason: '%s'.", networkCommand, e.getMessage()));
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
	
	public void resetLogin(){
		worker.resetLogin();
	}
	
    // Network API
    
    public void setDisplayName(String name){
    	worker.sendMessage(new NetworkMessage(Command.SET_DISPLAY_NAME, name));
    }
    
    public void requestGameUpdate(long gameID, int currentCommandNr, int stateChecksum){
    	throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public void requestRandomGame(int team, int god){
    	worker.sendMessage(new NetworkMessage(Command.REQUEST_RANDOM_GAME, team, god));
    }
    
    public void sendCommand(long gameID, int commandNr, com.anstrat.command.Command command){
    	worker.sendMessage(new NetworkMessage(Command.SEND_COMMAND, gameID, commandNr, command));
    }
}
