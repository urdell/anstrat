package com.anstrat.command;

import com.anstrat.animation.Animation;
import com.anstrat.animation.FullscreenTextAnimation;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public abstract class CommandHandler {

	/**
	 * Will only be called from the gui on the local client
	 * @param command
	 */
	public static void execute(Command command){
		if(command.isAllowed()){			
			Gdx.app.log("CommandHandler", String.format("Executing valid '%s' command.", command));
			GameInstance.activeGame.onCommandExecute(command);
			command.execute();
			Pathfinding.ranges.clear();
		}
		else{
			Gdx.app.log("CommandHandler", String.format("Attempted to execute an invalid '%s' command. Disallowed.", command));
			
			if (command instanceof CreateUnitCommand) {
				Animation animation = new FullscreenTextAnimation(((CreateUnitCommand) command).getReason());
				GEngine.getInstance().animationHandler.runParalell(animation);
			}
		}
	}
	
	/**
	 * Will only be called when an incoming network turn is received
	 * @param turn
	 */
	public static void executeNetwork(Command command){
		if(command.isAllowed()){
			command.execute();
			
			Gdx.app.log("CommandHandler", String.format("Got a valid '%s' command from the server.", command));
		}
		else
			Gdx.app.log("CommandHandler", String.format("Got a invalid '%s' command from the server!", command));	
	}

	// TODO: This should probably be moved somewhere else
	
	/**
	 * Constructs a create command for the given unit type for the current player, placing it at or adjacent tiles to the player's castle
	 * @return the constructed command, or <code>null</code> if there's no room around the player's castle.
	 */
	public static Command generateCreateCommand(UnitType type){
		
		
		TileCoordinate castleCoordinate = null;
		if(StateUtils.getCurrentPlayerCastle() == null)
			return new CreateUnitCommand(State.activeState.getCurrentPlayer(), castleCoordinate, type); // no position -> invalid command.
		castleCoordinate = StateUtils.getCurrentPlayerCastle().tileCoordinate;
		
		// First check if castle position is free
		// TODO: Check terrain and buildings too?
		if(StateUtils.getUnitByTile(castleCoordinate) == null){
			CreateUnitCommand createUnit = new CreateUnitCommand(State.activeState.getCurrentPlayer(), castleCoordinate, type);
			if(createUnit.isAllowed())
				return createUnit;
		}
		
		for(Tile t : State.activeState.map.getNeighbors(castleCoordinate)){
			if(State.activeState.map.isAdjacent(t.coordinates, castleCoordinate))
				
			// Check if something's in the way
			// TODO: Check terrain and buildings too?
			if(StateUtils.getUnitByTile(t.coordinates) == null){
				CreateUnitCommand createUnitC = new CreateUnitCommand(State.activeState.getCurrentPlayer(), t.coordinates, type);
				if(createUnitC.isAllowed()){
					
					System.out.println(t.coordinates);
					return createUnitC;
				}
			}
		}
		
		
		return null;
	}
}
