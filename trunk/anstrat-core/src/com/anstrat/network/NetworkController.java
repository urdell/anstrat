package com.anstrat.network;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.gameCore.Player;
import com.anstrat.gui.GEngine;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.menu.MainMenu;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.popup.DisplayNameChangePopup;
import com.anstrat.popup.Popup;
import com.anstrat.popup.TutorialPopup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
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
				setNetworkLabelText(String.format("Logged in as: %s", network.getUser().displayName));
			}
		});
		
		this.network.setConnectionLostCallback(new Runnable() {
			@Override
			public void run() {
				// Disable all network buttons
				for(Button b : networkButtons){
					Assets.SKIN.setEnabled(b, false);
				}
				
				setNetworkLabelText("Not connected.");
				networkLabelText = "Not connected.";
				if(networkLoginLabel != null) networkLoginLabel.setText(networkLabelText);
			}
		});
		
		this.network.setNewUserCredentialsCallback(new Runnable() {
			@Override
			public void run() {
				// Clear all saved games
				Main.getInstance().games.clear();
				MainMenu.getInstance().updateGamesList();
			}
		});
	}
	
	public void setNetworkLabel(Label label){
		this.networkLoginLabel = label;
		label.setText(networkLabelText);
	}
	
	private void setNetworkLabelText(String text){
		networkLabelText = text;
		if(networkLoginLabel != null) networkLoginLabel.setText(text);
	}
	
	public void registerNetworkButton(Button button){
		this.networkButtons.add(button);
		Assets.SKIN.setEnabled(button, network.isLoggedIn());
	}
	
	// Debug
	public void resetLogin(){
		network.resetLogin();
	}
	
	// UI actions
	
	public User getUser(){
		return network.getUser();
	}
	
	public void sendCommand(long gameID, int commandNr, com.anstrat.command.Command command){
		network.sendCommand(gameID, commandNr, command);
	}
	
	public void findRandomGame(GameOptions options){
		this.network.requestRandomGame(options);
	}
	
	public void invitePlayer(long userID, GameOptions options){
		this.network.invitePlayerByID(userID, options);
	}
	
	public void invitePlayer(String playerName, GameOptions options){
		this.network.invitePlayerByName(playerName, options);
	}
	
	public void requestGameUpdate(long gameID, int currentCommandNr){
		this.network.requestGameUpdate(gameID, currentCommandNr);
	}
	
	public void setDisplayName(String name){
		this.network.setDisplayName(name);
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
			public void inviteRequest(long inviteID, String senderName, GameOptions options) {
				new TutorialPopup().show();
				// TODO proper handling
				
			}
			
			@Override
			public void displayNameChanged(String name) {
				setNetworkLabelText(String.format("Logged in as: %s", name));
				
				Popup p = Popup.getCurrentPopup();
				if(p instanceof DisplayNameChangePopup){
					((DisplayNameChangePopup) p).nameChanged(name);
				}
			}
			
			@Override
			public void displayNameChangeRejected(String reason) {
				Popup p = Popup.getCurrentPopup();
				if(p instanceof DisplayNameChangePopup){
					((DisplayNameChangePopup) p).nameChangeError(reason);
				}
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
					
					new Popup("Game over", true,
							new Label(String.format("%s has resigned, you won!", player.getDisplayName()), Assets.SKIN), 
							ComponentFactory.createButton("OK", new ClickListener() {
								@Override
								public void click(Actor actor, float x, float y) {
									Main.getInstance().setScreen(MainMenu.getInstance());
									Popup.getCurrentPopup().close();
								}
							})).show();
				}
				
				Main.getInstance().games.endGame(game);
			}

			@Override
			public void inviteCompleted(long inviteID, boolean accept) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void invitePending(long inviteID,
					String receiverDisplayName, GameOptions options) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void inviteFailed(String reason) {
				Gdx.app.log("NetworkController", "Invite failed due to: " + reason);
			}
		};
			
	}
}
