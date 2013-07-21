package com.anstrat.core;

import com.anstrat.network.protocol.GameOptions;

public class Invite {

	public final long inviteId;
	public final GameOptions gameOptions;
	public final String otherPlayerName;
	
	public Invite(long inviteId, String otherPlayerName, GameOptions gameOptions){
		this.inviteId = inviteId;
		this.otherPlayerName = otherPlayerName;
		this.gameOptions = gameOptions;
	}
	
	public void accept(int team, int god){
		Main.getInstance().network.acceptInvite(inviteId, team, god);
	}
	
	public void decline(){
		Main.getInstance().network.declineInvite(inviteId);
	}
}
