package com.anstrat.gameCore;

import com.anstrat.animation.Animation;
import com.anstrat.animation.CreateUnitAnimation;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GBuilding;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;

/**
 * 
 * handles ingame creation of stuff that must be known by both the state and the gEngine
 *
 */
public final class CreationHandler {

	
	/**
	 * Only call when initiating. Graphics will be invalid.
	 */
	public static Unit createUnitStateOnly(TileCoordinate coordinate, UnitType unitType, int ownerId){
		Unit unit = new Unit(unitType, ownerId);
		State state = State.activeState;
		unit.tileCoordinate = state.map.tiles[coordinate.x][coordinate.y].coordinates;
		state.addUnit(coordinate, unit);
		return unit;
	}
	
	/**
	 * Only call when ingame. Updates both state and graphics.
	 */
	public static Unit createUnit(TileCoordinate coordinate, UnitType unitType, int playerID){
		Unit unit = new Unit(unitType, playerID);
		State state = State.activeState;
		
		unit.tileCoordinate = state.map.tiles[coordinate.x][coordinate.y].coordinates;
		state.addUnit(coordinate, unit);
		GUnit tempUnit = new GUnit(unit);
		Animation animation = new CreateUnitAnimation(tempUnit);
		GEngine.getInstance().gUnits.put(unit.id, tempUnit);
		
		
		GEngine.getInstance().animationHandler.enqueue(animation);
		// Subtract gold
		state.players[playerID].gold -= unitType.cost;
		
		return unit;
	}
	
	/**
	 * Only call when initiating. Graphics will be invalid.
	 */
	public static Building createBuildingStateOnly(TileCoordinate coordinate, int buildingType){
		State state = State.activeState;
		Building building = new Building(buildingType, state.map.nextBuildingId++, -1);
		building.tileCoordinate = state.map.tiles[coordinate.x][coordinate.y].coordinates;
		state.map.setBuilding(coordinate, building);
		return building;
	}
	
	/**
	 * Only call when ingame. Updates both state and graphics.
	 */
	public static Building createBuilding(TileCoordinate coordinate, int buildingType){
		State state = State.activeState;
		Building building = new Building(buildingType, state.map.nextBuildingId++, -1);
		building.tileCoordinate = state.map.tiles[coordinate.x][coordinate.y].coordinates;
		state.map.setBuilding(coordinate, building);
		GEngine.getInstance().gBuildings.put(building.id,new GBuilding(building,
				GEngine.getInstance().getMap(),
				state.players[state.currentPlayerId].team));
		return building;
	}
}