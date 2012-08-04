package com.anstrat.server.matchmaking;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.anstrat.geography.Map;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.server.IClientEventListener;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.DatabaseMethods;

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
		WaitingPlayer player1 = null, player2 = null;
		
		synchronized(lock){
			WaitingPlayer waitingPlayer = new WaitingPlayer(userID, god, team);
			userQueue.add(waitingPlayer);
			userToPlayer.put(userID, waitingPlayer);
			
			if(userQueue.size() >= 2){
				player1 = userQueue.poll();
				player2 = userQueue.poll();
			}
		}
		
		if(player1 != null && player2 != null){
			// Get display names
			String[] displayNames = DatabaseMethods.getDisplayNames(new long[]{player1.userID, player2.userID});
			
			// Create GameSetup
			GameSetup.Player[] players = new GameSetup.Player[]{
				new GameSetup.Player(player1.userID, player1.team, player1.god, displayNames[0]),
				new GameSetup.Player(player2.userID, player2.team, player2.god, displayNames[1]),
			};
			return new GameSetup(new Map(10, 10), new Random().nextLong(), players);
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
