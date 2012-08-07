package com.anstrat.network;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.menu.MainMenu;
import com.anstrat.network.protocol.GameSetup;
import com.badlogic.gdx.Gdx;

/**
 * Converts UI actions to network commands and vice-versa.<br>
 * All UI interaction with the network should pass through this class.
 * @author Erik
 *
 */
public class NetworkController {
	private final Network network;
	
	public NetworkController(final Network network){
		this.network = network;
		
		// Creating the implementation of the listener as an anonymous class, as we don't want
		// the implemented methods to be directly callable on this object. (it would be confusing) 
		this.network.setListener(getNetworkResponseHandlerImplementation());
		
		this.network.setLoginCallback(new Runnable() {
			@Override
			public void run() {
				
				// Request updates on all active games on login
				for(GameInstance game : Main.getInstance().games.getActiveGames()){
					if(game instanceof NetworkGameInstance){
						NetworkGameInstance networkGame = (NetworkGameInstance) game;
						
						network.requestGameUpdate(networkGame.getGameID(), networkGame.getCurrentCommandNr());
					}
				}
			}
		});
	}
	
	// Debug
	public void resetLogin(){
		network.resetLogin();
	}
	
	// UI actions
	// TODO: Add ui actions that invoke methods on com.anstrat.network.Network
	
	public long getGlobalUserID(){
		return network.getUserID();
	}
	
	public void sendCommand(long gameID, int commandNr, com.anstrat.command.Command command){
		network.sendCommand(gameID, commandNr, command);
	}
	
	public void findRandomGame(int team, int god){
		this.network.requestRandomGame(team, god);
	}
	
	public void requestGameUpdate(long gameID, int currentCommandNr){
		this.network.requestGameUpdate(gameID, currentCommandNr);
	}
	
	// Network listener implementation
	
	private INetworkResponseListener getNetworkResponseHandlerImplementation(){
		return new INetworkResponseListener() {
			
			@Override
			public void gameStarted(long gameID, GameSetup gameSetup) {
				
				List<NetworkGameInstance.NetworkPlayer> players = new ArrayList<NetworkGameInstance.NetworkPlayer>();
				
				// Create the players
				for(int i = 0; i < gameSetup.players.length; i++){
					GameSetup.Player player = gameSetup.players[i];
					System.out.println("Player: " + player.userID);
					players.add(new NetworkGameInstance.NetworkPlayer(player.userID, i, player.displayName, player.team, player.god));
				}
				
				Main.getInstance().games.createNetworkGame(
						gameID, 
						players.toArray(new NetworkGameInstance.NetworkPlayer[players.size()]), 
						gameSetup.map, 
						gameSetup.randomSeed);
				MainMenu.getInstance().updateGamesList();
			}
			
			@Override
			public void displayNameChanged(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void displayNameChangeRejected(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void command(long gameID, int commandNr, com.anstrat.command.Command command) {
				NetworkGameInstance game = Main.getInstance().games.getGame(gameID);
				
				if(game != null){
					game.commandReceived(commandNr, command);
				}
				else{
					Gdx.app.log("NetworkController", String.format("Received a command for a game that does not exist. (gameID = %d)"));
				}
			}
		};
			
	}
}
