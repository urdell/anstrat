package com.anstrat.network.protocol;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Simple custom protocol implementation.
 * @author jay
 *
 */
public class NetworkMessage implements Serializable, Comparable<NetworkMessage> {
	
	private static final long serialVersionUID = 3L;

	public enum Command {
		// Client -> Server
		LOGIN,
		CREATE_NEW_USER, 	
		SET_DISPLAY_NAME,
		REQUEST_GAME_UPDATE, 
		REQUEST_RANDOM_GAME,
		INVITE_PLAYER,
		ANSWER_INVITE,
		
		// Server -> Client
		ACCEPT_LOGIN, 
		DENY_LOGIN, 
		USER_CREDENTIALS, 
		DISPLAY_NAME_CHANGED, 
		DISPLAY_NAME_CHANGE_REJECTED,
		GAME_STATE_CORRUPTED,
		GAME_STARTED,
		INVITE_REQUEST,
		INVITE_PENDING,
		
		// Server <-> Client
		SEND_COMMAND,
	}
	
	private Command command;
	private List<Serializable> payload;
	private transient int priority;
	
	public NetworkMessage(Command command, List<Serializable> payload){
		this.command = command;
		this.payload = payload;
	}
	
	public NetworkMessage(Command command, Serializable... payload){
		this(command, Arrays.asList(payload));
	}
	
	/**
	 * Sets this message's priority, only affects in which order the message will be spent.
	 * @param priority the message's priority, 0 is the default value.
	 */
	public void setPriority(int priority){
		this.priority = priority;
	}
	
	public Command getCommand(){
		return this.command;
	}
	
	public int getPriority(){
		return this.priority;
	}
	
	public List<Serializable> getPayload(){
		return payload;
	}

	@Override
	public int compareTo(NetworkMessage o) {
		return this.priority - o.priority;
	}
}