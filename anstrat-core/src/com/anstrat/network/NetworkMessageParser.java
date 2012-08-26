package com.anstrat.network;

import java.io.Serializable;
import java.util.List;

import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.network.protocol.NetworkMessage.Command;
import com.badlogic.gdx.Gdx;

public class NetworkMessageParser {

	public void parseMessage(NetworkMessage message, final INetworkResponseListener listener) {
		Command networkCommand = message.getCommand();
		final List<Serializable> payload = message.getPayload();
		
		try{
			switch(networkCommand){
			
				// Game messages
				case GAME_STARTED: {
					long gameID = (Long) payload.get(0);
					GameSetup game = (GameSetup) payload.get(1);
					listener.gameStarted(gameID, game);
					
					break;
				}
				case SEND_COMMAND: {
					long gameID = (Long) payload.get(0);
					int commandNr = (Integer) payload.get(1);
					com.anstrat.command.Command command = (com.anstrat.command.Command) payload.get(2);
					listener.command(gameID, commandNr, command);
					
					break;
				}
				case PLAYER_RESIGNED: {
					long gameID = (Long) payload.get(0);
					int playerID = (Integer) payload.get(1);
					listener.playerResigned(gameID, playerID);
					
					break;
				}
				
				// Social messages
				case INVITE_PENDING: {
					long inviteID = (Long) payload.get(0);
					String receiverName = (String) payload.get(1);
					GameOptions options = (GameOptions) payload.get(2);
					listener.invitePending(inviteID, receiverName, options);
					
					break;
				}
				case INVITE_FAILED: {
					String reason = (String) payload.get(0);
					listener.inviteFailed(reason);
					
					break;
				}
				case INVITE_REQUEST: {
					long inviteID = (Long) payload.get(0);
					String senderName = (String) payload.get(1);
					GameOptions options = (GameOptions) payload.get(2);
					listener.inviteRequest(inviteID, senderName, options);
					
					break;
				}
				case INVITE_COMPLETED: {
					long inviteID = (Long) payload.get(0);
					boolean accept = (Boolean) payload.get(1);
					listener.inviteCompleted(inviteID, accept);
					
					break;
				}
				default: {
					Gdx.app.log("Network", String.format("Received unknown server command '%s'.", message.getCommand()));
				}
			}
		}
		catch(ClassCastException e){
			Gdx.app.log("Network", String.format("Unexpected payload type in message '%s'. Reason: '%s'.", networkCommand, e.getMessage()));
		}
		catch(IndexOutOfBoundsException e){
			Gdx.app.log("Network", String.format("Missing payload in message '%s'. Reason: '%s'.", networkCommand, e.getMessage()));
		}
	}
}
