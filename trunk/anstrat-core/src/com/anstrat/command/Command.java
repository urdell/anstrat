package com.anstrat.command;


import java.io.Serializable;

import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.State;
import com.badlogic.gdx.Gdx;

/**
 * NOTE: Must not store references to non-simple classes, store their id's instead (playerID, unitID, buildingID etc). 
 * Otherwise the whole objects (and a lot of unnecessary data) will be sent over the network.
 * 
 * OK classes: TileCoordinate, UnitType, TerrainType
 */
public abstract class Command implements Serializable {
	protected int playerID;
	
	private static final long serialVersionUID = 2L;
	
	/**
	 * Creates a command for the current player
	 */
	protected Command(){
		this(GameInstance.activeGame.getUserPlayer().playerId);
	}
	
	protected Command(int playerID){
		this.playerID = playerID;
	}

	protected abstract void execute();
	
	public boolean isAllowed(){
		boolean allowed = playerID == State.activeState.getCurrentPlayer().playerId;
		
		if(!allowed){
			Gdx.app.log("Command", String.format("Refused due to invalid playerID, found: '%d', expected: '%d'.", playerID, State.activeState.getCurrentPlayer().playerId));
		}
		return allowed;
	}
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName();
	}
	
	@Override
	public int hashCode(){
		return getClass().hashCode() * 13 + playerID * 17;
	}
}
