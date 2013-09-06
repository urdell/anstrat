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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Converts UI actions to network commands and vice-versa.<br>
 * All UI interaction with the network should pass through this class.
 * @author Erik
 *
 */
public class NetworkController {
	private final Network network;
	private List<Button> networkButtons = new ArrayList<Button>();
	
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
			}
		});
		
		this.network.setConnectionLostCallback(new Runnable() {
			@Override
			public void run() {
				// Disable all network buttons
				for(Button b : networkButtons){
					Assets.SKIN.setEnabled(b, false);
				}
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
	
	public void registerNetworkButton(Button button){
		this.networkButtons.add(button);
		Assets.SKIN.setEnabled(button, network.isLoggedIn());
	}
	
	public boolean isLoggedIn(){
		return network.isLoggedIn();
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
	
	public void acceptInvite(long inviteId, int team, int god){
		this.network.acceptInvite(inviteId, team, god);
	}
	public void declineInvite(long inviteId){
		this.network.declineInvite(inviteId);
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
					players.add(new NetworkGameInstance.NetworkPlayer(player.userID, i, playerName, player.team));
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
					Gdx.app.log("NetworkController", String.format("Received a command for a game that does not exist. (gameID = %d)", gameID));
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
								public void clicked(InputEvent event, float x, float y) {
									Main.getInstance().setScreen(MainMenu.getInstance());
									Popup.getCurrentPopup().close();
								}
							})).show();
				}
				
				Main.getInstance().games.endGame(game);
			}
			
			@Override
			public void inviteRequest(long inviteID, String senderName, GameOptions options) {
				Main.getInstance().invites.recievedInvite(inviteID, senderName, options);
			}

			@Override
			public void inviteCompleted(long inviteID, boolean accept) {
				Main.getInstance().invites.inviteCompleted(inviteID, accept);
			}

			@Override
			public void invitePending(long inviteID, String receiverDisplayName, GameOptions options) {
				Main.getInstance().invites.invitePending(inviteID, receiverDisplayName, options);
			}

			@Override
			public void inviteFailed(String reason) {
				Gdx.app.log("NetworkController", "Invite failed due to: " + reason);
			}
		};
			
	}
}
