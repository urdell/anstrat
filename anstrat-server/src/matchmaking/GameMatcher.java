package matchmaking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.anstrat.gameCore.GameType;
import com.anstrat.geography.Map;
import com.anstrat.network.NetworkMessage;
import com.anstrat.server.PlayerSocket;
import com.anstrat.server.User;
import com.anstrat.server.db.DatabaseHelper;
import com.anstrat.server.util.Logger;

public class GameMatcher {
	
	private static final Logger logger = Logger.getGlobalLogger();
	private Random rand = new Random();
	public final Object lock = new Object();
	
	// gameName -> HostedGame
	private HashMap<String,HostedGame> default_waiting = new HashMap<String,HostedGame>();
	private HashMap<String,HostedGame> default_random_waiting = new HashMap<String,HostedGame>();
	private HashMap<String,HostedGame> custom_waiting = new HashMap<String,HostedGame>();
	private HashMap<String,HostedGame> random_waiting = new HashMap<String,HostedGame>();
	
	// gameName -> RandomWaiter
	private HashMap<String,RandomWaiter> random_joiners = new HashMap<String,RandomWaiter>();
	
	// gameName -> HostedGame || Used for asynchronous map fetching.
	private HashMap<String,HostedGame> random_hosts_maps = new HashMap<String,HostedGame>();
	
	// gameName -> RandomWaiter || Used for asynchronous map fetching.
	private HashMap<String,RandomWaiter> random_joiners_maps = new HashMap<String,RandomWaiter>();
	
	// For performance
	private HashMap<Long,List<String>> user_to_mapname = new HashMap<Long,List<String>>();
	
	private static final int MAX_NAME_ATTEMPTS = 100;
	
	private class HostedGame
	{
		public long nonce;
		public PlayerSocket socket;
		public String password;
		public long timeLimit;
		public Map map;
		
		public HostedGame(long nonce, String password, long timeLimit, Map map, PlayerSocket socket)
		{
			this.nonce = nonce;
			this.socket = socket;
			this.password = password;
			this.timeLimit = timeLimit;
			this.map = map;
		}
		
		public boolean isPasswordProtected()
		{
			return password != null && !password.equals("");
		}
	}
	
	private class RandomWaiter
	{
		public long nonce;
		public int accept_flags;
		public long minTimeLimit;
		public long maxTimeLimit;
		public PlayerSocket socket;
		public Map map;
		
		public RandomWaiter(long nonce, int accept_flags, long minTimeLimit, long maxTimeLimit, PlayerSocket socket)
		{
			this.nonce = nonce;
			this.accept_flags = accept_flags;
			this.minTimeLimit = minTimeLimit;
			this.maxTimeLimit = maxTimeLimit;
			this.socket = socket;
		}
	}
	
	public boolean nameAvailable(String name)
	{
		return !default_waiting.containsKey(name) && !default_random_waiting.containsKey(name) &&
				!custom_waiting.containsKey(name) && !random_hosts_maps.containsKey(name) &&
				!random_joiners_maps.containsKey(name);
	}
	
	public String getNextAvailableName(String name)
	{
		if(nameAvailable(name))
			return name;
		
		for(int i=0;i<MAX_NAME_ATTEMPTS;i++)
		{
			String attemptedName = name + "_" + rand.nextInt(10000);
			
			if(nameAvailable(attemptedName))
				return attemptedName;
		}
		
		return null;
	}
	
	public void removeUserFromLists(Long userId)
	{
		List<String> queuedGames = user_to_mapname.remove(userId);
		
		if(queuedGames!=null)
		{
			logger.info("Removing player %d from %d listed games");
			
			for(String gameName : queuedGames)
			{
				// Awesome performance.
				if(default_waiting.remove(gameName)==null)
					if(default_random_waiting.remove(gameName)==null)
						if(custom_waiting.remove(gameName)==null)
							if(random_hosts_maps.remove(gameName)==null)
								if(random_joiners_maps.remove(gameName)==null)
									if(random_waiting.remove(gameName)==null)
										random_joiners.remove(gameName);
			}
		}
	}
	
	public void removeGameFromLists(Long userId, String gameName)
	{
		// Awesome performance.
		if(user_to_mapname.get(userId) != null)
				user_to_mapname.get(userId).remove(gameName);
		if(default_waiting.remove(gameName)==null)
			if(default_random_waiting.remove(gameName)==null)
				if(custom_waiting.remove(gameName)==null)
					if(random_hosts_maps.remove(gameName)==null)
						if(random_joiners_maps.remove(gameName)==null)
							if(random_waiting.remove(gameName)==null)
								random_joiners.remove(gameName);
	}

	public void hostCustomGame(long nonce, long timeLimit, String gameName, String password, com.anstrat.geography.Map map, PlayerSocket socket) 
	{
		synchronized(lock)
		{
			String freeName = getNextAvailableName(gameName);
			if(freeName==null)
				socket.sendMessage(new NetworkMessage("GAME_HOST_FAILURE",(Long) nonce,"An unknown error occurred. Please try again later."));
			else
			{
				if(map.getCastleCount()!=2)
					socket.sendMessage(new NetworkMessage("GAME_HOST_FAILURE",(Long) nonce, "Invalid number of castles in given map."));
				else
				{
					User host = socket.getUser();
					
					if(host==null)
						socket.sendMessage(new NetworkMessage("GAME_HOST_FAILURE",(Long) nonce, "Not logged in."));
					else
					{
						HostedGame hg = new HostedGame(nonce, password, timeLimit, map, socket);
						
						String randomOpponent = null;
						
						if(!hg.isPasswordProtected())
							randomOpponent = getRandomOpponent(host.getUserId(), GameType.TYPE_CUSTOM, timeLimit, timeLimit);
						
						if(randomOpponent != null)
						{
							RandomWaiter rw = random_joiners.remove(randomOpponent);
							User joiner = rw.socket.getUser();
							if(joiner!=null)
							{
								long matchedTimeLimit = getTimeLimit(timeLimit,timeLimit,rw.minTimeLimit,rw.maxTimeLimit);
								
								List<Serializable> gameInfo = DatabaseHelper.createGame(map, new User[]{host,joiner}, 
										2, matchedTimeLimit, rand.nextLong());
								
								Long gameId = (Long) gameInfo.get(0);
								Long randSeed = (Long) gameInfo.get(1);
								Long timelim = (Long) gameInfo.get(2);
								
								removeGameFromLists(joiner.getUserId(),randomOpponent);
								
								socket.sendMessage(new NetworkMessage("GAME_HOST_START", (Long) nonce, gameId, randSeed, 
										freeName, joiner.getUserId(), joiner.getDisplayedName()));
								
								rw.socket.sendMessage(new NetworkMessage("GAME_JOIN_SUCCESSFUL", (Long) rw.nonce, gameId, randSeed,
										timelim, freeName, map, GameType.TYPE_CUSTOM, host.getUserId(), host.getDisplayedName()));
								
								logger.info("%s started a custom game with %s.",host.getDisplayedName(),joiner.getDisplayedName());
							}
							else
								logger.warning("Joiner was null! Game discarded.");
						}
						else
						{
							addGameForUser(freeName,host.getUserId());
							custom_waiting.put(freeName, hg);
							socket.sendMessage(new NetworkMessage("GAME_HOST_WAIT_OPPONENT", (Long) nonce, freeName));
							
							logger.info("%s was placed into the custom_waiting queue.",host.getDisplayedName());
						}
					}
				}
			}
		}
	}
	
	public void joinRandomGame(long nonce, long minTimeLimit, long maxTimeLimit, int accept_flags, PlayerSocket socket)
	{
		synchronized(lock)
		{
			User joiner = socket.getUser();
			if(joiner==null)
				socket.sendMessage(new NetworkMessage("GAME_JOIN_FAILURE",(Long) nonce, "Not logged in."));
			else
			{
				if(GameType.validFlags(accept_flags))
				{
					boolean joined_game = false;
					
					if(!joined_game && GameType.accept_random(accept_flags))
					{
						Set<Entry<String,HostedGame>> randoms = random_waiting.entrySet();
						
						Long userId = null;
						String mapName = null;
						
						for(Entry<String,HostedGame> random : randoms)
						{
							if(!joined_game)
							{
								HostedGame hg = random.getValue();
								
								if(socket.getUser().getUserId() != hg.socket.getUser().getUserId())
								{
									long matchedTimeLimit = getTimeLimit(hg.timeLimit, hg.timeLimit, minTimeLimit, maxTimeLimit);
									
									if(matchedTimeLimit != -1l)
									{
										User host = hg.socket.getUser();
										
										List<Serializable> gameInfo = DatabaseHelper.createGame(hg.map, new User[]{host,joiner},
												2, matchedTimeLimit, rand.nextLong());
										
										Long gameId = (Long) gameInfo.get(0);
										Long randSeed = (Long) gameInfo.get(1);
										Long timelim = (Long) gameInfo.get(2);
										
										userId = socket.getUser().getUserId();
										mapName = random.getKey();
										
										hg.socket.sendMessage(new NetworkMessage("GAME_RANDOM_HOST", (Long) hg.nonce, gameId, randSeed, 
												timelim, random.getKey(), hg.map, 
												GameType.TYPE_RANDOM, joiner.getUserId(), joiner.getDisplayedName()));
										
										socket.sendMessage(new NetworkMessage("GAME_JOIN_SUCCESSFUL", (Long) nonce, gameId, randSeed, timelim,
												random.getKey(), hg.map, GameType.TYPE_RANDOM, host.getUserId(), host.getDisplayedName()));
										
										logger.info("%s started a random game with %s.",host.getDisplayedName(),joiner.getDisplayedName());
										
										joined_game = true;
									}
								}
							}
						}
						
						if(joined_game)
							removeGameFromLists(userId, mapName);
						
						if(!joined_game)
						{
							String opponentGameName = getRandomOpponent(socket.getUser().getUserId(),
									GameType.TYPE_RANDOM, minTimeLimit, maxTimeLimit);
							
							if(opponentGameName!=null)
							{
								RandomWaiter ropp = random_joiners.remove(opponentGameName);
								
								long matchedTimeLimit = getTimeLimit(minTimeLimit, maxTimeLimit, ropp.minTimeLimit, ropp.maxTimeLimit);
								
								User host = ropp.socket.getUser();
								
								List<Serializable> gameInfo = DatabaseHelper.createGame(ropp.map, new User[]{host,joiner}, 
										2, matchedTimeLimit, rand.nextLong());
								
								Long gameId = (Long) gameInfo.get(0);
								Long randSeed = (Long) gameInfo.get(1);
								Long timelim = (Long) gameInfo.get(2);
								
								removeGameFromLists(host.getUserId(), opponentGameName);
								
								ropp.socket.sendMessage(new NetworkMessage("GAME_RANDOM_HOST", (Long) ropp.nonce, gameId, randSeed, 
										timelim, opponentGameName, ropp.map, GameType.TYPE_RANDOM,
										joiner.getUserId(), joiner.getDisplayedName()));
								
								socket.sendMessage(new NetworkMessage("GAME_JOIN_SUCCESSFUL", (Long) nonce, gameId, randSeed,
										timelim, opponentGameName, ropp.map, GameType.TYPE_RANDOM, 
										host.getUserId(), host.getDisplayedName()));
								
								logger.info("%s started a random game with %s.",host.getDisplayedName(), joiner.getDisplayedName());
								
								joined_game = true;
							}
						}
					}
					
					if(!joined_game && GameType.accept_custom(accept_flags))
					{
						Set<Entry<String,HostedGame>> customs = custom_waiting.entrySet();
						
						for(Entry<String,HostedGame> custom : customs)
						{
							if(!joined_game)
							{
								HostedGame hg = custom.getValue();
								if(!hg.isPasswordProtected() && (hg.socket.getUser().getUserId() != socket.getUser().getUserId()))
								{
									long matchedTimeLimit = getTimeLimit(hg.timeLimit, hg.timeLimit, minTimeLimit, maxTimeLimit);
									
									if(matchedTimeLimit != -1l)
									{
										User host = hg.socket.getUser();
										
										List<Serializable> gameInfo = DatabaseHelper.createGame(hg.map, new User[]{host,joiner}, 
												2, matchedTimeLimit, rand.nextLong());
										
										Long gameId = (Long) gameInfo.get(0);
										Long randSeed = (Long) gameInfo.get(1);
										Long timelim = (Long) gameInfo.get(2);
										
										removeGameFromLists(host.getUserId(), custom.getKey());
										
										hg.socket.sendMessage(new NetworkMessage("GAME_HOST_START", (Long) hg.nonce, gameId, randSeed, 
												custom.getKey(), joiner.getUserId(), joiner.getDisplayedName()));
										
										socket.sendMessage(new NetworkMessage("GAME_JOIN_SUCCESSFUL", (Long) nonce, gameId, randSeed, 
												timelim, custom.getKey(), hg.map, GameType.TYPE_CUSTOM, host.getUserId(), host.getDisplayedName()));
										
										logger.info("%s started a random game with %s.",host.getDisplayedName(), joiner.getDisplayedName());
										
										joined_game = true;
									}
								}
							}
						}
					}
					
					// TODO fix for default|defr
					
					if(!joined_game)
					{
						String randomGameName = getNextAvailableName(joiner.getDisplayedName()+"\'s Game");
						
						if(randomGameName != null)
						{
							random_joiners_maps.put(randomGameName, new RandomWaiter(nonce, accept_flags, minTimeLimit, maxTimeLimit, socket));
							addGameForUser(randomGameName, joiner.getUserId());
							
							socket.sendMessage(new NetworkMessage("GENERATE_MAP",randomGameName,9,9,rand.nextLong(),(Long) nonce));
							
							logger.info("Waiting to receive a map from "+joiner.getDisplayedName()+".");
						}
					}
				}
				else
					socket.sendMessage(new NetworkMessage("GAME_JOIN_FAILURE",(Long) nonce, "Invalid game type flags."));
			}
		}
	}
	
	public void hostRandomGame(int width, int height, long nonce, long timeLimit, String gameName, 
			String password, PlayerSocket socket)
	{
		synchronized(lock)
		{
			String randomGameName = getNextAvailableName(gameName);
			
			if(randomGameName != null)
			{
				addGameForUser(randomGameName,socket.getUser().getUserId());
				
				random_hosts_maps.put(randomGameName, new HostedGame(nonce, password, timeLimit, null, socket));
				addGameForUser(randomGameName, socket.getUser().getUserId());
				
				socket.sendMessage(new NetworkMessage("GENERATE_MAP", randomGameName, width, height, rand.nextLong(), nonce));
				logger.info("Waiting to receive a map from "+socket.getUser().getDisplayedName()+".");
			}
		}
	}

	public void mapGenerated(String gameName, Map map)
	{
		synchronized(lock)
		{
			if(map==null)
				logger.info("Received a null map!");
			else if(random_hosts_maps.containsKey(gameName))
			{
				HostedGame hg = random_hosts_maps.remove(gameName);
				
				hg.map = map;
				
				boolean matched = false;
				
				if(!hg.isPasswordProtected())
				{
					String randomGameName = getRandomOpponent(hg.socket.getUser().getUserId(),
							GameType.TYPE_RANDOM, hg.timeLimit, hg.timeLimit);
					if(randomGameName != null)
					{
						RandomWaiter rw = random_joiners.remove(randomGameName);
						
						long matchedTimeLimit = getTimeLimit(hg.timeLimit, hg.timeLimit, rw.minTimeLimit, rw.maxTimeLimit);
						
						User host = hg.socket.getUser();
						User joiner = rw.socket.getUser();
						
						List<Serializable> gameInfo = DatabaseHelper.createGame(map, new User[]{host,joiner}, 
								2, matchedTimeLimit, rand.nextLong());
						
						Long gameId = (Long) gameInfo.get(0);
						Long randSeed = (Long) gameInfo.get(1);
						Long timelim = (Long) gameInfo.get(2);
						
						removeGameFromLists(host.getUserId(), gameName);
						removeGameFromLists(joiner.getUserId(), randomGameName);
						
						hg.socket.sendMessage(new NetworkMessage("GAME_HOST_START", (Long) hg.nonce, gameId, randSeed, 
								gameName, joiner.getUserId(), joiner.getDisplayedName()));
						
						rw.socket.sendMessage(new NetworkMessage("GAME_JOIN_SUCCESSFUL", (Long) rw.nonce, gameId, randSeed,
								timelim, gameName, map, GameType.TYPE_RANDOM, host.getUserId(), host.getDisplayedName()));
						
						matched = true;
						
						logger.info("%s started a random game with %s.",host.getDisplayedName(),joiner.getDisplayedName());
					}
				}
				
				if(!matched)
				{
					random_waiting.put(gameName,hg);
					hg.socket.sendMessage(new NetworkMessage("GAME_HOST_WAIT_OPPONENT",(Long) hg.nonce, gameName));
					logger.info("%s was placed into the random_waiting queue.",hg.socket.getUser().getDisplayedName());
				}
			}
			else if(random_joiners_maps.containsKey(gameName))
			{
				RandomWaiter rw = random_joiners_maps.remove(gameName);
				
				rw.map = map;
				
				String randomGameName = getRandomOpponent(rw.socket.getUser().getUserId(), 
						GameType.TYPE_RANDOM, rw.minTimeLimit, rw.maxTimeLimit);
				
				if(randomGameName != null)
				{
					RandomWaiter ropp = random_joiners.remove(randomGameName);
					
					long matchedTimeLimit = getTimeLimit(rw.minTimeLimit, rw.maxTimeLimit, ropp.minTimeLimit, ropp.maxTimeLimit);
					
					User host = rw.socket.getUser();
					User joiner = ropp.socket.getUser();
					
					List<Serializable> gameInfo = DatabaseHelper.createGame(map, new User[]{host,joiner}, 
							2, matchedTimeLimit, rand.nextLong());
					
					Long gameId = (Long) gameInfo.get(0);
					Long randSeed = (Long) gameInfo.get(1);
					Long timelim = (Long) gameInfo.get(2);
					
					removeGameFromLists(host.getUserId(), gameName);
					removeGameFromLists(joiner.getUserId(), randomGameName);
					
					rw.socket.sendMessage(new NetworkMessage("GAME_RANDOM_HOST", (Long) rw.nonce, gameId, randSeed, timelim,
							gameName, map, GameType.TYPE_RANDOM, joiner.getUserId(), joiner.getDisplayedName()));
					
					ropp.socket.sendMessage(new NetworkMessage("GAME_JOIN_SUCCESSFUL", (Long) ropp.nonce, gameId, randSeed,
							timelim, gameName, map, GameType.TYPE_RANDOM, host.getUserId(), host.getDisplayedName()));
					
					logger.info("%s started a random game with %s.",host.getDisplayedName(),joiner.getDisplayedName());
				}
				else
				{
					random_joiners.put(gameName, rw);
					rw.socket.sendMessage(new NetworkMessage("GAME_RANDOM_WAIT_OPPONENT",(Long) rw.nonce));
					
					logger.info("%s was placed in the random_joiners queue.",rw.socket.getUser().getDisplayedName());
				}
			}
			else
				logger.info("Received map for unknown game instance.");
		}
	}
	
	public String getRandomOpponent(long userId, int gameType, long minTimeLimit, long maxTimeLimit)
	{
		Set<Entry<String,RandomWaiter>> randoms = random_joiners.entrySet();
		
		for(Entry<String,RandomWaiter> random : randoms)
		{
			RandomWaiter rw = random.getValue();
			
			if((rw.socket.getUser().getUserId() != userId) && (rw.accept_flags & gameType) > 0)
			{
				if(getTimeLimit(rw.minTimeLimit, rw.maxTimeLimit, minTimeLimit, maxTimeLimit) != -1l)
					return random.getKey();
			}
		}
		
		return null;
	}
	
	public long getTimeLimit(long p1_minTimeLimit, long p1_maxTimeLimit, long p2_minTimeLimit, long p2_maxTimeLimit)
	{
		if(p1_minTimeLimit >= p2_minTimeLimit)
		{
			if(p1_minTimeLimit <= p2_maxTimeLimit)
				return p1_minTimeLimit;
			else
				return -1l;
		}
		else
		{
			if(p2_minTimeLimit <= p1_maxTimeLimit)
				return p2_minTimeLimit;
			else
				return -1l;
		}
	}
	
	public void addGameForUser(String gameName, Long userId)
	{
		if(user_to_mapname.get(userId) == null)
			user_to_mapname.put(userId, new ArrayList<String>());
		user_to_mapname.get(userId).add(gameName);
		
		logger.info("Game \'"+gameName+"\' linked to userId "+userId+".");
	}

	public void joinGame(Long nonce, String gameName, String password, PlayerSocket socket) 
	{
		synchronized(lock)
		{
			HostedGame game = null;
			
			if(default_waiting.containsKey(gameName))
			{
				game = default_waiting.get(gameName);
				if(!game.isPasswordProtected() || password.equals(game.password))
				{
					//TODO implement
				}
				else
					socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "Invalid game password."));
			}
			else if(default_random_waiting.containsKey(gameName))
			{
				game = default_random_waiting.get(gameName);
				if(!game.isPasswordProtected() || password.equals(game.password))
				{
					//TODO implement
				}
				else
					socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "Invalid game password."));
			}
			else if(random_waiting.containsKey(gameName))
			{
				game = random_waiting.get(gameName);
				if(!game.isPasswordProtected() || password.equals(game.password))
				{
					User host = game.socket.getUser();
					User joiner = socket.getUser();
					
					if(host.getUserId() != joiner.getUserId())
					{
						List<Serializable> gameInfo = DatabaseHelper.createGame(game.map, new User[]{host, joiner}, 
								2, game.timeLimit, rand.nextLong());
						
						Long gameId = (Long) gameInfo.get(0);
						Long randSeed = (Long) gameInfo.get(1);
						Long timelim = (Long) gameInfo.get(2);
						
						removeGameFromLists(host.getUserId(), gameName);
						
						game.socket.sendMessage(new NetworkMessage("GAME_RANDOM_HOST", (Long) game.nonce, gameId, randSeed, 
								timelim, gameName, game.map, 
								GameType.TYPE_RANDOM, joiner.getUserId(), joiner.getDisplayedName()));
						
						socket.sendMessage(new NetworkMessage("GAME_JOIN_SUCCESSFUL", (Long) nonce, gameId, randSeed, timelim,
								gameName, game.map, GameType.TYPE_RANDOM, host.getUserId(), host.getDisplayedName()));
						
						logger.info("%s started a random game with %s",host.getDisplayedName(),joiner.getDisplayedName());
					}
					else
					{
						socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "You are already the host of this game!"));
						logger.info("%s mistakenly tried to join his own game."+joiner.getDisplayedName());
					}
				}
				else
					socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "Invalid game password."));
			}
			else if(custom_waiting.containsKey(gameName))
			{
				game = custom_waiting.get(gameName);
				if(!game.isPasswordProtected() || password.equals(game.password))
				{
					User host = game.socket.getUser();
					User joiner = socket.getUser();
					
					if(host.getUserId() != joiner.getUserId())
					{
						List<Serializable> gameInfo = DatabaseHelper.createGame(game.map, new User[]{host, joiner}, 
								2, game.timeLimit, rand.nextLong());
						
						Long gameId = (Long) gameInfo.get(0);
						Long randSeed = (Long) gameInfo.get(1);
						Long timelim = (Long) gameInfo.get(2);
						
						removeGameFromLists(host.getUserId(), gameName);
						
						socket.sendMessage(new NetworkMessage("GAME_JOIN_SUCCESSFUL",(Long) nonce, gameId, randSeed, timelim,
								gameName, game.map, GameType.TYPE_CUSTOM, host.getUserId(), host.getDisplayedName()));
						
						game.socket.sendMessage(new NetworkMessage("GAME_HOST_START",(Long) game.nonce, gameId, randSeed, gameName,
								joiner.getUserId(), joiner.getDisplayedName()));
						
						logger.info("%s started a custom game with %s.",host.getDisplayedName(),joiner.getDisplayedName());
					}
					else
					{
						socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "You are already the host of this game!"));
						logger.info("%s mistakenly tried to join his own game."+joiner.getDisplayedName());
					}
				}
				else
					socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "Invalid game password."));
			}
			else
				socket.sendMessage(new NetworkMessage("COMMAND_REFUSED", "No such game was found."));
		}
	}

	// Please don't look here, you may die from sadness :(
	public void removeRequest(long nonce, PlayerSocket ps) {
		
		int removed = 0;
		long userId = ps.getUser().getUserId();
		
		synchronized(lock)
		{
			//TODO efficiency, lol!
			//TODO improve from worst code ever to something else
			
			String removeThisGame = null;
			
			for(Entry<String,HostedGame> entry : custom_waiting.entrySet())
			{
				if(removeThisGame == null && entry.getValue().nonce == nonce && 
						entry.getValue().socket.getUser().getUserId() == userId)
				{
					removeThisGame = entry.getKey();
				}
			}
			
			if(removeThisGame != null)
			{
				removed++;
				removeGameFromLists(userId, removeThisGame);
				removeThisGame = null;
			}
			
			for(Entry<String,HostedGame> entry : default_waiting.entrySet())
			{
				if(removeThisGame == null && entry.getValue().nonce == nonce && 
						entry.getValue().socket.getUser().getUserId() == userId)
				{
					removeThisGame = entry.getKey();
				}
			}
			
			if(removeThisGame != null)
			{
				removed++;
				removeGameFromLists(userId, removeThisGame);
				removeThisGame = null;
			}
			
			for(Entry<String,HostedGame> entry : default_random_waiting.entrySet())
			{
				if(removeThisGame == null && entry.getValue().nonce == nonce && 
						entry.getValue().socket.getUser().getUserId() == userId)
				{
					removeThisGame = entry.getKey();
				}
			}
			
			if(removeThisGame != null)
			{
				removed++;
				removeGameFromLists(userId, removeThisGame);
				removeThisGame = null;
			}
			
			for(Entry<String,HostedGame> entry : random_waiting.entrySet())
			{
				if(removeThisGame == null && entry.getValue().nonce == nonce && 
						entry.getValue().socket.getUser().getUserId() == userId)
				{
					removeThisGame = entry.getKey();
				}
			}
			
			if(removeThisGame != null)
			{
				removed++;
				removeGameFromLists(userId, removeThisGame);
				removeThisGame = null;
			}
			
			for(Entry<String,HostedGame> entry : random_hosts_maps.entrySet())
			{
				if(removeThisGame == null && entry.getValue().nonce == nonce && 
						entry.getValue().socket.getUser().getUserId() == userId)
				{
					removeThisGame = entry.getKey();
				}
			}
			
			if(removeThisGame != null)
			{
				removed++;
				removeGameFromLists(userId, removeThisGame);
				removeThisGame = null;
			}
			
			for(Entry<String,RandomWaiter> entry : random_joiners_maps.entrySet())
			{
				if(removeThisGame == null && entry.getValue().nonce == nonce && 
						entry.getValue().socket.getUser().getUserId() == userId)
				{
					removeThisGame = entry.getKey();
				}
			}
			
			if(removeThisGame != null)
			{
				removed++;
				removeGameFromLists(userId, removeThisGame);
				removeThisGame = null;
			}
			
			for(Entry<String,RandomWaiter> entry : random_joiners.entrySet())
			{
				if(removeThisGame == null && entry.getValue().nonce == nonce && 
						entry.getValue().socket.getUser().getUserId() == userId)
				{
					removeThisGame = entry.getKey();
				}
			}
			
			if(removeThisGame != null)
			{
				removed++;
				removeGameFromLists(userId, removeThisGame);
				removeThisGame = null;
			}
		}
		
		logger.info("Removed %d requests for player %d.",removed, (int) ps.getUser().getUserId());
	}
}