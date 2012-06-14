package com.anstrat.network;

import java.util.Date;
import java.util.HashMap;
import java.util.Queue;

import com.anstrat.command.Command;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;

public interface INetworkListener extends IConnectionLostListener {

	public void randomGameStarted(long gameID, long seed, long timelimit, Player[] participants);
	public void waitOpponentsRandom(long nonce);
	public void loginAccepted(long userID, String displayName);
	public void loginDenied(String reason);
	public void loginOverrided(String reason);
	
	public void quickLoginAccepted(long userID, String username, String password);
	public void quickLoginRejected(String reason);
	
	// Join
	public void joinGameFailed(long nonce, String reason);
	public void joinGameSuccess(long nonce, long id, long seed, long limit, String name, Map map, int type,
			long opponentId, String opponentName);
	
	// Host
	public void hostGameFailed(long nonce, String reason);
	public void hostWaitOpponent(long nonce, String gameName);
	public void hostGameStart(long nonce, long gameID, long seed, String gameName, long opponentId, String opponentName);
	public void hostGameRandom(long nonce, long gameID, long seed, long limit, String gameName, Map map, int type, long opponentId, String opponentName);
	
	//Map
	public void receivedServerMaps(HashMap<Long,Map> maps);
	public void generateMap(long nonce, String name, int width, int height, long seed);
	
	public void turns(long gameID, int turnStart, Date timestamp, int stateChecksum, Queue<Queue<Command>> turns);
	public void commandRefused(String reason);
	
}
