package com.anstrat.network;

import com.anstrat.command.Command;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;

/**
 * Converts UI actions to network commands and vice-versa.<br>
 * All UI interaction with the network should pass through this class.
 * @author Erik
 *
 */
public class NetworkController {
	private final Network network;
	
	public NetworkController(Network network){
		this.network = network;
		
		// Creating the implementation of the listener as an anonymous class, as we don't want
		// the implemented methods to be directly callable on this object. (it would be confusing) 
		this.network.setListener(getNetworkResponseHandlerImplementation());
	}
	
	// UI actions
	// TODO: Add ui actions that invoke methods on com.anstrat.network.Network
	
	// Network listener implementation
	
	private INetworkResponseHandler getNetworkResponseHandlerImplementation(){
		return new INetworkResponseHandler() {
			
			@Override
			public void userCredentials(long userID, String password) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void loginDenied() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void loginAccepted() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void gameStateCorrupted(long gameID) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void gameStarted(long gameID, long seed, Map map, Player[] players) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void displayNameChanged(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void displayNameChangeRejected(String name) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void command(long gameID, int commandNr, Command command) {
				// TODO Auto-generated method stub
				
			}
		};
			
	}
}
