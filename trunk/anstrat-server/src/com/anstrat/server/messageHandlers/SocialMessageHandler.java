package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;

import com.anstrat.server.IConnectionManager;

public class SocialMessageHandler {
	private final IConnectionManager connectionManager;
	
	public SocialMessageHandler(IConnectionManager connectionManager){
		this.connectionManager = connectionManager;
	}
	
	public void invitePlayer(InetSocketAddress client, String name){
		
	}
	
	public void answerInvite(InetSocketAddress client, long inviteID){
		
	}
}
