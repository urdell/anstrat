package com.anstrat.core;

import java.util.LinkedList;
import java.util.Queue;

import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.command.EndTurnCommand;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;
import com.anstrat.menu.MainMenu;
import com.badlogic.gdx.Gdx;

public class NetworkGameInstance extends GameInstance {
	
	private static final long serialVersionUID = 3L;
	
	private long gameID;
	private int currentCommandNr = 1;
	private int turnNr = 1;
	
	// Commands pending to be executed on the game state
	private Queue<Command> pendingCommands = new LinkedList<Command>();
	
	public NetworkGameInstance(long gameID, NetworkPlayer[] players, Map map, long seed){
		super(map, players, seed, GameInstanceType.NETWORK);
		this.gameID = gameID;
	}
	
	public synchronized void commandReceived(int commandNr, Command command){
		if(commandNr > currentCommandNr){
			// We've seemed to have missed some commands, ignore this one and request all missing commands from the server
			Main.getInstance().network.requestGameUpdate(gameID, currentCommandNr);
			return;
		}
		else if(commandNr < currentCommandNr){
			// Should not happen, log and ignore
			Gdx.app.log("NetworkGameInstance", "Received an command already received before. SHOULD NOT BE POSSIBLE.");
			return;
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
		
		addCommand(command);
	}
	
	@Override
	public void onCommandExecute(Command command){
		Main.getInstance().network.sendCommand(gameID, currentCommandNr, command);
		addCommand(command);
	}
	
	private void addCommand(Command command){
		currentCommandNr++;
		if(command instanceof EndTurnCommand){
			turnNr++;
			MainMenu.getInstance().updateGamesList();
		}
	}
	
	@Override
	public void showGame(boolean startZoom){
		super.showGame(startZoom);
		
		// Execute any pending commands
		executePendingCommands();
	}
	
	public void executePendingCommands(){
		if(pendingCommands.size() > 0){
			Gdx.app.log("NetworkGameInstance", String.format("Executing %d pending commands on game %d.", pendingCommands.size(), gameID));
		}
		
		while(!pendingCommands.isEmpty()){
			CommandHandler.executeNetwork(pendingCommands.poll());
		}
	}
	
	@Override
	public Player getUserPlayer(){
		long globalUserID = Main.getInstance().network.getUser().userID;
		
		for(Player player : state.players){
			NetworkPlayer networkPlayer = (NetworkPlayer) player;
			
			if(networkPlayer.userID == globalUserID){
				return networkPlayer;
			}
		}
		
		// Games where the user is not playing in should not exist
		throw new RuntimeException("NetworkGameInstance does not contain the user player!");
	}
	
	@Override
	public void resign(){
		super.resign();
		Main.getInstance().network.resign(gameID);
	}
	
	@Override
	public int getTurnNumber(){
		return this.turnNr;
	}
	
	public long getGameID(){
		return this.gameID;
	}
	
	public int getCurrentCommandNr(){
		return this.currentCommandNr;
	}
	
	public static class NetworkPlayer extends Player {
		
		private static final long serialVersionUID = 1L;
		public final long userID;
		
		public NetworkPlayer(long userID, int playerID, String displayName, int team) {
			super(playerID, displayName, team);
			this.userID = userID;
		}
	}
}
