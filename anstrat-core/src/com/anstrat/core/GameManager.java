package com.anstrat.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.anstrat.core.NetworkGameInstance.NetworkPlayer;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.Map;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Manages active and pending games.
 * @author Erik
 *
 */
public class GameManager {
	private List<GameInstance> games = new ArrayList<GameInstance>();
	private java.util.Map<Long, NetworkGameInstance> networkGameByID = new HashMap<Long, NetworkGameInstance>();
	
	public GameInstance createAIGame(Map map, Integer... aiPlayerIds){
		GameInstance gi = createHotseatGame(map);
		gi.setAIPlayers(aiPlayerIds);
		return gi;
	}
	
	/**
	 * @param map a map or null to generate a random map
	 * @return
	 */
	public GameInstance createHotseatGame(Map map){
		return createHotseatGame(map, 2);
	}
	
	public GameInstance createHotseatGame(Map map, int numPlayers){
		
		Player[] players = new Player[numPlayers];
		int team = 0;
		
		for(int i = 0; i < players.length; i++){
			players[i] = new Player(i, "Player " + i, team, Player.getRandomGodFromTeam(team));
			
			// Toggle team
			team = (team + 1) % UnitType.TEAMS.length;
		}
		
		// If no map given, create a random one
		if(map == null) map = new Map(10, 10, new Random());
		
		GameInstance gi = new GameInstance(games.size() + 1, map, players);
		games.add(gi);
		
		return gi;
	}
	
	public GameInstance createNetworkGame(long gameID, NetworkPlayer[] players, Map map, long seed){
		NetworkGameInstance gi = new NetworkGameInstance(gameID, players, map, seed);
		
		networkGameByID.put(gameID, gi);
		games.add(gi);
		
		return gi;
	}
	
	public void endGame(GameInstance game){
		this.games.remove(game);
	}
	
	public NetworkGameInstance getGame(long gameID){
		return networkGameByID.get(gameID);
	}
	
	public Collection<GameInstance> getActiveGames(){
		return this.games;
	}
	
	public void saveGameInstances(FileHandle handle){
		Serialization.writeObject(new GameInstanceList(games), handle);
	}
	
	public void loadGameInstances(FileHandle handle){
		Object obj = Serialization.readObject(handle);
		
		if(obj == null){
			Gdx.app.log("GameInstance", "No previous game instances found.");
		}
		else{
			games = ((GameInstanceList)obj).games;
			
			for(GameInstance gi : games){
				if(gi instanceof NetworkGameInstance){
					NetworkGameInstance ngi = (NetworkGameInstance) gi;
					networkGameByID.put(ngi.getGameID(), ngi);
				}
			}
		}
	}
	
	// Class used only to serialize/deserialize game instances
	private static class GameInstanceList implements Serializable {
		private static final long serialVersionUID = 1L;
		private List<GameInstance> games;
		
		public GameInstanceList(List<GameInstance> games){
			this.games = games;
		}

		@Override
		public String toString() {
			return String.format("%s(size = %d)", this.getClass().getSimpleName(), games.size());
		}
	}
}
