package com.anstrat.server.matchmaking;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.anstrat.geography.Map;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.server.IClientEventListener;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.DatabaseMethods;
import com.anstrat.server.db.User;

public class SimpleGameMatcher implements IClientEventListener {

	private Queue<WaitingPlayer> userQueue = new LinkedList<WaitingPlayer>();
	private HashMap<Long, WaitingPlayer> userToPlayer = new HashMap<Long, WaitingPlayer>();
	private Object lock = new Object();
	private final IConnectionManager connectionManager;
	
	public SimpleGameMatcher(IConnectionManager connectionManager){
		this.connectionManager = connectionManager;
		connectionManager.addClientEventListener(this);
	}

	// Adds the user to the game queue.
	// May return a GameSetup of paired users 
	public GameSetup addUserToQueue(long userID, int team, int god){
		ArrayList<WaitingPlayer> players = new ArrayList<WaitingPlayer>();
		
		synchronized(lock){
			WaitingPlayer waitingPlayer = new WaitingPlayer(userID, god, team);
			userQueue.add(waitingPlayer);
			userToPlayer.put(userID, waitingPlayer);
			
			if(userQueue.size() >= 2){
				players.add(userQueue.poll());
				players.add(userQueue.poll());
			}
		}
		
		if(players.size() > 0){
			// Get display names
			Long[] userIDs = new Long[players.size()];
			
			for(int i = 0; i < players.size(); i++){
				userIDs[i] = players.get(i).userID;
			}
			
			java.util.Map<Long, User> users = DatabaseMethods.getUsers(userIDs);
			
			// Create GameSetup
			GameSetup.Player[] gameSetupPlayers = new GameSetup.Player[players.size()];
			
			for(int i = 0; i < gameSetupPlayers.length; i++){
				WaitingPlayer player = players.get(i);
				gameSetupPlayers[i] = new GameSetup.Player(player.userID, player.team, player.god, users.get(player.userID).getDisplayedName());
			}

			return new GameSetup(new Map(10, 10), new Random().nextLong(), gameSetupPlayers);
		}
		
		return null;
	}
	
	@Override
	public void clientConnected(InetSocketAddress address) {
		
	}

	@Override
	public void clientDisconnected(InetSocketAddress address) {
		
		long userID = connectionManager.getUserID(address);
		
		// If user was logged in
		if(userID != -1){
			
			// Check if user was in queue for a game, and if so remove him
			synchronized(lock){
				WaitingPlayer existing = userToPlayer.get(userID);
				
				if(existing != null){
					userQueue.remove(existing);
					userToPlayer.remove(existing);
				}
			}
		}
	}

	@Override
	public void clientAuthenticated(InetSocketAddress address, long userID) {
		
	}
	
	private class WaitingPlayer {
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
