package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.IDatabaseService;
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
		// Triggers a SEND_COMMAND to each other client.
	}
	
	public void requestGameUpdate(InetSocketAddress client, long gameID, int currentCommandNr, int stateChecksum){
		// May trigger one or many SEND_COMMAND or a single GAME_STATE_CORRUPTED
	}
	
	public void requestRandomGame(InetSocketAddress client, int team, int god){
		long userID = connectionManager.getUserID(client);
		if(userID == -1){
			logger.info("%s attempted to start a random game, but is not logged in.", client);
			return;
		}
		
		GameSetup game = matcher.addUserToQueue(userID, team, god);
		
		if(game != null){
			// Create game
			long gameID = database.createGame(game);
			
			if(gameID != -1){
				connectionManager.sendMessage(client, new NetworkMessage(NetworkMessage.Command.GAME_STARTED, gameID, game));
			}
			else{
				logger.info("Failed to create game.");
			}
		}
	}
}
