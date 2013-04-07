package com.anstrat.server.messageHandlers;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

import com.anstrat.command.Command;
import com.anstrat.geography.Map;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameSetup;
import com.anstrat.network.protocol.GameSetup.Player;
import com.anstrat.network.protocol.NetworkMessage;
import com.anstrat.server.IConnectionManager;
import com.anstrat.server.db.IDatabaseService;
import com.anstrat.server.db.Invite;
import com.anstrat.server.db.User;
import com.anstrat.server.db.Invite.Status;
import com.anstrat.server.events.ClientAuthenticatedEvent;
import com.anstrat.server.events.Event;
import com.anstrat.server.util.DependencyInjector.Inject;
import com.anstrat.server.util.Logger;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.google.common.primitives.Longs;

public class SocialMessageHandler {
	
	@Inject
	private Logger logger;
	
	@Inject
	private IConnectionManager connectionManager;
	
	@Inject
	private IDatabaseService db;
	
	public SocialMessageHandler(){
		Event.register(this);
	}
	
	public void inviteAccept(InetSocketAddress client, long inviteID, int team, int god){
		Invite invite = handleRequest(client, inviteID);
		if(invite == null) return;
		
		NetworkMessage inviteCompleted = new NetworkMessage(NetworkMessage.Command.INVITE_COMPLETED, true);
		
		// Send invite completed to client
		if(!connectionManager.sendMessage(client, inviteCompleted)){
			// Invite completion was failed to be sent to client, pretend this INVITE_ACCEPT message never happened
			logger.info("%s disconnected before %s could be sent, accept of invite '%d' ignored.", client, inviteCompleted.getCommand(), inviteID);
			return;
		}
		
		// Setup game
		GameSetup setup = createGameSetup(invite, team, god);
		Long gameID = db.createGame(setup);
		
		// If this fails, the invite will simply succeed without a game being started
		if(gameID != null){
			NetworkMessage gameStarted = new NetworkMessage(NetworkMessage.Command.GAME_STARTED, gameID, setup);
			
			// Assumes that if INVITE_COMPLETED succeeds, GAME_STARTED will also succeed
			// Assumes that if INVITE_COMPLETED fails, GAME_STARTED will also fail
			// This however is currently not guaranteed, as it's impossible to make sure 2 or 0 NetworkMessage's reaches the end client.
			connectionManager.sendMessage(client, gameStarted);
			connectionManager.sendMessage(invite.sender, gameStarted); 
		}
		else{
			logger.info("Failed to start invite game for users '%d' (sender) and '%d' (receiver).", invite.sender, invite.receiver);
		}
		
		// Send invite completed to original sender of invite
		if(connectionManager.sendMessage(invite.sender, inviteCompleted)){
			// Invite completion was successfully sent, remove it from the database as it has been processed by both players
			db.removeInvites(inviteID);
		}
		else{
			// User is not available at the moment, the invite will be sent on his next login
			db.updateInvite(inviteID, Invite.Status.ACCEPTED, gameID);
		}
	}
	
	public void inviteDecline(InetSocketAddress client, long inviteID){
		Invite invite = handleRequest(client, inviteID);
		if(invite == null) return;
		
		NetworkMessage inviteCompleted = new NetworkMessage(NetworkMessage.Command.INVITE_COMPLETED, false);
		
		// Send invite completed to client
		if(!connectionManager.sendMessage(client, inviteCompleted)){
			// Invite completion was failed to be sent to client, pretend this INVITE_DECLINE message never happened
			logger.info("%s disconnected before %s could be sent, decline of invite '%d' ignored.", client, inviteCompleted.getCommand(), inviteID);
			return;
		}
		
		// Send invite completed to original sender of invite
		if(connectionManager.sendMessage(invite.sender, inviteCompleted)){
			// Invite completion was successfully sent, remove it from the database as it has been processed by both players
			db.removeInvites(inviteID);
		}
		else{
			// User is not available at the moment, the invite will be sent on his next login
			db.updateInvite(inviteID, Invite.Status.REJECTED, null);
		}
	}
	
	public void invitePlayerByName(InetSocketAddress client, String name, GameOptions options){
		if(name == null){
			connectionManager.sendMessage(client, new NetworkMessage(NetworkMessage.Command.INVITE_FAILED, "Name cannot be null."));
			return;
		}
		
		User user = db.getUser(name);
		
		if(user != null){
			invitePlayer(client, user, options);
		}
		else{
			logger.info("%s attempted to invite user with name '%s', but that user does not exist.", client, name);
			connectionManager.sendMessage(client, new NetworkMessage(NetworkMessage.Command.INVITE_FAILED, String.format("A user named '%s' does not exist.", name)));
		}
	}
	
	public void invitePlayerByID(InetSocketAddress client, long userID, GameOptions options){
		User user = db.getUser(userID);
		
		if(user != null){
			invitePlayer(client, user, options);
		}
		else{
			logger.info("%s attempted to invite user with id '%d', but that user does not exist.", client, userID);
			connectionManager.sendMessage(client, new NetworkMessage(NetworkMessage.Command.INVITE_FAILED, String.format("A user with id '%d' does not exist.", userID)));
		}
	}
	
	private void invitePlayer(InetSocketAddress client, User user, GameOptions options){
		// Logged in?
		Long clientUserID = connectionManager.getUserID(client);
		
		if(clientUserID == null){
			logger.info("%s attempted to invite user by id '%d' without being logged in.", client, user.getUserID());
			return;
		}
		
		// Can't invite yourself
		if(clientUserID == user.getUserID()){
			logger.warning("%s (%d) attempted to invite himself.",  client, user.getUserID());
			connectionManager.sendMessage(client, new NetworkMessage(NetworkMessage.Command.INVITE_FAILED, "You can't invite yourself!"));
			return;
		}
		
		Invite invite = db.createInvite(clientUserID, user.getUserID(), options);
		if(invite != null){
			
			// Notify client the invite request has been handled
			connectionManager.sendMessage(client, new NetworkMessage(NetworkMessage.Command.INVITE_PENDING, invite.inviteID, user.getDisplayedName(), options));
			
			// Get display name of invite sender
			User sender = db.getUser(clientUserID);
			if(sender == null) throw new IllegalStateException(String.format("No user exists in database for logged in user '%d'.", clientUserID));
			
			// If the receiving user is currently online, invite request will be sent directly
			connectionManager.sendMessage(user.getUserID(), new NetworkMessage(NetworkMessage.Command.INVITE_REQUEST, invite.inviteID, sender.getDisplayedName(), options));
		}
	}
	
	@Subscribe
	public void onUserLoggedIn(final ClientAuthenticatedEvent event){
		
		// Retrieve invites where the user is either sender and receiver
		Invite[] invites = db.getInvites(event.getUserID());
		long userID = event.getUserID();
		List<Long> invitesToRemove = Lists.newArrayList();
		
		for(Invite invite : invites){
			// Notify user of incoming invite request
			if(userID == invite.receiver && invite.status == Status.PENDING){
				String senderName = db.getUser(invite.sender).getDisplayedName();
				NetworkMessage message = new NetworkMessage(NetworkMessage.Command.INVITE_REQUEST, invite.inviteID, senderName, invite.options);
				if(!connectionManager.sendMessage(event.getClient(), message)){
					// Connection was lost, stop early
					break;
				}
			}
			// Notify user of accepted invite request
			else if(userID == invite.sender && invite.status == Status.ACCEPTED){
				NetworkMessage message = new NetworkMessage(NetworkMessage.Command.INVITE_COMPLETED, invite.inviteID, false);
				
				if(connectionManager.sendMessage(event.getClient(), message)){
					// Message received, the invite has now been processed by both players and can be deleted
					invitesToRemove.add(invite.inviteID);
					
					// Assumes that if INVITE_COMPLETED succeeds, GAME_STARTED and any following TURN messages will also succeed
					// Assumes that if INVITE_COMPLETED fails, GAME_STARTED and any following TURN messages will also fail
					// This however is not be guaranteed, as it's impossible to make sure N or 0 NetworkMessage's reaches the end client.
					GameSetup setup = db.getGame(invite.gameID);
					
					if(setup != null){
						connectionManager.sendMessage(event.getClient(), new NetworkMessage(NetworkMessage.Command.GAME_STARTED, invite.gameID, setup));
						
						// Send any already present commands
						Command[] commands = db.getCommands(invite.gameID, 1);
						for(int i = 0; i < commands.length; i++){
							connectionManager.sendMessage(event.getClient(), new NetworkMessage(NetworkMessage.Command.SEND_COMMAND, invite.gameID, 1 + i, commands[i]));
						}
					}
					else{
						logger.info("Failed to find game between invited users '%d' (sender) and '%d' (receiver).", invite.sender, invite.receiver);
					}
				}
				else{
					// Connection was lost, stop early
					break;
				}
			}
			// Notify user of declined invite request
			else if(userID == invite.sender && invite.status == Status.REJECTED){
				NetworkMessage message = new NetworkMessage(NetworkMessage.Command.INVITE_COMPLETED, invite.inviteID, false);
				if(connectionManager.sendMessage(event.getClient(), message)){
					// Message received, the invite has now been processed by both players and can be deleted
					invitesToRemove.add(invite.inviteID);
				}
				else{
					// Connection was lost, stop early
					break;
				}
			}
		}
		
		long[] inviteIDs = Longs.toArray(invitesToRemove);
		db.removeInvites(inviteIDs);
	}
	
	private Invite handleRequest(InetSocketAddress client, long inviteID){
		// Logged in?
		Long clientUserID = connectionManager.getUserID(client);
		if(clientUserID == null){
			logger.info("%s attempted to accept/decline invite '%d', but is not logged in.", client, inviteID);
			return null;
		}
		
		// Invite exists?
		Invite invite = db.getInvite(inviteID);
		if(invite == null){
			logger.info("%s attempted to accept/decline invite '%d', but that invite does not exist.", client, inviteID);
			return null;
		}
		
		return invite;
	}
	
	private GameSetup createGameSetup(Invite invite, int team, int god){
		User sender = db.getUser(invite.sender);
		User receiver = db.getUser(invite.receiver);
		
		Player[] players = new Player[]{
			new Player(invite.sender, invite.options.team, invite.options.god, sender.getDisplayedName()),
			new Player(invite.receiver, team, god, receiver.getDisplayedName()),
		};
		
		// TODO: Select map according to map choice
		return new GameSetup(new Map(10, 10), new Random().nextLong(), players);
	}
}
