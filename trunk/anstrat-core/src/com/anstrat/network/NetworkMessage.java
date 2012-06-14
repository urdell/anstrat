package com.anstrat.network;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Simple custom protocol implementation.
 * @author jay
 *
 */
public class NetworkMessage implements Serializable, Comparable<NetworkMessage> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6944277581424954132L;
	private String command;
	private List<Serializable> payload;
	private transient int priority;
	
	public NetworkMessage(String command, List<Serializable> payload)
	{
		this.command = command;
		this.payload = payload;
	}
	
	public NetworkMessage(String command, Serializable... payload)
	{
		this(command, Arrays.asList(payload));
	}
	
	/**
	 * Sets this message's priority, only affects in which order the message will be spent.
	 * @param priority the message's priority, 0 is the default value.
	 */
	public void setPriority(int priority){
		this.priority = priority;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public int getPriority(){
		return this.priority;
	}
	
	public List<Serializable> getPayload()
	{
		return payload;
	}

	@Override
	public int compareTo(NetworkMessage o) {
		return this.priority - o.priority;
	}
}