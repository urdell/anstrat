package com.anstrat.network;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.anstrat.network.protocol.NetworkMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.PauseableThread;


public abstract class NetworkWorker {
	private GameSocket socket;
	private PauseableThread sender, reader;
	private INetworkCallback callback;
	protected BlockingQueue<NetworkMessage> outgoing;
	
	private int reconnectCount;
	private static final long[] RETRY_DELAY = {5000, 10000, 15000, 30000};
	
	public NetworkWorker(final GameSocket socket){
		this.socket = socket;
		outgoing = new PriorityBlockingQueue<NetworkMessage>();
		
		// Sender thread, keeps a queue of messages to send
		sender = createThread(new IOTask() {
			@Override
			public void run() throws IOException, InterruptedException {
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
		});
		
		// Reader thread
		reader = createThread(new IOTask() {
			@Override
			public void run() throws IOException, InterruptedException {
				Object obj = null;
				Throwable cause = null;
				
				try {
					// This is a blocking operation
					obj = socket.readObject();
				}
				catch (ClassNotFoundException e) {
					cause = e;
				}
				
				if(obj instanceof NetworkMessage){
					NetworkMessage message = (NetworkMessage)obj;
					Gdx.app.log("NetworkReaderWorker", String.format("Received network command '%s'.", message.getCommand()));
					if(callback != null) callback.messageReceived((NetworkMessage) obj);
				}
				else{
					throw new GdxRuntimeException("Malformed network input (not a NetworkMessage).", cause);
				}
			}
		});
	}
	
	public void setCallback(INetworkCallback callback){
		this.callback = callback;
	}
	
	public void start(){
		sender.start();
		reader.start();
	}
	
	public void stop(){
		sender.stopThread();
		reader.stopThread();
		
		sender.interrupt();
		reader.interrupt();
		
		this.socket.close();
	}
	
	public GameSocket getSocket(){
		return this.socket;
	}
	
	private PauseableThread createThread(final IOTask task){
		return new PauseableThread(new Runnable(){
			@Override
			public void run() {
				try{
					reconnectHandler(task);
				}
				catch(InterruptedException e){
					Gdx.app.log(this.getClass().getSimpleName(), "Network thread was interrupted.");
					Thread.currentThread().interrupt();
					Gdx.app.log(this.getClass().getSimpleName(), "Thread died.");
				}
			}
		});
	}
	
	private void reconnectHandler(IOTask ioTask) throws InterruptedException {
		try {
			// Reconnect (if necessary)
			synchronized (socket) {
				if(!socket.connect()){
					long retryDelay = RETRY_DELAY[reconnectCount < RETRY_DELAY.length ? reconnectCount : RETRY_DELAY.length - 1];
					
					// Prevent a reconnection from being performed too often
					// The sleep while holding a lock is intended, as it makes sure all both threads wait at least the retry delay
					Gdx.app.log("NetworkWorker", String.format("Failed to connect to server %s:%d, retrying in %d ms...", socket.getHost(), socket.getPort(), retryDelay));
					Thread.sleep(retryDelay);
					
					reconnectCount++;
				}
				else{
					reconnectCount = 0;
				}
			}
			
			ioTask.run();
		}
		catch(IOException e){
			// Simply aborts the current IOTask
		}
	}
	
	public static interface INetworkCallback {
		public void messageReceived(NetworkMessage message);
	}
	
	private static interface IOTask {
		public void run() throws IOException, InterruptedException;
	}
}
