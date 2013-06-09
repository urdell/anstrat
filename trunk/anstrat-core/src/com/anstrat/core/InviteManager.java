package com.anstrat.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.anstrat.menu.InvitesMenu;
import com.anstrat.network.protocol.GameOptions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public class InviteManager {
	
	/**
	 * Invites the user has recieved
	 */
	private List<Invite> invites = new ArrayList<Invite>();
	/**
	 * Invites the user has sent
	 */
	private List<Invite> outgoingInvites = new ArrayList<Invite>();
	private FileHandle invitesFile;
	public Button inviteButton;
	
	public InviteManager(FileHandle invitesFile){
		this.invitesFile = invitesFile;
	}
	
	public void recievedInvite(long inviteId, String senderName, GameOptions gameInfo){
		invites.add( new Invite(inviteId, senderName, gameInfo));
		refresh();
	}
	
	public void inviteCompleted(long inviteID, boolean accept) {
		Invite inviteToBeRemoved = null;
		
		for(Invite invite : invites){
			if(invite.inviteId == inviteID)
				inviteToBeRemoved = invite;	
		}
		invites.remove(inviteToBeRemoved);
		
		for(Invite invite : outgoingInvites){
			if(invite.inviteId == inviteID)
				inviteToBeRemoved = invite;	
		}
		outgoingInvites.remove(inviteToBeRemoved);
		
		refresh();
	}

	public void invitePending(long inviteID, String receiverDisplayName, GameOptions options) {
		outgoingInvites.add( new Invite(inviteID, receiverDisplayName, options));
		refresh();
		
	}
	
	public List<Invite> getInvites(){
		return invites;
	}
	public List<Invite> getSentInvites(){
		return outgoingInvites;
	}
	
	private void refresh(){
		if(inviteButton != null){
			inviteButton.setVisible(( !invites.isEmpty() || !outgoingInvites.isEmpty() ));
			inviteButton.setVisible(true); //TODO remove after finished with testing
		}
		
		InvitesMenu.getInstance().refresh();
	}
	
	/**
	 * This button will be visible or invisible depending on invite state.
	 */
	public void registerInviteButton(Button inviteButton){
		this.inviteButton = inviteButton;
		refresh();
	}
	
	public void saveInviteInstances(){
		Serialization.writeObject(new InviteInstanceList(invites, outgoingInvites), invitesFile);
	}
	
	public void loadInviteInstances(){
		Object obj = Serialization.readObject(invitesFile);
		
		if(obj == null){
			Gdx.app.log("Invite", "No previous invite instances found.");
		}
		else{
			invites = ((InviteInstanceList)obj).invites;
			outgoingInvites = ((InviteInstanceList)obj).outgoingInvites;
		}
	}
	
	// Class used only to serialize/deserialize invites
	private static class InviteInstanceList implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private List<Invite> invites;
		private List<Invite> outgoingInvites;
		
		public InviteInstanceList(List<Invite> invites, List<Invite> outgoingInvites){
			this.invites = invites;
			this.outgoingInvites = outgoingInvites;
		}

		@Override
		public String toString() {
			return String.format("%s(size = %d + %d)", this.getClass().getSimpleName(), invites.size(), outgoingInvites.size());
		}
	}
}
