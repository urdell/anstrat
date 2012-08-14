package com.anstrat.network;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.gameCore.Player;
import com.anstrat.gui.GEngine;
import com.anstrat.menu.MainMenu;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Converts UI actions to network commands and vice-versa.<br>
 * All UI interaction with the network should pass through this class.
 * @author Erik
 *
 */
public class NetworkController {
	private final Network network;
	private List<Button> networkButtons = new ArrayList<Button>();
	
	private Label networkLoginLabel;
	private String networkLabelText = "Not connected.";
	private String userDisplayName;
	
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
				
				// Enable network buttons
				for(Button b : networkButtons){
					Assets.SKIN.setEnabled(b, true);
				}
				
				// Update login label to reflect the new status
				String displayName = userDisplayName != null ? userDisplayName : "Unnamed" + network.getUserID();
				networkLabelText = String.format("Logged in as: %s", displayName);
				if(networkLoginLabel != null) networkLoginLabel.setText(networkLabelText);
			}
		});
		
		this.network.setConnectionLostCallback(new Runnable() {
			@Override
			public void run() {
				for(Button b : networkButtons){
					Assets.SKIN.setEnabled(b, false);
				}
				
				networkLabelText = "Not connected.";
				if(networkLoginLabel != null) networkLoginLabel.setText(networkLabelText);
			}
		});	
	}
	
	public void setNetworkLabel(Label label){
		this.networkLoginLabel = label;
		label.setText(networkLabelText);
	}
	
	public void registerNetworkButton(Button button){
		this.networkButtons.add(button);
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
	
	public void resign(long gameID){
		// Send a resign command for the user player
		this.network.resign(gameID, Main.getInstance().games.getGame(gameID).getUserPlayer().playerId);
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
					String playerName = player.displayName != null ? player.displayName : "Unnamed" + player.userID;
					players.add(new NetworkGameInstance.NetworkPlayer(player.userID, i, playerName, player.team, player.god));
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

			@Override
			public void playerResigned(long gameID, int playerID) {
				GameInstance game = Main.getInstance().games.getGame(gameID);
				
				if(game != null && Main.getInstance().getScreen() instanceof GEngine && GameInstance.activeGame == game){
					Player player = game.state.players[playerID];
					Popup.showGenericPopup("Game over", String.format("%s has resigned, you won!", player.getDisplayName()));
				}
				
				Main.getInstance().games.endGame(game);
			}
		};
			
	}
}
