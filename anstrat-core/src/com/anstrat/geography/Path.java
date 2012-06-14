package com.anstrat.geography;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.gameCore.State;
import com.anstrat.gameCore.UnitType;

public class Path {
	
	/**
	 * The path starts with the first tile adjecant to the starting position, and always ends with the target.
	 */
	public List<TileCoordinate> path = new ArrayList<TileCoordinate>();
	
	public Path(){
		
	}
	public Path(List<TileCoordinate> coords){
		path=coords;
	}

	public int getPathCost(UnitType type){
		int cost = 0;
		Map map = State.activeState.map;
		
		if(path!=null)
			for(TileCoordinate tc : path){
				cost += type.getTerrainPenalty(map.getTile(tc).terrain);
			}
		
		return cost;		
	}
	

}
