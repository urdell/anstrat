package com.anstrat.core;

import java.util.LinkedList;
import java.util.Queue;

import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.command.EndTurnCommand;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;
import com.badlogic.gdx.Gdx;

public class NetworkGameInstance extends GameInstance {
	
	private static final long serialVersionUID = 3L;
	
	private long gameID;
	private int currentCommandNr = 1;
	private int turnNr = 1;
	
	// Commands pending to be executed on the game state
	private Queue<Command> pendingCommands = new LinkedList<Command>();
	
	public NetworkGameInstance(long gameID, NetworkPlayer[] players, Map map, long seed){
		super(map, players, seed);
		this.gameID = gameID;
	}
	
	@Override
	public int getTurnNumber(){
		return turnNr;
	}
	
	@Override
	public Player getUserPlayer(){
		long globalUserID = Main.getInstance().network.getGlobalUserID();
		
		for(Player player : state.players){
			NetworkPlayer networkPlayer = (NetworkPlayer) player;
			
			if(networkPlayer.userID == globalUserID){
				return networkPlayer;
			}
		}
		
		// Games where the user is not playing in should not exist
		throw new RuntimeException("NetworkGameInstance does not contain the user player!");
	}
	
	public void commandReceived(int commandNr, Command command){
		if(commandNr != currentCommandNr){
			// TODO: State is corrupted, should request new from server
			throw new IllegalStateException(String.format("Received command %d, expected: %d", commandNr, currentCommandNr));
		}
		
		if(isActiveGame()){
			// Execute command immediately
			CommandHandler.executeNetwork(command);
			Gdx.app.log("NetworkGameInstance", String.format("Received command from network to game %d, game is active, executing immediately.", gameID));
		}
		else{
			// Add to pending commands
			pendingCommands.add(command);
			Gdx.app.log("NetworkGameInstance", String.format("Received command from network to game %d, game is NOT active, adding to queue.", gameID));
		}
		
		currentCommandNr++;
		if(command instanceof EndTurnCommand) turnNr++;
	}
	
	@Override
	public void onCommandExecute(Command command){
		Main.getInstance().network.sendCommand(gameID, currentCommandNr, command);
		currentCommandNr++;
		if(command instanceof EndTurnCommand) turnNr++;
	}
	
	public long getGameID(){
		return this.gameID;
	}
	
	@Override
	public void showGame(boolean startZoom){
		super.showGame(startZoom);
		
		// Execute any pending commands
		if(pendingCommands.size() > 0){
			Gdx.app.log("NetworkGameInstance", String.format("Executing %d pending commands on game %d.", pendingCommands.size(), gameID));
		}
		
		while(!pendingCommands.isEmpty()){
			CommandHandler.executeNetwork(pendingCommands.poll());
		}
	}
	
	public static class NetworkPlayer extends Player {
		
		private static final long serialVersionUID = 1L;
		public final long userID;
		
		public NetworkPlayer(long userID, int playerID, String displayName, int team, int god) {
			super(playerID, displayName, team, god);
			this.userID = userID;
		}
	}
}
