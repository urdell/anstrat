package com.anstrat.network;

import java.io.Serializable;
import java.util.List;

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
				case GAME_STARTED: {
					final long gameID = (Long) payload.get(0);
					final GameSetup game = (GameSetup) payload.get(1);
					listener.gameStarted(gameID, game);
					
					break;
				}
				case SEND_COMMAND: {
					final long gameID = (Long) payload.get(0);
					final int commandNr = (Integer) payload.get(1);
					final com.anstrat.command.Command command = (com.anstrat.command.Command) payload.get(2);
					listener.command(gameID, commandNr, command);
					
					break;
				}
				case PLAYER_RESIGNED: {
					final long gameID = (Long) payload.get(0);
					final int playerID = (Integer) payload.get(1);
					listener.playerResigned(gameID, playerID);
					
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
