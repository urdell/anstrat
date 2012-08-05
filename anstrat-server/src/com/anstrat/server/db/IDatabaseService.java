package com.anstrat.server.db;

import java.util.Map;

import com.anstrat.network.protocol.GameSetup;

public interface IDatabaseService {
	public enum DisplayNameChangeResponse {SUCCESS, FAIL_NAME_EXISTS, FAIL_ERROR}
	
	User createUser(String password);
	Map<Long, User> getUsers(long... userIDs);
	long createGame(GameSetup game);
	DisplayNameChangeResponse setDisplayName(long userID, String name);
}
