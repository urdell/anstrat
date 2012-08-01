package matchmaking;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * A queue for waiting players, matching them to games.
 */
public class GameQueue {

	// Index = numPlayers - minNumPlayers
	// if minNumPlayers = 2:
	// [0] = 2 player game
	// [1] = 3 player game
	// [2] = 4 player game
	private Queue<WaitingPlayer>[] players;
	
	private Map<String, WaitingPlayer> playerToWaitingPlayer; 
	
	private int indexOffset;
	
	/**
	 * 
	 * @param minNumPlayers the minimum accepted value of numPlayers
	 * @param maxNumPlayers the maximum accepted value of numPlayers
	 */
	@SuppressWarnings("unchecked")
	public GameQueue(int minNumPlayers, int maxNumPlayers){
		if(minNumPlayers < 0) throw new IllegalArgumentException("minNumPlayers must be greater than 0.");
		if(maxNumPlayers < minNumPlayers) throw new IllegalArgumentException("maxNumPlayers must be greater than or equal to minNumPlayers.");
		players = new Queue[maxNumPlayers - minNumPlayers + 1];
		playerToWaitingPlayer = new HashMap<String, WaitingPlayer>();
		indexOffset = minNumPlayers;
	}
	
	/**
	 * @param username a String representing the player
	 * @param numPlayers the game size the player has requested
	 * @param timelimit the turn time limit the player has requested
	 * @return <code>true</code> if the player was added to the queue 
	 */
	public synchronized void addPlayer(String player, int numPlayers, long timelimit){
		int index = getIndex(numPlayers);
		if(players[index] == null) players[index] = new LinkedList<WaitingPlayer>();
		
		WaitingPlayer waitingPlayer;
		
		if(playerToWaitingPlayer.containsKey(player)){
			// if already waiting, remove player from old waiting list
			waitingPlayer = playerToWaitingPlayer.get(player);
			int oldIndex = getIndex(waitingPlayer.requestedNumPlayers);
			players[oldIndex].remove(waitingPlayer);
		}
		else{
			waitingPlayer = new WaitingPlayer();
		}

		playerToWaitingPlayer.put(player, waitingPlayer);
		players[index].add(waitingPlayer);
		waitingPlayer.player = player;
		waitingPlayer.requestedTimelimit = timelimit;
		waitingPlayer.requestedNumPlayers = numPlayers;
	}
	
	public synchronized void removePlayer(String player){
		WaitingPlayer waitingPlayer = playerToWaitingPlayer.remove(player);
		if(waitingPlayer == null) return;	// Player does not exist in queue
		
		for(int i = 0; i < players.length; i++){
			if(players[i] == null) continue;
			players[i].remove(waitingPlayer);
		}
	}
	
	private int getIndex(int numPlayers){
		return numPlayers - indexOffset;
	}
	
	/**
	 * Tries to match the players currently in the queue returning a game that should be started.
	 * Returns null if there's too few players in the queue to start any game.
	 * The players returned will have been removed from the queue.
	 */
	public synchronized GameMatch findMatch(){
		// TODO: Magical algorithm that always matches all of the waiting players' wishes
		
		// Currently just matches players that have the same requested number of players
		for(int i = 0; i < players.length; i++){
			int numPlayers = i + indexOffset;
			
			// No players has been added to this numPlayer queue, just continue
			if(players[i] == null) continue;
			
			if(players[i].size() >= numPlayers){
				
				String[] matchedPlayers = new String[numPlayers];
				long timelimit = Long.MAX_VALUE;
				
				// Retrieve the first 'numPlayers' players
				for(int j = 0; j < numPlayers; j++){
					WaitingPlayer waitingPlayer = players[i].poll();
					
					matchedPlayers[j] = waitingPlayer.player;
					if(waitingPlayer.player == null) System.out.println("PLAYER IS NULL NULL NULL");
					timelimit = Math.min(timelimit, waitingPlayer.requestedTimelimit);
				}
				
				System.out.println(matchedPlayers + "; length = " + matchedPlayers.length);
				for(String p : matchedPlayers){
					System.out.println(p);
				}
				return new GameMatch(matchedPlayers, timelimit);
			}
		}
		
		return null;
	}
	
	private static class WaitingPlayer {
		private String player;
		private long requestedTimelimit;
		private int requestedNumPlayers;
	}
	
	public static class GameMatch {
		public final String[] players;
		public final long timelimit;
		
		private GameMatch(String[] players, long timelimit){
			this.players = players;
			this.timelimit = timelimit;
		}
	}
}
