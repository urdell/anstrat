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
	/**
	 * Creates a new path following the same path as the instance.
	 * if lastCoordinate is not found, a clone of the path will be returned.
	 * @param lastCoordinate
	 * @return the first part of the path, ending with lastCoordinate
	 */
	public Path getSubPath(TileCoordinate lastCoordinate){
		Path subPath=new Path();
		for(TileCoordinate tc : path){
			subPath.path.add(tc);
			if (tc.equals(lastCoordinate)){
				break;
			}
		}	
		
		return subPath;
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
