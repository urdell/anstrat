package com.anstrat.server;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import matchmaking.GameMatcher;

import com.anstrat.command.Command;
import com.anstrat.geography.Map;
import com.anstrat.network.NetworkMessage;
import com.anstrat.server.util.Logger;

/**
 * This class sorts the contents of NetworkMessages and delegates tasks to the corresponding handlers.
 * @author jay
 *
 */
public class ServerMessageHandler {
	private MainServer server;
	private AuthMessageHandler amh;
	private GameMessageHandler gmh;
	private static final Logger logger = Logger.getGlobalLogger();
	
	/**
	 * Default constructor.
	 * Initializes an AuthMessageHandler and a GameMessageHandler to delegate tasks to.
	 * @param server Required for some functionality and for logging purposes.
	 */
	public ServerMessageHandler(MainServer server, GameMatcher matcher)
	{
		this.server = server;
		amh = new AuthMessageHandler(server);
		gmh = new GameMessageHandler(server,matcher);
	}
	
	/**
	 * Checks the message's validity with regards to contents and delegates tasks further.
	 * @param ps The socket the message was received from.
	 * @param nm The NetworkMessage that was received.
	 */
	@SuppressWarnings("unchecked")
	public void handleMessage(PlayerSocket ps, NetworkMessage nm)
	{
		String command = nm.getCommand();
		List<Serializable> payload = nm.getPayload();
		
		try
		{
			if(!command.equalsIgnoreCase("POLL_TURNS"))
				logger.info("Received a "+command+" message from "+ps.getNetworkName()+".");
			
			if(command.equalsIgnoreCase("LOGIN"))
			{
				String username = (String) payload.get(0);
				String password = (String) payload.get(1);
				amh.login(username.toLowerCase(Locale.ENGLISH),password, ps);
			}
			else if(command.equalsIgnoreCase("QUICK_LOGIN"))
			{
				amh.serveQuickLogin(ps);
			}
			else if(command.equalsIgnoreCase("REGISTER"))
			{
				//TODO imp
				ps.sendMessage(new NetworkMessage("COMMAND_REFUSED", "Not implemented yet."));
			}
			else if(command.equalsIgnoreCase("LOGOUT"))
			{
				server.logout(ps, true);
			}
			else if(command.equalsIgnoreCase("END_TURN"))
			{
				long gameId = (Long) payload.get(0);
				int turn = (Integer) payload.get(1);
				Queue<Command> commands = (Queue<Command>) payload.get(2);
				
				gmh.endTurn(gameId, turn, commands, ps.getUser(), true);
			}
			else if(ps.isLoggedIn())
			{
				if(command.equalsIgnoreCase("POLL_TURNS"))
				{
					long gameId = (Long) payload.get(0);
					int expectedTurn = (Integer) payload.get(1);
					
					gmh.pollTurns(gameId, expectedTurn, ps);
				}
				else if(command.equalsIgnoreCase("GAME_HOST_CUSTOM"))
				{
					long nonce = (Long) payload.get(0);
					long timeLimit = (Long) payload.get(1);
					String gameName = (String) payload.get(2);
					String password = (String) payload.get(3);
					Map map = (Map) payload.get(4);
					gmh.hostCustomGame(nonce, timeLimit, gameName, password, map, ps);
				}
				else if(command.equalsIgnoreCase("POLL_DEFAULT_MAPS"))
				{
					gmh.pollDefaultMaps(ps);
				}
				else if(command.equalsIgnoreCase("GAME_HOST_DEFAULT"))
				{
					ps.sendMessage(new NetworkMessage("COMMAND_REFUSED", "Not implemented yet: "+command+"."));
				}
				else if(command.equalsIgnoreCase("GAME_HOST_DEFAULT_RANDOM"))
				{
					ps.sendMessage(new NetworkMessage("COMMAND_REFUSED", "Not implemented yet: "+command+"."));
				}
				else if(command.equalsIgnoreCase("GAME_JOIN"))
				{
					long nonce = (Long) payload.get(0);
					String gameName = (String) payload.get(1);
					String password = (String) payload.get(2);
					gmh.joinGame(nonce, gameName, password, ps);
				}
				else if(command.equalsIgnoreCase("GAME_JOIN_RANDOM"))
				{
					long nonce = (Long) payload.get(0);
					long minTimeLimit = (Long) payload.get(1);
					long maxTimeLimit = (Long) payload.get(2);
					int accept_flags = (Integer) payload.get(3);
					gmh.joinRandomGame(nonce, minTimeLimit, maxTimeLimit, accept_flags, ps);
				}
				else if(command.equalsIgnoreCase("GAME_HOST_RANDOM"))
				{
					long nonce = (Long) payload.get(0);
					long timeLimit = (Long) payload.get(1);
					String gameName = (String) payload.get(2);
					String password = (String) payload.get(3);
					int width = (Integer) payload.get(4);
					int height = (Integer) payload.get(5);
					
					// Sanitize inputs
					width = width > Map.MAX_SIZE ? Map.MAX_SIZE : width;
					height = height > Map.MAX_SIZE ? Map.MAX_SIZE : height;
					width = width < Map.MIN_SIZE ? Map.MIN_SIZE : width;
					height = height < Map.MIN_SIZE ? Map.MIN_SIZE : height;
					
					gmh.hostRandomGame(width, height, nonce, timeLimit, gameName, password, ps);
				}
				else if(command.equalsIgnoreCase("MAP_GENERATED"))
				{
					String gameName = (String) payload.get(0);
					com.anstrat.geography.Map map = (com.anstrat.geography.Map) payload.get(1);
					gmh.mapGenerated(gameName, map, ps);
				}
				else if(command.equalsIgnoreCase("REMOVE_REQUEST"))
				{
					long nonce = (Long) payload.get(0);
					gmh.removeRequest(nonce, ps);
				}
				else
				{
					logger.info("Received unknown command from %s: %s", ps.getNetworkName(), command);
				}
			}
			else
			{
				logger.info("Received unauthorized command from "+ps.getNetworkName()+": "+command);
			}
		}
		catch(IndexOutOfBoundsException iobe)
		{
			logger.info("Illegal number of arguments in "+command+" message!");
		}
		catch(ClassCastException cce)
		{
			logger.info("Invalid input type in "+command+" message!");
		}
	}
}
