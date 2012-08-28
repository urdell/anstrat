package com.anstrat.network;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameSetup;

public interface INetworkResponseListener {
	public void displayNameChanged(String name);
	public void displayNameChangeRejected(String reason);
	public void command(long gameID, int commandNr, Command command);
	public void gameStarted(long gameID, GameSetup gameSetup);
	public void playerResigned(long gameID, int playerID);
	public void inviteRequest(long inviteID, String senderName, GameOptions options);
	public void inviteCompleted(long inviteID, boolean accept);
	public void invitePending(long inviteID, String receiverDisplayName, GameOptions options);
	public void inviteFailed(String reason);
}
