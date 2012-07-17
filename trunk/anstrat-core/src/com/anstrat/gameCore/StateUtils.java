package com.anstrat.gameCore;

import com.anstrat.geography.TileCoordinate;

/**
 * Various useful tools (-to-be) for State etc
 * @author jay
 *
 */
public abstract class StateUtils {
	
	/**
	 * Gets the unit (if any) standing on the given tile
	 * @param tile Tile to retrieve unit from
	 * @return A Unit if tile contains any, else null
	 */
	public static Unit getUnitByTile(TileCoordinate tileCoordinates)
	{
		State state = State.activeState;
		
		for(Unit u : state.unitList.values())
			if(u.tileCoordinate.equals(tileCoordinates))
				return u;
		
		return null;
	}
	
	/**
	 * Gets the building (if any) placed on the given tile
	 * @param tile Tile to retrieve building from
	 * @return A Building if tile contains any, else null
	 */
	public static Building getBuildingByTile(TileCoordinate tileCoordinates)
	{
		return State.activeState.map.getBuildingByTile(tileCoordinates);
	}
	
	public static Building getCurrentPlayerCastle(){
		return State.activeState.map.getPlayersCastle(State.activeState.currentPlayerId);
	}
}
