package com.anstrat.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.anstrat.gui.GEngine;
import com.anstrat.menu.GameInvitePopup;
import com.anstrat.menu.SplashScreen;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.popup.Popup;

public class InviteManager {
	
	private Map<Long, Invite> sentInvites = new HashMap<Long, Invite>();
	private Collection<Invite> receivedInvitesQueue = new ArrayList<Invite>();
	private Collection<Invite> declinedInvitesQueue = new ArrayList<Invite>();
	
	public void recievedInvite(long inviteID, String senderName, GameOptions options){
		Invite invite = new Invite(inviteID, senderName, options);
		
		if (queuePopup()) {
			receivedInvitesQueue.add(invite);
		}
		else {
			new GameInvitePopup(invite).show();
		}
	}
	
	public void inviteCompleted(long inviteID, boolean accept) {
		Invite invite = sentInvites.remove(inviteID);

		if (invite != null && !accept) {
			// If player is in a game, queue popup
			if (queuePopup()) {
				declinedInvitesQueue.add(invite);
			}
			else {
				Popup.showGenericPopup("Invite declined", String.format("%s has declined your invite.", invite.otherPlayerName));
			}	
		}
	}

	public void invitePending(long inviteID, String receiverDisplayName, GameOptions options) {
		sentInvites.put(inviteID, new Invite(inviteID, receiverDisplayName, options));
	}	

	public void playerLeftGameScreen() {
		showPendingPopups();
	}
	
	public void playerLeftSplashScreen(){
		showPendingPopups();
	}
	
	private void showPendingPopups(){
		for(Invite invite : receivedInvitesQueue) {
			new GameInvitePopup(invite).show();	
		}
		
		for(Invite invite : declinedInvitesQueue) {
			Popup.showGenericPopup("Invite declined", String.format("%s has declined your invite.", invite.otherPlayerName));	
		}
		
		receivedInvitesQueue.clear();
		declinedInvitesQueue.clear();
	}
	
	private static boolean queuePopup(){
		boolean inGame = Main.getInstance().getScreen() instanceof GEngine;
		boolean inSplash = Main.getInstance().getScreen() instanceof SplashScreen;
		return inGame || inSplash;
	}
}
