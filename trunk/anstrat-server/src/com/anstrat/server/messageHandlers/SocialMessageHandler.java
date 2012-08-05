package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;

import com.anstrat.server.IConnectionManager;
import com.anstrat.server.util.Logger;
import com.anstrat.server.util.DependencyInjector.Inject;

public class SocialMessageHandler {
	
	@Inject
	private Logger logger;
	
	@Inject
	private IConnectionManager connectionManager;
	
	public void invitePlayer(InetSocketAddress client, String name){
		
	}
	
	public void answerInvite(InetSocketAddress client, long inviteID){
		
	}
}
