package com.anstrat.command;


import java.io.Serializable;

import com.anstrat.core.NetworkGameInstance;
import com.anstrat.core.User;
import com.anstrat.gameCore.State;
import com.badlogic.gdx.Gdx;

/**
 * NOTE: Must not store references to non-simple classes, store their id's instead (playerID, unitID, buildingID etc). 
 * Otherwise the whole objects (and a lot of unnecessary data) will be sent over the network.
 * 
 * OK classes: TileCoordinate, UnitType, TerrainType
 */
public abstract class Command implements Serializable {
	private long callerId;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Always called by all commands
	 */
	protected Command(){
		this((State.activeState.getCurrentPlayer().ai == null) 
				? User.globalUserID
				: State.activeState.getCurrentPlayer().userID);	// The AI (currentPlayer) issued the command
	}
	
	/**
	 * @param callId the userID of the player that this command is originating from.
	 */
	protected Command(long callId){
		this.callerId = callId;
	}

	protected abstract void execute();
	
	public boolean isAllowed(){
		boolean allowed = (callerId == State.activeState.getCurrentPlayer().userID) ||
							(State.activeState.getCurrentPlayer().ai == null && 
							!(State.activeState.gameInstance instanceof NetworkGameInstance));
		
		if(!allowed){
			Gdx.app.log("Command", String.format("Refused due to invalid callerID, expected: '%d', found: '%d'", State.activeState.getCurrentPlayer().userID, callerId));
		}
		return allowed;
	}
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}
	
	@Override
	public int hashCode(){
		return getClass().hashCode() * 13 + (int)callerId * 17;
	}
}
