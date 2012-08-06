package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.IDatabaseService;
import com.anstrat.server.db.Player;
import com.anstrat.server.matchmaking.SimpleGameMatcher;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;

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
		
		// Add command to database
		database.createCommand(gameID, commandNr, command);
		
		// Get players in game
		Player[] players = database.getPlayers(gameID);
		
		if(players != null){
			// Broadcast command to all players (except the source)
			NetworkMessage message = new NetworkMessage(NetworkMessage.Command.SEND_COMMAND, gameID, commandNr, command);
			
			for(Player player : players){
				connectionManager.sendMessage(player.userID, message);
			}
		}
		else{
			logger.info("Received command for game '%d', but that game does not exist.", gameID);
		}
		
	}
	
	public void requestGameUpdate(InetSocketAddress client, long gameID, int currentCommandNr, int stateChecksum){
		// May trigger one or many SEND_COMMAND or a single GAME_STATE_CORRUPTED
	}
	
	public void requestRandomGame(InetSocketAddress client, int team, int god){
		Long userID = connectionManager.getUserID(client);
		
		if(userID == null){
			logger.info("%s attempted to start a random game, but is not logged in.", client);
			return;
		}
		
		GameSetup game = matcher.addUserToQueue(userID, team, god);
		
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
}
