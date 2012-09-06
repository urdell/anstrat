package com.anstrat.server.db;

import java.util.Map;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameSetup;

public interface IDatabaseService {
	public enum DisplayNameChangeResponse {SUCCESS, FAIL_NAME_EXISTS, FAIL_ERROR}
	
	boolean createCommand(long gameID, int commandNr, Command command);
	User createUser(String password);
	Map<Long, User> getUsers(long... userIDs);
	User getUser(long userID);	// Convenience method, implemented using getUsers
	User getUser(String displayName);
	Long createGame(GameSetup game);
	GameSetup getGame(long gameID);
	DisplayNameChangeResponse setDisplayName(long userID, String name);
	Player[] getPlayers(long gameID);
	Command[] getCommands(long gameID, int greaterThanOrEqualToCommandNr);
	Invite createInvite(long senderID, long receiverID, GameOptions options);
	boolean updateInvite(long inviteID, Invite.Status inviteStatus, Long gameID);
	
	/** @return the number of deleted invites. */
	int removeInvites(long... inviteIDs);
	
	/** Returns the invites associated with a user, where the user is either the sender or receiver. */
	Invite[] getInvites(long userID);
	Invite getInvite(long inviteID);
}
