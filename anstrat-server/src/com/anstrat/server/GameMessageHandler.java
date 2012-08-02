package com.anstrat.server;

import java.net.InetSocketAddress;

import com.anstrat.command.Command;

/**
 * All game-related NetworkMessage handling is delegated to this class.
 * @author jay
 *
 */
public class GameMessageHandler {

	private final IConnectionManager connectionManager;
	
	public GameMessageHandler(IConnectionManager connectionManager){
		this.connectionManager = connectionManager;
	}
	
	public void command(InetSocketAddress client, int commandNr, Command command){
		// Triggers a SEND_COMMAND to each other client.
	}
	
	public void requestGameUpdate(InetSocketAddress client, int currentCommandNr, int stateChecksum){
		// May trigger one or many SEND_COMMAND or a single GAME_STATE_CORRUPTED
	}
	
	public void requestRandomGame(InetSocketAddress client, int team){
		// Triggers GAME_STARTED
	}
	
	// gameOptions temporary set to Object until a concrete format is decided upon
	public void invitePlayer(InetSocketAddress client, String opponentDisplayName, int team, Object gameOptions){
		// Triggers INVITE_PENDING
		// Triggers INVITE_REQUEST on other client
		// Triggers INVITE_PROCESSED at a later time.
		// May trigger GAME_STARTED at a later time.
	}
	
	public void answerInvite(InetSocketAddress client, int team, boolean accept){
		
	}
}
