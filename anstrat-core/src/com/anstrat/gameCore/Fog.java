package com.anstrat.gameCore;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.geography.Map;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;

public class Fog {
	
	public static final int VIEW_DISTANCE = 2;

	public static void initFog(Map map) {
		if(map.fogEnabled) {
			for(Tile[] row : map.tiles){
				for(Tile tile : row){
					tile.visible[0] = 0;
					tile.visible[1] = 0;
				}
			}
		}
	}
	
	public static void recalculateFog(int playerId, State state){
		recalculateFog(playerId, state, null, null);
	}
	
	public static void recalculateFog(int playerId, State state, TileCoordinate extraTile, TileCoordinate notYet){
		Map map = state.map;
		fogTurn(playerId,state.map);
		if(map.fogEnabled) {
			List<TileCoordinate> controlledTiles = new ArrayList<TileCoordinate>();
			for(Unit unit : state.unitList.values()) {
				if (unit.ownerId == playerId)
					controlledTiles.add(unit.tileCoordinate);
			}
			if(notYet != null)
				controlledTiles.remove(notYet);
			if(extraTile != null)
				controlledTiles.add(extraTile);
			for(Building building : map.buildingList.values()) {
				if (building.controllerId == playerId)
					controlledTiles.add(building.tileCoordinate);
			}
			for (TileCoordinate tc : controlledTiles) {
				for(Tile[] row : map.tiles){
					for(Tile tile : row){
						if (Pathfinding.getDistance(tc, tile.coordinates) <= VIEW_DISTANCE){
							if(tile.visible[playerId] < 1)
								tile.visible[playerId] = 1;
						}
					}
				}
			}
		}
	}
	
	public static void fogTurn(int playerId, Map map){
		if(map.fogEnabled) {
			for(Tile[] row : map.tiles){
				for(Tile tile : row){
					if (tile.visible[playerId] > 0)
						tile.visible[playerId] -= 1;
				}
			}
		}
	}
	
	public static boolean isVisible(TileCoordinate tile, int playerId) {
		if(State.activeState.map.tiles[tile.x][tile.y].visible == null ||
				!State.activeState.map.fogEnabled) 
			return true;
		return State.activeState.map.tiles[tile.x][tile.y].visible[playerId] > 0;
	}
}
