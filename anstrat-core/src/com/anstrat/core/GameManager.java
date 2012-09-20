package com.anstrat.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.anstrat.core.NetworkGameInstance.NetworkPlayer;
import com.anstrat.gameCore.Player;
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
	private FileHandle gamesFile;
	
	public GameManager(FileHandle gamesFile){
		this.gamesFile = gamesFile;
	}
	
	public GameInstance createAIGame(Map map, int player1god, int player1team, Integer... aiPlayerIds){
		GameInstance gi = createHotseatGame(map, player1god, player1team, Player.getRandomGod(), Player.getRandomTeam());
		gi.setAIPlayers(aiPlayerIds);
		return gi;
	}
	
	public GameInstance createHotseatGame(Map map, int player1god, int player1team, int player2god, int player2team){
		return createHotseatGame(map, 2, new int[]{player1god, player2god}, new int[]{player1team, player2team});
	}
	
	public GameInstance createHotseatGame(boolean fog, int sizeX, int sizeY, int player1god, int player1team,
			int player2god, int player2team) {
		Map map = new Map(sizeX, sizeY, new Random());
		map.fogEnabled = fog;
		return createHotseatGame(map, player1god, player1team, player2god, player2team);
	}
	
	public GameInstance createHotseatGame(Map map, int numPlayers, int[] gods, int[] teams){
		
		Player[] players = new Player[numPlayers];
		
		for(int i = 0; i < players.length; i++){
			players[i] = new Player(i, "Player " + i, teams[i], gods[i]);
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
	
	public void clear(){
		this.games.clear();
		gamesFile.delete();
	}
	
	public Collection<GameInstance> getActiveGames(){
		return this.games;
	}
	
	public void saveGameInstances(){
		Serialization.writeObject(new GameInstanceList(games), gamesFile);
	}
	
	public void loadGameInstances(){
		Object obj = Serialization.readObject(gamesFile);
		
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
