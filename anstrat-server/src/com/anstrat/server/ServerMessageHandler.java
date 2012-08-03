package com.anstrat.server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;

import com.anstrat.network.NetworkMessage;
import com.anstrat.network.NetworkMessage.Command;
import com.anstrat.server.util.Logger;

public class ServerMessageHandler {

	private static final Logger logger = Logger.getGlobalLogger();
	private final AuthMessageHandler authMessageHandler;
	private final GameMessageHandler gameMessageHandler;
	
	public ServerMessageHandler(IConnectionManager connectionManager){
		this.authMessageHandler = new AuthMessageHandler(connectionManager);
		this.gameMessageHandler = new GameMessageHandler(connectionManager);
	}
	
	/**
	 * Checks the message's validity with regards to contents and delegates tasks further.
	 * @param source The source of the message.
	 * @param message The NetworkMessage that was received.
	 */
	public void handleMessage(InetSocketAddress client, NetworkMessage message){
		Command command = message.getCommand();
		List<Serializable> payload = message.getPayload();
		
		try{
			switch(command){
				case LOGIN: {
					long userID = (Long) payload.get(0);
					String password = (String) payload.get(1);
					authMessageHandler.login(client, userID, password);
					break;
				}
				case CREATE_NEW_USER: {
					authMessageHandler.createNewUser(client);
					break;
				}
				case SET_DISPLAY_NAME: {
					authMessageHandler.setDisplayName(client, null);
					break;
				}
				case REQUEST_GAME_UPDATE: {
					gameMessageHandler.requestGameUpdate(client, -1, -1);
					break;
				}
				case REQUEST_RANDOM_GAME: {
					gameMessageHandler.requestRandomGame(client, -1);
					break;
				}
				case SEND_COMMAND: {
					gameMessageHandler.command(client, -1, null);
					break;
				}
				case INVITE_PLAYER: {
					gameMessageHandler.invitePlayer(client, null, -1, null);
					break;
				}
				case ANSWER_INVITE: {
					gameMessageHandler.answerInvite(client, -1, false);
					break;
				}
				default: {
					logger.error("Received unknown client command '%s'.", command);
					break;
				}
			}
		}
		catch(ClassCastException e){
			logger.error("Unexpected payload type in message '%s'. Reason: '%s'.", command, e.getMessage());
		}
		catch(IndexOutOfBoundsException e){
			logger.error("Missing payload in message '%s'. Reason: '%s'.", command, e.getMessage());
		}
	}
}
