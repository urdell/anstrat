package com.anstrat.network;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import com.anstrat.command.Command;
import com.anstrat.core.GameInstance;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;
import com.anstrat.menu.NetworkDependentTracker;
import com.badlogic.gdx.Gdx;

/**
 * Converts requests from the application to network messages.
 * Converts replies from the server for the rest of application through a {@link INetworkListener}.
 * 
 * @author eriter
 *
 */
public class Network implements Runnable, INetworkReaderListener, IConnectionLostListener {

	private static final long RETRY_DELAY = 5000;	// ms
	private static final long POLL_INTERVAL = 5000; // ms
	private static final int LOGIN_MESSAGE_PRIORITY = 10;
	
	private INetworkListener listener;
	private NetworkWorker reader;
	private NetworkSenderWorker sender;
	private Session session;
	private Thread poller;
	private GameSocket socket;
	
	public Network(INetworkListener listener, String host, int port){
		this.listener = listener;
		
		socket = new GameSocket(host, port, this);
		reader = new NetworkReaderWorker(RETRY_DELAY, socket, this);
		sender = new NetworkSenderWorker(RETRY_DELAY, socket);
	}

	@Override
	public void run(){
		while(!Thread.currentThread().isInterrupted()){
			try {
				Thread.sleep(POLL_INTERVAL);
			} catch (InterruptedException e) {
				Gdx.app.log("Network", "Turn polling thread was interrupted!");
				return;
			} 
			
			// Poll every 'POLL_INTERVAL' milliseconds
			if(!socket.isConnected()) continue;		// Skip if not connected			
			
			Collection<GameInstance> games = Collections.synchronizedCollection(GameInstance.getActiveGames());
			
			synchronized(games){
				for(GameInstance gi : games){
					if(gi instanceof NetworkGameInstance){
						
						NetworkGameInstance ngi = (NetworkGameInstance) gi;
						int pollTurn = ngi.getNextTurnToPoll();
						
						if(pollTurn != -1){
							pollTurns(ngi.getGameID(), pollTurn);
						}
					}
				}
			}
		}
	}
	
	public void start(){
		reader.start();
		sender.start();
		
		if(poller != null) poller.interrupt();
		poller = new Thread(this);
		poller.start();
	}
	
	public void stop(){
		reader.stop();
		sender.stop();
		poller.interrupt();
	}
	
	public void resume(){
		Gdx.app.log("Network", "Resumed network.");
		reader.resume();
		sender.resume();
		
		if(poller != null) poller.interrupt();
		poller = new Thread(this);
		poller.start();
	}
	
	public void pause(){
		Gdx.app.log("Network", "Paused network.");
		reader.pause();
		sender.pause();
		poller.interrupt();
	}
	
	// Network command methods

	/**
	 * Messages the server to end the turn and execute the given command for all opposing players.
	 * @param gameID
	 * @param turn
	 * @param commands
	 */
	public void endTurn(long gameID, int turn, Queue<Command> commands){
		Gdx.app.log("Network", String.format("Attempting to send turn %d for game '%d' containing %d commands.", turn, gameID, commands.size()));
		sendMessage(new NetworkMessage("END_TURN", gameID, turn, (Serializable)commands));
	}
	
	/**
	 * Sends a request to start a random game with the given number of players.<br>
	 * The server will first respond with {@link INetworkListener#waitOpponents()}<br>
	 * and then {@link INetworkListener#randomGameStarted(long, long, long, Player[])} 
	 * when the game is ready to start.
	 * @param numPlayers
	 */
	public void findRandomGame(long nonce, long minTimelimit, long maxTimeLimit, int accept_flags){
		Gdx.app.log("Network", String.format("Attempting to start a random game with %d players " + "and timelimit between to %.1f and %.1f seconds.", 2, minTimelimit / 1000f, maxTimeLimit / 1000f));
		sendMessage(new NetworkMessage("GAME_JOIN_RANDOM", nonce, minTimelimit, maxTimeLimit, accept_flags));
	}
	
	/**
	 * Sends a quick login request (requests the server to register and login<br>
	 * using a generated username and password)
	 * A response will be given through either<br>
	 * {@link INetworkListener#quickLoginAccepted(long, String, String)} or <br>
	 * {@link INetworkListener#quickLoginRejected(String)}
	 * @param username
	 * @param password
	 */
	public void quickLogin(){
		Gdx.app.log("Network", String.format("Attempting to do quicklogin."));
		NetworkMessage loginMessage = new NetworkMessage("QUICK_LOGIN");
		loginMessage.setPriority(LOGIN_MESSAGE_PRIORITY);
		sender.sendMessage(loginMessage);
	}
	
	public void register(String username, String password, String displayedName){
		
	}
	
	/**
	 * Attempts to login using the given username and password.<br>
	 * A response will be given through either<br>
	 * {@link INetworkListener#loginAccepted(long, String)} or <br>
	 * {@link INetworkListener#loginDenied(String)}
	 * @param username
	 * @param password
	 */
	public void login(String username, String password){
		Gdx.app.log("Network", String.format("Attempting to login with username '%s'.", username));
		this.session = new Session(username, password);
		
		// Put login message at the front of the send queue,
		// as there are messages that depend on a successful login.
		NetworkMessage loginMessage = new NetworkMessage("LOGIN", username, password);
		loginMessage.setPriority(LOGIN_MESSAGE_PRIORITY);
		sender.sendMessage(loginMessage);
	}
	
	/**
	 * Sends a request for commands for the given game, starting at the given expected turn.<br>
	 * Server will respond through {@link INetworkListener#turns(long, int, Queue)}.
	 * @param gameID
	 * @param expectedTurn which EndTurnCommand to poll for, ex 1 means poll for the end command of turn 1.
	 */
	public void pollTurns(long gameID, int expectedTurn){
		//Gdx.app.log("Network", String.format("Game %d: Polling for turns >= %d.", gameID, expectedTurn));
		sendMessage(new NetworkMessage("POLL_TURNS", gameID, expectedTurn));
	}
	
	public void cancelRandomGameSearch(){
		sendMessage(new NetworkMessage("GAME_RANDOM_CANCEL"));
	}
	
	public void hostCustomGame(long nonce, long timeLimit, String gameName, String password, Map map){
		Gdx.app.log("Network", String.format("Attempting to host game '%s' with map '%s' and time limit '%d'.", gameName, map.name, (int)timeLimit/1000));
		sendMessage(new NetworkMessage("GAME_HOST_CUSTOM",nonce,timeLimit,gameName,password,map));
	}
	
	public void hostRandomGame(long nonce, long timeLimit, String gameName, String password, int width, int height) {
		Gdx.app.log("Network", String.format("Attempting to host %dx%d game '%s' with random map and time limit '%d'.", 
					width, height, gameName, (int)timeLimit/1000));
		sendMessage(new NetworkMessage("GAME_HOST_RANDOM", nonce, timeLimit, gameName, password, width, height));
	}
	
	public void joinGame(long nonce, String gameName, String password){
		Gdx.app.log("Network", String.format("Attempting to join game '%s'.", gameName));
		sendMessage(new NetworkMessage("GAME_JOIN",nonce,gameName,password));
	}
	
	public void pollServerMaps(){
		Gdx.app.log("Network", "Polling for server maps.");
		sendMessage(new NetworkMessage("POLL_SERVER_MAPS"));
	}

	public void hostDefaultGame(long nonce, long timeLimit, String gameName, String password, long mapId){
		Gdx.app.log("Network", String.format("Attempting to host default game '%s'.", gameName));
		sender.sendMessage(new NetworkMessage("GAME_HOST_DEFAULT",nonce,timeLimit,gameName,password, mapId));
	}
	
	public void hostDefaultRandomGame(long nonce, long timeLimit, String gameName, String password){
		Gdx.app.log("Network", String.format("Attempting to host default random game '%s'.", gameName));
		sendMessage(new NetworkMessage("GAME_HOST_DEFAULT_RANDOM",nonce,timeLimit,gameName,password));
	}
	
	public void mapGenerated(String gameName, Map map){
		Gdx.app.log("Network", String.format("Attempting to send generated map."));
		sendMessage(new NetworkMessage("MAP_GENERATED",gameName,map));
	}
	
	public void cancelRequest(long nonce)
	{
		Gdx.app.log("Network", String.format("Removing game request %d",(int) nonce));
		sendMessage(new NetworkMessage("REMOVE_REQUEST", (Long) nonce));
	}
	
	// Private helper methods
	
	private void sendMessage(NetworkMessage message){
		if(session == null){
			Gdx.app.log("Network", String.format("WARNING: Rejected sending command '%s' - requires user to be logged in.", message.getCommand()));
			return;
		}
		
		session.login();
		sender.sendMessage(message);
	}
	
	public void logout(){
		sender.sendMessage(new NetworkMessage("LOGOUT"));
		NetworkDependentTracker.changeLogin(null);
		session = null;
	}
	
	public boolean isLoggedIn(){
		return session != null;
	}
	
	// Event handlers
	
	@Override
	public void messageReceived(NetworkMessage m) {
		String c = m.getCommand();
		List<Serializable> payload = m.getPayload();
		
		try{
			// Login
			if("ACCEPT_LOGIN".equalsIgnoreCase(c)){
				final String displayName = (String) payload.get(0);
				final long userID = (Long) payload.get(1);
				session.loginAccepted();
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.loginAccepted(userID, displayName);
					}
				});
			}
			else if("DENY_LOGIN".equalsIgnoreCase(c)){
				final String reason = (String) payload.get(0);
				session.loginDenied();
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {	
						listener.loginDenied(reason);
					}
				});
			}
			else if("LOGIN_OVERRIDE".equalsIgnoreCase(c)){
				final String reason = (String) payload.get(0);

				if(session != null) session.invalidate();
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {	
						listener.loginOverrided(reason);
					}
				});
			}
			
			// Quick login
			else if("ACCEPT_QUICK".equalsIgnoreCase(c)){
				final long userID = (Long) payload.get(0);
				final String username = (String) payload.get(1);
				final String password = (String) payload.get(2);
				
				session = new Session(username, password);
				session.loginAccepted();	
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {	
						listener.quickLoginAccepted(userID, username, password);
					}
				});
			}
			else if("REJECT_QUICK".equalsIgnoreCase(c)){
				final String reason = (String) payload.get(0);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {	
						listener.quickLoginRejected(reason);
					}
				});
			}
			
			
			// Poll turns
			else if("TURNS".equalsIgnoreCase(c)){
				
				final long gameID = (Long) payload.get(0);
				final int turnStart = (Integer) payload.get(1);
				final Date timestamp = (Date) payload.get(2);
				final int stateChecksum = (Integer) payload.get(3);
				@SuppressWarnings("unchecked")
				final Queue<Queue<Command>> turns = (Queue<Queue<Command>>) payload.get(4);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {	
						listener.turns(gameID, turnStart, timestamp, stateChecksum, turns);
					}
				});
			}
			
			// Maps
			else if("DEFAULT_MAPS".equalsIgnoreCase(c)){
				final HashMap<Long, Map> maps = new HashMap<Long, Map>();
				for(int i=0; i<payload.size(); i+=2){
					maps.put((Long)payload.get(i), (Map)payload.get(i+1));
					System.err.println("Received map: "+(Long)payload.get(i)+","+((Map)payload.get(i+1)).name);//Debug
				}
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {	
						listener.receivedServerMaps(maps);
					}
				});
			}
			else if("GENERATE_MAP".equalsIgnoreCase(c)){
				final String name = (String)payload.get(0);
				final int width =  (Integer)payload.get(1);
				final int height = (Integer)payload.get(2);
				final long seed =  (Long)payload.get(3);
				final long nonce = (Long)payload.get(4);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {	
						listener.generateMap(nonce, name, width, height, seed);
					}
				});
			}
			
			// Hosting game
			else if("GAME_HOST_FAILURE".equalsIgnoreCase(c)){
				final long nonce = (Long)payload.get(0);
				final String reason = (String)payload.get(1);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.hostGameFailed(nonce, reason);
					}
				});
			}
			else if("GAME_HOST_WAIT_OPPONENT".equalsIgnoreCase(c)){
				final long nonce = (Long)payload.get(0);
				final String gameName = (String)payload.get(1);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.hostWaitOpponent(nonce, gameName);
					}
				});
			}
			else if ("GAME_RANDOM_WAIT_OPPONENT".equalsIgnoreCase(c)) {
				final long nonce = (Long)payload.get(0);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.waitOpponentsRandom(nonce);
					}
				});
			}
			else if("GAME_HOST_START".equalsIgnoreCase(c)){
				final long nonce = (Long)payload.get(0);
				final long gameid = (Long)payload.get(1);
				final long randomseed = (Long)payload.get(2);
				final String gamename = (String)payload.get(3);
				final long opponentid = (Long)payload.get(4);
				final String opponentname = (String)payload.get(5);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.hostGameStart(nonce, gameid, randomseed, gamename, opponentid, opponentname);
					}
				});
			}
			else if("GAME_RANDOM_HOST".equalsIgnoreCase(c)){
				final long nonce = (Long)payload.get(0);
				final long gameid = (Long)payload.get(1);
				final long randomseed = (Long)payload.get(2);
				final long timeLimit = (Long)payload.get(3);
				final String gamename = (String)payload.get(4);
				final Map map = (Map)payload.get(5);
				final int gameType = (Integer)payload.get(6);
				final long opponentid = (Long)payload.get(7);
				final String opponentname = (String)payload.get(8);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.hostGameRandom(nonce, gameid, randomseed, timeLimit, gamename, map, gameType, opponentid, opponentname);
					}
				});
			}
			
			// Joining games
			else if("GAME_JOIN_SUCCESSFUL".equalsIgnoreCase(c)){
				final long nonce = (Long)payload.get(0);
				final long gameid = (Long)payload.get(1);
				final long randomseed = (Long)payload.get(2);
				final long timeLimit = (Long)payload.get(3);
				final String gamename = (String)payload.get(4);
				final Map map = (Map)payload.get(5);
				final Integer gameType = (Integer)payload.get(6);
				final long opponentid = (Long)payload.get(7);
				final String opponentname = (String)payload.get(8);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.joinGameSuccess(nonce, gameid, randomseed, timeLimit, gamename, map, gameType, opponentid, opponentname);
					}
				});
			}
			else if("GAME_JOIN_FAILURE".equalsIgnoreCase(c)){
				final long nonce = (Long)payload.get(0);
				final String reason = (String)payload.get(2);
				
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {	
						listener.joinGameFailed(nonce, reason);
					}
				});
			}
			else if("COMMAND_REFUSED".equalsIgnoreCase(c))
			{
				final String reason = (String) payload.get(0);
				listener.commandRefused(reason);
			}
		}
		catch(ClassCastException e){
			Gdx.app.log("Network", String.format("Unexpected payload type in message '%s'. Reason: '%s'.", c, e.getMessage()));
		}
		catch(IndexOutOfBoundsException e){
			Gdx.app.log("Network", String.format("Missing payload in message '%s'. Reason: '%s'.", c, e.getMessage()));
		}
	}
	
	@Override
	public void connectionLost(final Throwable cause) {
		if(session != null) session.invalidate();
		
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {	
				listener.connectionLost(cause);
			}
		});
	}
	
	// TODO: Store the password more safely in memory
	private class Session {

		private String username;
		private String password;
		private boolean loginInProgress;
		private boolean loggedIn;
		
		public Session(String username, String password){
			this.username = username;
			this.password = password;
		}
		
		public void loginAccepted(){
			this.loginInProgress = false;
			this.loggedIn = true;
		}
		
		public void loginDenied(){
			this.loginInProgress = false;
			this.loggedIn = false;
		}
		
		public void login(){
			if(loggedIn || loginInProgress) return;
			
			this.loginInProgress = true;
			
			// Put login message at the front of the send queue,
			// as there are messages that depend on a successful login.
			NetworkMessage loginMessage = new NetworkMessage("LOGIN", username, password);
			loginMessage.setPriority(LOGIN_MESSAGE_PRIORITY);
			sender.sendMessage(loginMessage);
		}
		
		public void invalidate(){
			Gdx.app.log("Session", "Invalidating session.");
			this.loginInProgress = false;
			this.loggedIn = false;
		}
	}
}
