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
import com.anstrat.menu.MainMenu;
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
	
	public GameInstance createAIGame(Map map, int player1team, Integer... aiPlayerIds){
		GameInstance gi = createHotseatGame(map, player1team, Player.getRandomTeam());
		gi.setAIPlayers(aiPlayerIds);
		return gi;
	}
	public GameInstance createAIGame(boolean fog, int sizeX, int sizeY, int player1team,
			Integer... aiPlayerIds) {
		Map map = new Map(sizeX, sizeY, new Random());
		map.fogEnabled = fog;
		GameInstance gi = createHotseatGame(map, player1team, Player.getRandomTeam());
		gi.setAIPlayers(aiPlayerIds);
		return gi;
	}
	
	public GameInstance createNetworkGame(long gameID, NetworkPlayer[] players, Map map, long seed){
		NetworkGameInstance gi = new NetworkGameInstance(gameID, players, map, seed);
		
		MainMenu.getInstance().pendingGames = 0;
		
		
		networkGameByID.put(gameID, gi);
		games.add(gi);
		
		return gi;
	}
	
	public GameInstance createHotseatGame(int width, int height, Player[] players){
		GameInstance gi = new GameInstance(games.size() + 1, new Map(width, height, new Random()), players, GameInstanceType.HOTSEAT);
		games.add(gi);
		
		return gi;
	}
	
	public GameInstance createHotseatGame(Map map, Player[] players) {
		// If no map given, create a random one
		if(map == null) map = new Map(10, 10, new Random());
		
		GameInstance gi = new GameInstance(games.size() + 1, map, players, GameInstanceType.HOTSEAT);
		games.add(gi);
		
		return gi;
	}
	
	public GameInstance createHotseatGame(Map map, int player1team, int player2team){
		Player[] players = new Player[]{
			new Player(0, "Player 1", player1team),
			new Player(1, "Player 2", player2team)
		};

		return createHotseatGame(map, players);
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
