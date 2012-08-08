package com.anstrat.server.matchmaking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import com.anstrat.geography.Map;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.IDatabaseService;
import com.anstrat.server.db.User;
import com.anstrat.server.events.ClientDisconnectedEvent;
import com.anstrat.server.events.Event;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.common.primitives.Longs;

public class SassyGameMatcher {

	private Queue<WaitingPlayer> small_random = new LinkedList<WaitingPlayer>();
	private Queue<WaitingPlayer> medium_random = new LinkedList<WaitingPlayer>();
	private Queue<WaitingPlayer> large_random = new LinkedList<WaitingPlayer>();
	private Queue<WaitingPlayer> small_premade = new LinkedList<WaitingPlayer>();
	private Queue<WaitingPlayer> medium_premade = new LinkedList<WaitingPlayer>();
	private Queue<WaitingPlayer> large_premade = new LinkedList<WaitingPlayer>();
	
	private HashMap<Long, WaitingPlayer> userToPlayer = Maps.newHashMap();
	private Object lock = new Object();
	
	@Inject
	private Logger logger;
	
	@Inject
	private IConnectionManager connectionManager;
	
	@Inject
	private IDatabaseService database;
	
	public SassyGameMatcher(){
		Event.register(this);
	}

	// Adds the user to the game queue.
	// May return a GameSetup of paired users 
	public GameSetup addUserToQueue(long userID, int team, int god, int gametype){
		ArrayList<WaitingPlayer> players = new ArrayList<WaitingPlayer>();
		
		synchronized(lock){
			if(userToPlayer.containsKey(userID)){
				// User is already in queue, don't add twice
				logger.info("Ignoring random game request, user '%d' is already in queue.", userID);
				return null;
			}
			
			Queue<WaitingPlayer> userQueue = getQueueForGametype(gametype);
			
			WaitingPlayer waitingPlayer = new WaitingPlayer(userID, god, team, gametype);
			userQueue.add(waitingPlayer);
			userToPlayer.put(userID, waitingPlayer);
			logger.info("Added user '%d' to game queue. (queue size = %d)", userID, userQueue.size());
			
			if(userQueue.size() >= 2){
				for(int i = 0; i < 2; i++){
					WaitingPlayer player = userQueue.poll();
					players.add(player);
					userToPlayer.remove(player);
				}
			}
		}
		
		if(players.size() > 0){
			// Get display names
			List<Long> userIDs = Lists.newArrayList();
			for(WaitingPlayer player : players) userIDs.add(player.userID);
			java.util.Map<Long, User> users = database.getUsers(Longs.toArray(userIDs));
			
			// Create GameSetup
			GameSetup.Player[] gameSetupPlayers = new GameSetup.Player[players.size()];
			
			for(int i = 0; i < gameSetupPlayers.length; i++){
				WaitingPlayer player = players.get(i);
				gameSetupPlayers[i] = new GameSetup.Player(player.userID, player.team, player.god, users.get(player.userID).getDisplayedName());
			}
			
			return new GameSetup(getMapForGametype(gametype), new Random().nextLong(), gameSetupPlayers);
		}
		
		return null;
	}

	public Map getMapForGametype(int gametype)
	{
		switch(gametype)
		{
			case GameSetup.gametype_small_random:
				return new Map(10, 10, new Random());
			case GameSetup.gametype_medium_random:
				return new Map(16, 16, new Random());
			case GameSetup.gametype_large_random:
				return new Map(24, 24, new Random());
			case GameSetup.gametype_small_premade:
				return new Map(10, 10, new Random());
			case GameSetup.gametype_medium_premade:
				return new Map(16, 16, new Random());
			case GameSetup.gametype_large_premade:
				return new Map(24, 24, new Random());
			default:
				return new Map(10, 10, new Random());
		}
	}
	
	public Queue<WaitingPlayer> getQueueForGametype(int gametype)
	{
		switch(gametype)
		{
			case GameSetup.gametype_small_random:
				return small_random;
			case GameSetup.gametype_medium_random:
				return medium_random;
			case GameSetup.gametype_large_random:
				return large_random;
			case GameSetup.gametype_small_premade:
				return small_premade;
			case GameSetup.gametype_medium_premade:
				return medium_premade;
			case GameSetup.gametype_large_premade:
				return large_premade;
			default:
				return null;
		}
	}
	
	@Subscribe
	public void clientDisconnected(ClientDisconnectedEvent event) {
		
		Long userID = connectionManager.getUserID(event.getClient());
		
		// If user was logged in
		if(userID != null){
			
			// Check if user was in queue for a game, and if so remove him
			synchronized(lock){
				WaitingPlayer existing = userToPlayer.remove(userID);
				
				if(existing != null){
					getQueueForGametype(existing.gametype).remove(existing);					
					
					logger.info("Removed user '%d' from all queues, user disconnected.", userID);
				}
			}
		}
	}
	
	private static class WaitingPlayer {
		public final long userID;
		public final int god;
		public final int team;
		public final int gametype;
		
		public WaitingPlayer(long userID, int god, int team, int gametype) {
			this.userID = userID;
			this.god = god;
			this.team = team;
			this.gametype = gametype;
		}
	}
}