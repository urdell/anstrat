package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.network.protocol.NetworkMessage.Command;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.DatabaseMethods;
import com.anstrat.server.db.User;
import com.anstrat.server.db.DatabaseMethods.DisplayNameChangeResponse;
import com.anstrat.server.util.Logger;
import com.anstrat.server.util.Password;

public class AuthMessageHandler {

	private static final Logger logger = Logger.getGlobalLogger();
	private final IConnectionManager connectionManager;
	
	public AuthMessageHandler(IConnectionManager connectionManager){
		this.connectionManager = connectionManager;
	}
	
	// Triggers ACCEPT_LOGIN or DENY_LOGIN
	public void login(InetSocketAddress client, long userID, String password){
		User user = DatabaseMethods.getUser(userID);
		
		// Authenticate
		boolean userExists = user != null;
		boolean userPasswordMatches = user == null || Password.authenticate(password, user.getEncryptedPassword());
		
		if(userExists && userPasswordMatches){
			connectionManager.linkUserToAddress(userID, client);
			connectionManager.sendMessage(client, new NetworkMessage(Command.ACCEPT_LOGIN, userID));
		}
		else{
			connectionManager.sendMessage(client, new NetworkMessage(Command.DENY_LOGIN, "UserID / password combination does not match."));
		}
		
		// More specific logging
		if(!userExists) logger.info("%s attempted to login as userID '%d', but the user does not exist.", client, userID);
		if(!userPasswordMatches) logger.info("%s attempted to login as user '%d' with an invalid password..", client, userID);
	}
	
	// Triggers USER_CREDENTIALS(userID, password)
	public void createNewUser(InetSocketAddress client){
		String password = Password.generateRandomAlphaNumericPassword(64);
		User user = DatabaseMethods.createUser(password);
		
		connectionManager.linkUserToAddress(user.getUserID(), client);
		connectionManager.sendMessage(client, new NetworkMessage(Command.USER_CREDENTIALS, user.getUserID(), password));
	}
	
	// Requires login
	
	// Triggers DISPLAY_NAME_CHANGE_REJECTED or DISPLAY_NAME_CHANGED(name)
	public void setDisplayName(InetSocketAddress client, String name){
		
		// Limit the kind of name that is allowed
		if(!Pattern.matches("^[a-zA-Z0-9\\-_]{3,20}$", name)){
			connectionManager.sendMessage(client, new NetworkMessage(Command.DISPLAY_NAME_CHANGE_REJECTED, "Name is too short or too long or contains invalid characters."));
			return;
		}

		long userID = connectionManager.getUserID(client);
		
		// Not logged in?
		if(userID != -1){
			connectionManager.sendMessage(client, new NetworkMessage(Command.DISPLAY_NAME_CHANGE_REJECTED, "Not logged in."));
			return;
		}
		
		// Go ahead with name change
		DisplayNameChangeResponse response = DatabaseMethods.setDisplayName(userID, name);
		
		switch(response){
			case SUCCESS: {
				connectionManager.sendMessage(client, new NetworkMessage(Command.DISPLAY_NAME_CHANGED, name));
				return;
			}
			case FAIL_NAME_EXISTS: {
				connectionManager.sendMessage(client, new NetworkMessage(Command.DISPLAY_NAME_CHANGE_REJECTED, "Name is already in use."));
				return;
			}
			case FAIL_ERROR: {
				connectionManager.sendMessage(client, new NetworkMessage(Command.DISPLAY_NAME_CHANGE_REJECTED, "Unexpected error."));
				return;
			}
		}
	}
}
