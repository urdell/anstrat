package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;
import java.util.List;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.IDatabaseService;
import com.anstrat.server.db.Player;
import com.anstrat.server.matchmaking.SimpleGameMatcher;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;
import com.google.common.collect.Lists;

/**
 * All game-related NetworkMessage handling is delegated to this class.
 * @author jay
 *
 */
public class GameMessageHandler {

	@Inject
	private Logger logger;
	
	@Inject
	private IConnectionManager connectionManager;
	
	@Inject
	private IDatabaseService database;
	
	private final SimpleGameMatcher matcher = new SimpleGameMatcher();
	
	public void command(InetSocketAddress client, long gameID, int commandNr, Command command){
		
		GameChecks checks = gameCheck(client, gameID);
		if(checks == null) return;
		
		// Add command to database
		database.createCommand(gameID, commandNr, command);
		
		// Broadcast command to all players (except the source)
		NetworkMessage message = new NetworkMessage(NetworkMessage.Command.SEND_COMMAND, gameID, commandNr, command);
		
		for(Player player : checks.others){
			logger.info("Forwarding %s to player %s.", command.getClass().getSimpleName(), player.displayName);
			connectionManager.sendMessage(player.userID, message);
		}
	}
	
	public void requestGameUpdate(InetSocketAddress client, long gameID, int currentCommandNr){
		
		Long userID = loginCheck(client);
		if(userID == null) return;
		
		// Here we could check if the user actually belongs to the gameID,
		// but it would require an extra database query and there's no reason to check it
		
		// Send commands
		Command[] commands = database.getCommands(gameID, currentCommandNr);
		for(int i = 0; i < commands.length; i++){
			connectionManager.sendMessage(client, new NetworkMessage(NetworkMessage.Command.SEND_COMMAND, gameID, currentCommandNr + i, commands[i]));
		}
	}
	
	public void requestRandomGame(InetSocketAddress client, GameOptions options){
		Long userID = loginCheck(client);
		if(userID == null) return;
		
		GameSetup game = matcher.addUserToQueue(userID, options.team, options.god);
		
		// Create game
		if(game != null){
			Long gameID = database.createGame(game);
			
			if(gameID != null){
				// Send GAME_STARTED to all players in the game
				NetworkMessage message = new NetworkMessage(NetworkMessage.Command.GAME_STARTED, gameID, game);
				
				for(GameSetup.Player player : game.players){
					connectionManager.sendMessage(player.userID, message);
				}
			}
			else{
				logger.info("Failed to create game.");
			}
		}
	}
	
	public void resign(InetSocketAddress client, long gameID, int playerID){
		GameChecks checks = gameCheck(client, gameID);
		if(checks == null) return;
		
		// Check that the user is not resigning for someone else
		if(checks.userPlayer.playerIndex != playerID){
			logger.warning("%s attempted to resign for someone else.", client);
			return;
		}
		
		// Broadcast PLAYER_RESIGNED to all other plays in game
		NetworkMessage message = new NetworkMessage(NetworkMessage.Command.PLAYER_RESIGNED, gameID, playerID);
		for(Player other : checks.others){
			connectionManager.sendMessage(other.userID, message);
		}
	}
	
	private Player findUser(long userID, Player[] players){
		for(Player p : players) if(p.userID == userID) return p;
		return null;
	}
	
	private GameChecks gameCheck(InetSocketAddress client, long gameID){
		// Check that user is logged in
		Long userID = loginCheck(client);
		if(userID == null) return null;
		
		// Check that game exists
		Player[] players = database.getPlayers(gameID);
		if(players == null){
			logger.warning("%s: Game '%d' does not exist.", client, gameID);
			return null;
		}
		
		// Check that the client actually plays in the game
		Player userPlayer = findUser(userID, players);
		
		if(userPlayer == null){
			logger.warning("%s does not play in game '%d'.", client, gameID);
			return null;
		}
		
		// Create list of other players
		List<Player> others = Lists.newArrayList();
		for(Player player : players){
			if(player.userID != userID) others.add(player);
		}
		
		return new GameChecks(userPlayer, others.toArray(new Player[others.size()]));
	}
	
	private Long loginCheck(InetSocketAddress client){
		Long userID = connectionManager.getUserID(client);
		if(userID == null){
			logger.warning("%s is not logged in.", client);
		}
		
		return userID;
	}
	 
	private static class GameChecks {
		public final Player userPlayer;
		public final Player[] others;
		
		public GameChecks(Player userPlayer, Player[] others){
			this.userPlayer = userPlayer;
			this.others = others;
		}
	}
}
