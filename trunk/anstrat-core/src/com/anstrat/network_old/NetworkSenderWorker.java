package com.anstrat.network_old;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;

public class NetworkSenderWorker extends NetworkWorker {

	private BlockingQueue<NetworkMessage> outgoing;
	
	public NetworkSenderWorker(long retryDelay, GameSocket socket){
		super(retryDelay, socket);
		outgoing = new PriorityBlockingQueue<NetworkMessage>();
	}
	
	public void sendMessage(NetworkMessage message){
		Gdx.app.log(this.getClass().getSimpleName(), String.format("Adding command '%s' to send queue with priority %d.", message.getCommand(), message.getPriority()));
		outgoing.add(message);
	}
	
	@Override
	protected void doIOWork() throws InterruptedException, IOException {
		
		NetworkMessage message = null;
		
		try{
			// This operation blocks while the queue is empty
			message = outgoing.poll(Long.MAX_VALUE, TimeUnit.SECONDS);
			socket.sendObject(message);
			
			Gdx.app.log("NetworkSenderWorker", String.format("'%s' command sent.", message.getCommand()));
		}
		catch(IOException e){
			// Failed to send message, add it to the front of the queue again
			message.setPriority(Integer.MAX_VALUE);
			outgoing.add(message);
			
			throw e;
		}
	}
}
