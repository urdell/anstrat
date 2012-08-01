package com.anstrat.core;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.gameCore.GameType;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.geography.Map;
import com.anstrat.gui.GEngine;
import com.anstrat.menu.AccountMenu;
import com.anstrat.menu.MainMenu;
import com.anstrat.menu.NetworkDependentTracker;
import com.anstrat.network.GameRequest;
import com.anstrat.network.INetworkListener;
import com.anstrat.network.Network;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;

/**
 * Converts UI actions to network commands and vice-versa.<br>
 * All UI interaction with the network should pass through this class.
 * @author Erik
 *
 */
public class NetworkController {
	private User user;
	private final Network network;
	private final HashMap<Long, GameRequest> gameRequests = new HashMap<Long, GameRequest>();
	private final Random random = new Random();
	
	public NetworkController(Network network){
		this.network = network;
		
		// Creating the implementation of the listener as an anonymous class, as we don't want
		// the implemented methods to be directly callable on this object. (it would be confusing) 
		network.setListener(getNetworkListenerImplementation());
		
		// Try to load saved login info
		user = User.fromFile(Gdx.files.local("login.bin"));
		if(user != null){
			network.login(user.username, user.password);
		}
		else{
			user = new User();
			network.quickLogin();
		}
	}
	
	// UI actions
	
	public void register(String username, String password, String displayName){
		network.register(username, password, displayName);
	}
	
	public void quickLogin(){
		network.quickLogin();
	}
	
	public User getUser(){
		return user;
	}
	
	public void login(String username, String password){
		user.username = username;
		user.password = password;
		network.login(username, password);
	}
	
	public void logout(){
		network.logout();
	}
	
	public boolean isLoggedIn(){
		return network.isLoggedIn();
	}
	
	public void startRandomGameSearch(){
		if(!isLoggedIn()){
    		if(Popup.currentPopup != null) Popup.currentPopup.close();
    		Popup.showGenericPopup("Please log in", "You need to be logged in to join a random game.");
    		return;
    	}
		
		// 2 player game per default
		long nonce = new Random().nextLong();
		GameRequest temp = new GameRequest(nonce, "Random game");
		gameRequests.put(nonce, temp);
		
		//TODO insert correct times
		network.findRandomGame(nonce, 604800000l, 604800000l, GameType.TYPE_ALL);
		
		//waitingForRandomGameLabel.setText("Connecting to server...");
		//waitingForRandomGamePopup.show();
		
		MainMenu.getInstance().updateGamesList();
		Main.getInstance().setScreen(MainMenu.getInstance());
	}
	
	public void endTurn(long gameID, int turn, Queue<Command> commands){
		network.endTurn(gameID, turn, commands);
	}
	
	public void hostCustomGame(long timeLimit, String gameName, String password, Map map){
		long nonce = random.nextLong();
		GameRequest temp = new GameRequest(nonce, timeLimit, gameName, password, map);
		gameRequests.put(nonce, temp);
		
		MainMenu.getInstance().updateGamesList();
		Main.getInstance().setScreen(MainMenu.getInstance());
		network.hostCustomGame(nonce, timeLimit, gameName, password, map);
	}
	
	public void hostGameRandom(int width, int height, long timeLimit, String gameName, String password) {
		long nonce = random.nextLong();
		GameRequest temp = new GameRequest(nonce, timeLimit, gameName, password);
		gameRequests.put(nonce, temp);
		
		network.hostRandomGame(nonce, timeLimit, gameName, password, width, height);
		
		MainMenu.getInstance().updateGamesList();
		Main.getInstance().setScreen(MainMenu.getInstance());
	}
	
	public void joinGame(String gameName, String password){
		long nonce = random.nextLong();
		GameRequest temp = new GameRequest(nonce);
		gameRequests.put(nonce, temp);
		MainMenu.getInstance().updateGamesList();
		
		network.joinGame(nonce, gameName, password);
	}

	public void cancelRequest(long nonce){
		gameRequests.remove(nonce);
		MainMenu.getInstance().updateGamesList();
		network.cancelRequest(nonce);
	}
	
	public Collection<GameRequest> getGameRequests(){
		return gameRequests.values();
	}
	
	// Network listener implementation
	
	private INetworkListener getNetworkListenerImplementation(){
		return new INetworkListener() {
			
			@Override
			public void connectionLost(Throwable cause) {
				NetworkDependentTracker.changeLogin(null);
				Gdx.app.log("Main", String.format("Network connection lost due to '%s'.", cause.getMessage()));
			}
			
			@Override
			public void waitOpponentsRandom(long nonce) {
				gameRequests.get(nonce).status = GameRequest.STATUS_WAIT_OPPONENT;
				MainMenu.getInstance().updateGamesList();
			}
			
			@Override
			public void turns(long gameID, int turnStart, Date timestamp, int stateChecksum, Queue<Queue<Command>> turns) {
				long diff = new Date().getTime() - timestamp.getTime();
				Gdx.app.log("Main", String.format("Received %d turns (turn %d to turn %d) for gameID %d. (Last turn submitted: %.1f seconds ago)", turns.size(), turnStart, turnStart + turns.size(), gameID, diff / 1000f));
				
				NetworkGameInstance game = NetworkGameInstance.getGame(gameID);
				
				// Shouldn't happen unless something goes wrong, games are never removed (only overwritten) from the NetworkGameInstance list
				if(game == null) throw new IllegalStateException("Received turns from server for a game that doesn't exist!");
				
				// Check if state is corrupted
				int newStateChecksum = game.lastStateChecksum + turns.hashCode();
				
				if(newStateChecksum != stateChecksum){
					Gdx.app.log("Main", String.format("State checksum did not match! %d (client) != %d (server) Game state is corrupted.", stateChecksum, newStateChecksum));
				}
				else{
					Gdx.app.log("Main", String.format("State checksum matches. (%d)", newStateChecksum));
				}	
				
				game.lastStateChecksum = stateChecksum;
				
				// If this is the currently active game, execute commands immediately
				if(Main.getInstance().getScreen() instanceof GEngine && State.activeState == game.state){
					game.endTurns(turns.size(), timestamp);
					Gdx.app.log("Main", "Game is active, executing turns immediately.");
					
					for(Queue<Command> turn : turns){
						CommandHandler.execute(turn);
					}
				}
				else{
					Gdx.app.log("Main", "Game is NOT active, adding turns to queue.");
					game.queueTurns(turns, turnStart, timestamp);
					MainMenu.getInstance().updateGamesList();
				}
			}
			
			@Override
			public void receivedServerMaps(HashMap<Long, Map> maps) {
				// TODO
				
			}
			
			@Override
			public void randomGameStarted(long gameID, final long seed, long timelimit, final Player[] participants) {
				new NetworkGameInstance(gameID, participants, seed, timelimit).showGame(true);
				if(Popup.currentPopup != null) Popup.currentPopup.close();
			}
			
			@Override
			public void quickLoginRejected(String reason){
				String message = String.format("Quick login rejected, reason: '%s'.", reason);
				Gdx.app.log("Main", message);
				Popup.showGenericPopup("Failed to login", message);
				if(Popup.currentPopup == AccountMenu.getInstance().connectingPopup) Popup.currentPopup.close();
				user = null;
			}
			
			@Override
			public void quickLoginAccepted(long userID, String username, String password){
				Gdx.app.log("Main", String.format("Quick login successful, logged in with username / display name: '%s.'", username));
				
				if(user==null)
					user = new User();
				user.username = username;
				user.password = password;
				user.displayName = username;
				User.globalUserID = userID;
				user.toFile(Gdx.files.local("login.bin"));
				
				if(Popup.currentPopup == AccountMenu.getInstance().connectingPopup) Popup.currentPopup.close();
				
				NetworkDependentTracker.changeLogin(username);
				if(Main.getInstance().getScreen() instanceof AccountMenu)
					Main.getInstance().popScreen();
			}
			
			@Override
			public void loginOverrided(String reason) {
				Gdx.app.log("Main", String.format("Login overrided, user has been logged out. Reason: '%s'.", reason));
				NetworkDependentTracker.changeLogin(null);
			}
			
			@Override
			public void loginDenied(String reason) {
				Gdx.app.log("Main", String.format("Login denied, reason: '%s'.", reason));
				Popup.showGenericPopup("Could not log in", reason);
				if(Popup.currentPopup == AccountMenu.getInstance().connectingPopup) Popup.currentPopup.close();
			}
			
			@Override
			public void loginAccepted(long userID, String displayName) {
				Gdx.app.log("Main", String.format("Login accepted, logged in with display name '%s.'", displayName));
				
				user.displayName = displayName;
				
				// Scrap old gameRequests if user changes
				if(userID != User.globalUserID) {
					gameRequests.clear();
					MainMenu.getInstance().updateGamesList();
				}

				User.globalUserID = userID;
				user.toFile(Gdx.files.local("login.bin"));
				
				if(Popup.currentPopup == AccountMenu.getInstance().connectingPopup) Popup.currentPopup.close();
				
				NetworkDependentTracker.changeLogin(displayName);
				if(Main.getInstance().getScreen() instanceof AccountMenu)
					Main.getInstance().popScreen();
			}
			
			@Override
			public void joinGameSuccess(long nonce, long id, long seed, long limit,
					String name, Map map, int type, long opponentId, String opponentName) {
				
				GameRequest temp = gameRequests.remove(nonce);
				if (temp == null) {
					Gdx.app.log("Main", "Cannot find gamerequest");
				}
				else  {
					GameInstance.createOnlineGame(id, seed, limit, name, map, opponentId, opponentName, false);
					Popup.showGenericPopup("Game joined", "Joined game '"+name+"' against '"+opponentName+"'");
					MainMenu.getInstance().updateGamesList();
					
				}
			}
			
			@Override
			public void joinGameFailed(long nonce, String reason) {
				
				GameRequest gameRequest = gameRequests.remove(nonce);
				
				if(gameRequest != null){
					MainMenu.getInstance().updateGamesList();
					Popup.showGenericPopup(String.format("Error joining game '%s'.", gameRequest.gameName), reason);
				}
				
				Gdx.app.log("Main", "Game join failure");
			}
			
			@Override
			public void hostWaitOpponent(long nonce, String gameName) {
				gameRequests.get(nonce).status = GameRequest.STATUS_WAIT_OPPONENT;
				gameRequests.get(nonce).gameName = gameName;
				
				MainMenu.getInstance().updateGamesList();
			}
			
			@Override
			public void hostGameStart(long nonce, long gameID, long seed, String gameName, long opponentId, String opponentName) {		
				GameRequest temp = gameRequests.remove(nonce);
				
				if (temp == null) {
					Gdx.app.log("Network", "Error: GAME_HOST_START: No gamerequest was found");
				}
				else {
					// public void hostGameStart(long nonce, long id, long seed, String gameName, long opponentId, String opponentName) {
					GameInstance.createOnlineGame(gameID, seed, temp.timeLimit, gameName, temp.map, opponentId, opponentName, true);
					MainMenu.getInstance().updateGamesList();
					Gdx.app.log("Network", "GAME_HOST_START: "+opponentName+" has joined game");
					Popup.showGenericPopup("Game started", opponentName+" has joined your game");
				}
			}
			
			@Override
			public void hostGameRandom(long nonce, long gameID, long seed, long limit, String gameName, Map map, int type, long opponentId, String opponentName) {
				GameRequest temp = gameRequests.remove(nonce);
				
				if (temp == null) {
					Gdx.app.log("Network", "Error: GAME_RANDOM_HOST: No gamerequest was found");
				}
				else {
					GameInstance.createOnlineGame(gameID, seed, limit, gameName, map, opponentId, opponentName, true);
					MainMenu.getInstance().updateGamesList();
					Gdx.app.log("Network", "GAME_RANDOM_HOST: "+opponentName+" has joined game");
					Popup.showGenericPopup("Game started", opponentName+" has joined your game");
				}
			}
			
			@Override
			public void hostGameFailed(long nonce, String reason) {
				gameRequests.remove(nonce);
				MainMenu.getInstance().updateGamesList();
				Popup.showGenericPopup("Error hosting game", reason);
			}
			
			@Override
			public void generateMap(long nonce, String name, int width, int height, long seed) {
				Map map = new Map(width,height,new Random(seed));
				gameRequests.get(nonce).map = map;
				MainMenu.getInstance().updateGamesList();
				network.mapGenerated(name, map);
			}
			
			@Override
			public void commandRefused(final String cause){
				Popup.showGenericPopup("Command refused", cause);
			}
		};
	}
}
