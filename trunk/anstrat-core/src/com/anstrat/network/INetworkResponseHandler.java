package com.anstrat.network;

import com.anstrat.command.Command;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;

public interface INetworkResponseHandler {
	public void loginAccepted();
	public void loginDenied();
	public void userCredentials(long userID, String password);
	public void displayNameChanged(String name);
	public void displayNameChangeRejected(String name);
	public void command(long gameID, int commandNr, Command command);
	public void gameStateCorrupted(long gameID);
	public void gameStarted(long gameID, long seed, Map map, Player[] players);
}
