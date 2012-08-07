package com.anstrat.server.db;

import java.util.Map;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameSetup;

public interface IDatabaseService {
	public enum DisplayNameChangeResponse {SUCCESS, FAIL_NAME_EXISTS, FAIL_ERROR}
	
	boolean createCommand(long gameID, int commandNr, Command command);
	User createUser(String password);
	Map<Long, User> getUsers(long... userIDs);
	Long createGame(GameSetup game);
	DisplayNameChangeResponse setDisplayName(long userID, String name);
	Player[] getPlayers(long gameID);
	Command[] getCommands(long gameID, int greaterThanOrEqualToCommandNr);
}
