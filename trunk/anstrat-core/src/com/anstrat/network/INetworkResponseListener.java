package com.anstrat.network;

import com.anstrat.command.Command;
import com.anstrat.network.protocol.GameSetup;

public interface INetworkResponseListener {
	public void displayNameChanged(String name);
	public void displayNameChangeRejected(String name);
	public void command(long gameID, int commandNr, Command command);
	public void gameStateCorrupted(long gameID);
	public void gameStarted(long gameID, GameSetup gameSetup);
}
