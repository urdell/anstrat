package com.anstrat.core;

import com.anstrat.command.Command;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;

public class NetworkGameInstance extends GameInstance {
	
	private static final long serialVersionUID = 3L;
	
	private long gameID;
	
	public NetworkGameInstance(long gameID, NetworkPlayer[] players, Map map, long seed){
		super(map, players, seed);
		this.gameID = gameID;
	}
	
	@Override
	public int getTurnNumber(){
		return state.turnNr;
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
	
	public static class NetworkPlayer extends Player {
		
		private static final long serialVersionUID = 1L;
		public final long userID;
		
		public NetworkPlayer(long userID, int playerID, String displayName, int team, int god) {
			super(playerID, displayName, team, god);
			this.userID = userID;
		}
	}
}
