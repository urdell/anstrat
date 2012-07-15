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
	public static final int NOT_ADJECANT = -1;
	public static final int ADJECANT_SW = 0;
	public static final int ADJECANT_S = 1;
	public static final int ADJECANT_SE = 2;
	public static final int ADJECANT_NE = 3;
	public static final int ADJECANT_N = 4;
	public static final int ADJECANT_NW = 5;
	public static int NearbyOrientation(TileCoordinate center, TileCoordinate adj){
		if(center.x%2==1){		// bottom peak
			if(center.x - 1 == adj.x && center.y == adj.y)   //left
				return ADJECANT_NW;
			if(center.x == adj.x && center.y - 1 == adj.y)   //up
				return ADJECANT_N;
			if(center.x + 1 == adj.x && center.y == adj.y)   //right
				return ADJECANT_NE;
			if(center.x - 1 == adj.x && center.y + 1 == adj.y)   //left+down
				return ADJECANT_SW;
			if(center.x == adj.x && center.y + 1 == adj.y)   //down
				return ADJECANT_S;
			if(center.x + 1 == adj.x && center.y + 1 == adj.y)   //right+down
				return ADJECANT_SE;
		}
		else if(center.x%2==0){		// top peak
			if(center.x - 1 == adj.x && center.y == adj.y)   //left
				return ADJECANT_SW;
			if(center.x == adj.x && center.y - 1 == adj.y)   //up
				return ADJECANT_N;
			if(center.x + 1 == adj.x && center.y == adj.y)   //right
				return ADJECANT_SE;
			if(center.x - 1 == adj.x && center.y - 1 == adj.y)   //left+up
				return ADJECANT_NW;
			if(center.x == adj.x && center.y + 1 == adj.y)   //down
				return ADJECANT_S;
			if(center.x + 1 == adj.x && center.y - 1 == adj.y)   //right+up
				return ADJECANT_NE;
		}
		return -1;  // no case fulfilled -> not adjecant.
	}
}
