package com.anstrat.networkv2;



/**
 * Encapsulates the NetworkWorker, making sure a user always exists and is logged in.
 * @author Erik
 *
 */
class NetworkSessionWorker extends NetworkWorker implements GameSocket.IConnectionListener {
	private static final int LOGIN_MESSAGE_PRIORITY = 10;
	
	public NetworkSessionWorker(GameSocket socket, final INetworkCallback callback) {
		super(socket, new NetworkWorker.INetworkCallback() {
			@Override
			public void messageReceived(NetworkMessage message) {
				// TODO: Intercept login messages
				
				callback.messageReceived(message);
			}
		});
		
		socket.addListener(this);
	}

	@Override
	public void connectionLost(Throwable cause) {
		// TODO: Invalidate login
	}

	@Override
	public void connectionEstablished() {
		// TODO: Send login message
		// Remember, login messages should have a high priority
	}
	
}
