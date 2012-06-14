package com.anstrat.core;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;

public class NetworkGameInstance extends GameInstance {
	
	private static final long serialVersionUID = 2L;
	static java.util.Map<Long, NetworkGameInstance> gameByID = new HashMap<Long, NetworkGameInstance>();
	
	private long gameID;
	private long timelimit;	// ms
	private Date turnEndTime;
	public int lastStateChecksum = 0;
	
	// Which index (0 to numPlayers - 1) the user has been assigned to.
	private int userPlayerIndex;
	
	// Turns that have been received from a server but not executed on the state yet
	private Queue<Queue<Command>> scheduledTurns;
	
	// The "effective" turnNo, including all scheduled turns
	private int turnNo = 1;
	
	public NetworkGameInstance(long gameID, Player[] players, long seed, long timelimit){
		this(gameID, players, new Map(10, 10, new Random(seed)), seed, timelimit);
	}
	
	public NetworkGameInstance(long gameID, Player[] players, Map map, long seed, long timelimit){
		super(map, players, seed);
		
		this.gameID = gameID;
		this.timelimit = timelimit;
		
		this.turnEndTime = new Date(new Date().getTime() + timelimit);
		
		scheduledTurns = new LinkedList<Queue<Command>>();
		
		// Find the user's playerID, will fall back to 0
		for(Player player : players){
			if(player.userID == User.globalUserID){
				userPlayerIndex = player.playerId;
			}
		}
		
		GameInstance.gamesList.add(this);
		gameByID.put(gameID, this);
	}
	
	public synchronized void queueTurns(Queue<Queue<Command>> turns, int turnStart, Date timestamp){
		
		this.turnEndTime = new Date(timestamp.getTime() + timelimit);
		
		// Make sure we don't add a turn twice
		int i = turnStart;
		int addedCount = 0;
		
		// Ignore overlapping turns (turns we've already got)
		for(Queue<Command> turn : turns){
			if(i >= turnNo){
				scheduledTurns.add(turn);
				addedCount++;
			}
			
			i++;
		}
		
		turnNo += addedCount;
	}
	
	/**
	 * Returns the which EndTurnCommand to poll for, or -1 if <br>
	 * this game should be skipped. 
	 * For example a 1 means we should poll for the end of turn 1.
	 * @return
	 */
	public int getNextTurnToPoll(){
		int currentPlayer = (turnNo - 1) % state.players.length;
		
		// Only poll if it's not our turn, or if our turn has expired
		boolean poll = currentPlayer != userPlayerIndex || new Date().after(turnEndTime);
		
		return !poll ? -1 : turnNo;
	}
	
	public void endTurns(int numTurns, Date timestamp){
		this.turnNo += numTurns;
		this.turnEndTime = new Date(timestamp.getTime() + timelimit);
	}
	
	public void endTurn(){
		this.turnNo++;
		this.turnEndTime = new Date(new Date().getTime() + timelimit);
	}
	
	@Override
	public int getTurnNumber(){
		return this.turnNo;
	}
	
	@Override
	public Player getCurrentPlayer(){
		int currentPlayerIndex = (turnNo - 1) % state.players.length; 
		return state.players[currentPlayerIndex];
	}
	
	@Override
	public void showGame(boolean startZoom){
		super.showGame(startZoom);
		
		Queue<Queue<Command>> copy;
		synchronized(this){
			copy = new LinkedList<Queue<Command>>(scheduledTurns);
			scheduledTurns.clear();
		}
		
		// Execute all turns
		for(Queue<Command> turn : copy){
			CommandHandler.execute(turn);
		}
	}
	
	public Date getTurnEndTime(){
		return turnEndTime;
	}
	
	public long getGameID(){
		return this.gameID;
	}
	
	public static NetworkGameInstance getGame(long gameID){
		return gameByID.get(gameID);
	}
}
