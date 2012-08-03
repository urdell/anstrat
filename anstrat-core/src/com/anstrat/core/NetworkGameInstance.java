package com.anstrat.core;

import java.util.HashMap;
import java.util.Random;

import com.anstrat.command.Command;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;

public class NetworkGameInstance extends GameInstance {
	
	private static final long serialVersionUID = 3L;
	static final java.util.Map<Long, NetworkGameInstance> gameByID = new HashMap<Long, NetworkGameInstance>();
	
	private long gameID;
	
	public NetworkGameInstance(long gameID, NetworkPlayer[] players, long seed){
		this(gameID, players, new Map(10, 10, new Random(seed)), seed);
	}
	
	public NetworkGameInstance(long gameID, NetworkPlayer[] players, Map map, long seed){
		super(map, players, seed);
		
		this.gameID = gameID;
		
		GameInstance.gamesList.add(this);
		gameByID.put(gameID, this);
	}
	
	@Override
	public int getTurnNumber(){
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	@Override
	public Player getUserPlayer(){
		long globalUserID = Main.getInstance().network.getGlobalUserID();
		
		for(Player player : state.players){
			NetworkPlayer networkPlayer = (NetworkPlayer) player;
			
			if(networkPlayer.userID == globalUserID){
				return networkPlayer;
			}
		}
		
		// Games where the user is not playing in should not exist
		throw new RuntimeException("NetworkGameInstance does not contain the user player!");
	}
	
	@Override
	public void onCommandExecute(Command command){
		// TODO: Send command over network
	}
	
	public long getGameID(){
		return this.gameID;
	}
	
	public static NetworkGameInstance getGame(long gameID){
		return gameByID.get(gameID);
	}
	
	public static class NetworkPlayer extends Player {
		
		private static final long serialVersionUID = 1L;

		private NetworkPlayer(long userID, int playerID, String displayName, int team, int god) {
			super(playerID, displayName, team, god);
			this.userID = userID;
		}

		public final long userID;
		
		public static NetworkPlayer fromPlayer(Player player, long userID){
			return new NetworkPlayer(userID, player.playerId, player.displayedName, player.team, player.god);
		}
	}
}
