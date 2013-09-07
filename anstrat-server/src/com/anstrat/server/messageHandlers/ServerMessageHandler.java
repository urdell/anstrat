package com.anstrat.server.messageHandlers;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;

/**
 * The main network message handler, retrieves the message content and then delegates it to: <br>
 * {@link AuthMessageHandler} - login and login related messages<br>
 * {@link GameMessageHandler} - all messages related to gameplay<br>
 * {@link SocialMessageHandler} - player to player communication
 *
 */
public class ServerMessageHandler {

	@Inject
	private Logger logger;
	
	private final AuthMessageHandler authMessageHandler = new AuthMessageHandler();
	private final GameMessageHandler gameMessageHandler = new GameMessageHandler();
	private final SocialMessageHandler socialMessageHandler =  new SocialMessageHandler();
	
	/**
	 * Checks the message's validity with regards to contents and delegates tasks further.
	 * @param source The source of the message.
	 * @param message The NetworkMessage that was received.
	 */
	public void handleMessage(InetSocketAddress client, NetworkMessage message){
		com.anstrat.network.protocol.NetworkMessage.Command networkCommand = message.getCommand();
		List<Serializable> payload = message.getPayload();
		
		try{
			switch(networkCommand){
			
				// Authentication (login-related) messages
				case LOGIN: {
					long userID = (Long) payload.get(0);
					String password = (String) payload.get(1);
					long versionNr = payload.size() >= 3 ? (Long) payload.get(3) : -1l;
					authMessageHandler.login(client, userID, password, versionNr);
					break;
				}
				case CREATE_NEW_USER: {
					authMessageHandler.createNewUser(client);
					break;
				}
				case SET_DISPLAY_NAME: {
					String name = (String) payload.get(0);
					authMessageHandler.setDisplayName(client, name);
					break;
				}
				
				// Game related messages
				case REQUEST_GAME_UPDATE: {
					long gameID = (Long) payload.get(0);
					int currentCommandNr = (Integer) payload.get(1);
					gameMessageHandler.requestGameUpdate(client, gameID, currentCommandNr);
					break;
				}
				case REQUEST_RANDOM_GAME: {
					GameOptions options = (GameOptions) payload.get(0);
					gameMessageHandler.requestRandomGame(client, options);
					break;
				}
				case SEND_COMMAND: {
					long gameID = (Long) payload.get(0);
					int commandNr = (Integer) payload.get(1);
					Command command = (com.anstrat.command.Command) payload.get(2);
					gameMessageHandler.command(client, gameID, commandNr, command);
					break;
				}
				case RESIGN: {
					long gameID = (Long) payload.get(0);
					int playerID = (Integer) payload.get(1);
					gameMessageHandler.resign(client, gameID, playerID);
					break;
				}
				
				// Social messages
				case INVITE_PLAYER_NAME: {
					String name = (String) payload.get(0);
					GameOptions options = (GameOptions) payload.get(1);
					socialMessageHandler.invitePlayerByName(client, name, options);
					break;
				}
				case INVITE_PLAYER_ID: {
					long userID = (Integer) payload.get(0);
					GameOptions options = (GameOptions) payload.get(1);
					socialMessageHandler.invitePlayerByID(client, userID, options);
					break;
				}
				case INVITE_ACCEPT: {
					long inviteID = (Long) payload.get(0);
					int team = (Integer) payload.get(1);
					int god = (Integer) payload.get(2);
					socialMessageHandler.inviteAccept(client, inviteID, team, god);
					break;
				}
				case INVITE_DECLINE: {
					long inviteID = (Long) payload.get(0);
					socialMessageHandler.inviteDecline(client, inviteID);
					break;
				}
				default: {
					logger.error("Received unknown client command '%s', command ignored.", networkCommand);
					break;
				}
			}
		}
		catch(ClassCastException e){
			logger.error("Unexpected payload type in message '%s'. Reason: '%s'.", networkCommand, e.getMessage());
		}
		catch(IndexOutOfBoundsException e){
			logger.error("Missing payload in message '%s'. Reason: '%s'.", networkCommand, e.getMessage());
		}
	}
}
