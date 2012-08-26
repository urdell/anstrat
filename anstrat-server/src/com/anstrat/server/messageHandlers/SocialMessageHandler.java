package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;

import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.IDatabaseService;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;

public class SocialMessageHandler {
	
	@Inject
	private Logger logger;
	
	@Inject
	private IConnectionManager connectionManager;
	
	@Inject
	private IDatabaseService database;
	
	public void invitePlayer(InetSocketAddress client, String name){
		
	}
	
	public void invitePlayer(InetSocketAddress client, long userID){
		
	}
	
	public void answerInvite(InetSocketAddress client, long inviteID){
		
	}
}
