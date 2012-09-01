package com.anstrat.core;

import java.io.Serializable;

import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameSetup;

public class Invite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public long inviteId;
	public GameOptions gameOptions;
	public String otherPlayerName;
	
	public Invite(long inviteId, String otherPlayerName, GameOptions gameOptions){
		this.inviteId = inviteId;
		this.otherPlayerName = otherPlayerName;
		this.gameOptions = gameOptions;
	}
	
	
	
}
