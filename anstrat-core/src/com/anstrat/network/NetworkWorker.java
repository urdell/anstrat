package com.anstrat.network;

import java.io.IOException;

import com.badlogic.gdx.Gdx;

public abstract class NetworkWorker implements Runnable {

	private long retryDelay;
	private Thread thread;
	
	protected GameSocket socket;
	private boolean paused;
	
	public NetworkWorker(long retryDelay, GameSocket socket){
		this.socket = socket;
		this.retryDelay = retryDelay;
		thread = new Thread(this);
		thread.setPriority(3);
		thread.setName(this.getClass().getSimpleName());
	}
	
	/**
	 * Starts this NetworkWorker, may only be called once.<br>
	 * Use {@link #resume()} instead when resuming a paused thread.
	 */
	public void start(){
		thread.start();
	}
	
	public void stop(){
		Gdx.app.log(this.getClass().getSimpleName(), "Interrupting thread.");
		thread.interrupt();
		socket.close();
	}
	
	public synchronized void pause(){
		this.paused = true;
	}
	
	public synchronized void resume(){
		if(this.paused){
			this.paused = false;
			this.notify();
		}
	}
	
	@Override
	public void run(){
		try{
			reconnectLoop();
		}
		catch(InterruptedException e){
			Gdx.app.log(this.getClass().getSimpleName(), "Network thread was interrupted.");
		}
		
		Gdx.app.log(this.getClass().getSimpleName(), "Thread died.");
	}
	
	private void reconnectLoop() throws InterruptedException {
		while(!Thread.currentThread().isInterrupted()){
			try{
				
				// wait() and notify() cannot be called without holding the 
				// lock on the object it is called on
				synchronized (this) {
					if(paused) wait();
				}
				
				// Reconnect if necessary
				synchronized (socket) {
					if(!socket.isConnected()){
						socket.connect();
					}
				}
				
				doIOWork();
			}
			catch(IOException e){
				// Prevent a reconnection from being performed too often
				// The sleep while holding a lock is intended, as it makes sure all NetworkWorker's have to wait the 'retryDelay'.
				synchronized (socket) {
					if(!socket.isConnected()){
						Gdx.app.log("NetworkWorker", String.format("Failed to connect to server %s:%d, retrying in %d ms...", socket.getHost(), socket.getPort(), retryDelay));
						Thread.sleep(retryDelay);
					}
				}
			}
		}
	}
	
	protected abstract void doIOWork() throws InterruptedException, IOException;
}
