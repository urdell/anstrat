package com.anstrat.server;

import java.net.InetSocketAddress;

import com.anstrat.command.Command;

public class AuthMessageHandler {

	private final IConnectionManager connectionManager;
	
	public AuthMessageHandler(IConnectionManager connectionManager){
		this.connectionManager = connectionManager;
	}
	
	public void login(InetSocketAddress client, long userID, String password){
		// Triggers ACCEPT_LOGIN or DENY_LOGIN
	}
	
	public void createNewUser(InetSocketAddress client){
		// Triggers USER_CREDENTIALS(userID, password)
	}
	
	// Requires login
	
	public void setDisplayName(InetSocketAddress client, String name){
		// Triggers DISPLAY_NAME_CHANGE_REJECTED or DISPLAY_NAME_CHANGED(name)
	}
}
