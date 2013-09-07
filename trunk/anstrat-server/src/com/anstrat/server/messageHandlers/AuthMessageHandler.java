package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.network.protocol.NetworkMessage.Command;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.MainServer;
import com.anstrat.server.db.IDatabaseService;
import com.anstrat.server.db.IDatabaseService.DisplayNameChangeResponse;
import com.anstrat.server.db.User;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;
import com.anstrat.server.util.Password;

public class AuthMessageHandler {

	@Inject
	private Logger logger;
	
	@Inject
	private IConnectionManager connectionManager;
	
	@Inject
	private IDatabaseService database;
	
	// Triggers ACCEPT_LOGIN or DENY_LOGIN
	public void login(InetSocketAddress client, long userID, String password, long versionId){
		long tmp_mainNr = versionId/1000000;
		long tmp_subNr = (versionId-1000000*tmp_mainNr)/1000;
		long tmp_subSubNr = versionId-1000000*tmp_mainNr-1000*tmp_subNr;
		
		boolean versionOk;
		
		if(tmp_mainNr > MainServer.mainNr)
			versionOk = true;
		else if(tmp_mainNr == MainServer.mainNr){
			if(tmp_subNr > MainServer.subNr)
				versionOk = true;
			else if(tmp_subNr == MainServer.subNr){
				if(tmp_subSubNr >= MainServer.subSubNr)
					versionOk = true;
				else
					versionOk = false;
			}
			else
				versionOk = false;
		}
		else
			versionOk = false;
		
		User user = database.getUsers(userID).get(userID);
		
		// Authenticate
		boolean userExists = user != null;
		boolean userPasswordMatches = user == null || Password.authenticate(password, user.getEncryptedPassword());
		
		if(userExists && userPasswordMatches && versionOk){
			connectionManager.linkUserToAddress(user, client);
			connectionManager.sendMessage(client, new NetworkMessage(Command.ACCEPT_LOGIN, userID));
		}
		else{
			if(!versionOk)
				connectionManager.sendMessage(client, new NetworkMessage(Command.DENY_LOGIN, "Playing online requires you to have at least "+
			"version "+prettyVersion(1l, MainServer.mainNr,MainServer.subNr,MainServer.subSubNr) + " (local version: "+
						prettyVersion(versionId, tmp_mainNr, tmp_subNr, tmp_subSubNr)+")"));
			else
				connectionManager.sendMessage(client, new NetworkMessage(Command.DENY_LOGIN, "UserID / password combination does not match."));
		}
		
		// More specific logging
		if(!userExists) logger.info("%s attempted to login as userID '%d', but the user does not exist.", client, userID);
		if(!userPasswordMatches) logger.info("%s attempted to login as user '%d' with an invalid password..", client, userID);
		if(!versionOk) logger.info("%s attempted to login as userID '%d', but with an outdated game version.", client, userID);
	}
	
	public String prettyVersion(long total, long main, long sub, long subsub){
		if(total==-1l) return "ancient";
		return Long.toString(main) + "." + Long.toString(sub) + "." + Long.toString(subsub);
	}
	
	// Triggers USER_CREDENTIALS(userID, password)
	public void createNewUser(InetSocketAddress client){
		String password = Password.generateRandomAlphaNumericPassword(64);
		User user = database.createUser(password);
		
		connectionManager.linkUserToAddress(user, client);
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

		Long userID = connectionManager.getUserID(client);
		
		// Not logged in?
		if(userID == null){
			connectionManager.sendMessage(client, new NetworkMessage(Command.DISPLAY_NAME_CHANGE_REJECTED, "Not logged in."));
			return;
		}
		
		// Go ahead with name change
		DisplayNameChangeResponse response = database.setDisplayName(userID, name);
		
		switch(response){
			case SUCCESS: {
				connectionManager.sendMessage(client, new NetworkMessage(Command.DISPLAY_NAME_CHANGED, name));
				logger.info("%s changed name to '%s'.", client, name);
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
