package com.anstrat.network;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class NetworkReaderWorker extends NetworkWorker{

	private INetworkReaderListener listener;
	
	public NetworkReaderWorker(long retryDelay, GameSocket socket, INetworkReaderListener listener) {
		super(retryDelay, socket);
		this.listener = listener;
	}

	@Override
	protected void doIOWork() throws InterruptedException, IOException {
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
			listener.messageReceived((NetworkMessage) obj);
		}
		else{
			throw new GdxRuntimeException("Malformed network input (not a NetworkMessage).", cause);
		}
	}
}
