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

public class SimpleGameMatcher {

	private Queue<WaitingPlayer> userQueue = new LinkedList<WaitingPlayer>();
	private HashMap<Long, WaitingPlayer> userToPlayer = Maps.newHashMap();
	private Object lock = new Object();
	
	@Inject
	private Logger logger;
	
	@Inject
	private IConnectionManager connectionManager;
	
	@Inject
	private IDatabaseService database;
	
	public SimpleGameMatcher(){
		Event.register(this);
	}

	// Adds the user to the game queue.
	// May return a GameSetup of paired users 
	public GameSetup addUserToQueue(long userID, int team, int god){
		ArrayList<WaitingPlayer> players = new ArrayList<WaitingPlayer>();
		
		synchronized(lock){
			if(userToPlayer.containsKey(userID)){
				// User is already in queue, don't add twice
				logger.info("Ignoring random game request, user '%d' is already in queue. (queue size = %d)", userID, userQueue.size());
				return null;
			}
			
			WaitingPlayer waitingPlayer = new WaitingPlayer(userID, god, team);
			userQueue.add(waitingPlayer);
			userToPlayer.put(userID, waitingPlayer);
			logger.info("Added user '%d' to random game queue. (queue size = %d)", userID, userQueue.size());
			
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

			return new GameSetup(new Map(10, 10, new Random()), new Random().nextLong(), gameSetupPlayers);
		}
		
		return null;
	}

	@Subscribe
	public void clientDisconnected(ClientDisconnectedEvent event) {
		
		long userID = connectionManager.getUserID(event.getClient());
		
		// If user was logged in
		if(userID != -1){
			
			// Check if user was in queue for a game, and if so remove him
			synchronized(lock){
				WaitingPlayer existing = userToPlayer.remove(userID);
				
				if(existing != null){
					userQueue.remove(existing);
					logger.info("Removed user '%d' from queue, user disconnected.", userID);
				}
			}
		}
	}
	
	private static class WaitingPlayer {
		public final long userID;
		public final int god;
		public final int team;
		
		public WaitingPlayer(long userID, int god, int team) {
			this.userID = userID;
			this.god = god;
			this.team = team;
		}
	}
}
