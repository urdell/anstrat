package com.anstrat.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;

public class ActionMap {
	
	/** Cannot perform an action on this tile. Other fields may be undefined */
	public static final int ACTION_NULL = 0;
	/** Clicking this coordinate will issue a move command */
	public static final int ACTION_MOVE = 1;
	public static final int ACTION_ATTACK_MELEE = 2;
	public static final int ACTION_ATTACK_RANGED = 3;
	
	public int xSize, ySize;

	/** Contains cost of performing actions at given tile. Coordinates that can't have actions performed may be undefined. */
	//public int[][] costMap;
	public HashMap<TileCoordinate, Integer> costMap;
	/** Contains action types as integers. */
	//public int[][] actionTypeMap;
	public HashMap<TileCoordinate, Integer> actionTypeMap;
	/** Do not read when not valid. Any performed command should render it invalid. Writing to it should end with making it valid. */
	public boolean isValid = false;
	
	
	
	
	public ActionMap(){
		
		costMap = new HashMap<TileCoordinate, Integer>();
		actionTypeMap = new HashMap<TileCoordinate, Integer>();
		//costMap = new int[xSize][ySize];
		//actionTypeMap = new int[xSize][ySize];
		
	}
	
	public int getCost(TileCoordinate coordinate){

		return costMap.get(coordinate);
		//return costMap[coordinate.x][coordinate.y];
	}
	
	public int getActionType(TileCoordinate coordinate){
		if(actionTypeMap.get(coordinate) == null){
			return ACTION_NULL;
		}
		return actionTypeMap.get(coordinate);
		//return actionTypeMap[coordinate.x][coordinate.y];
	}
	public void setCost(TileCoordinate coordinate, int cost){
		costMap.put(coordinate, cost);
	}
	public void setActionType(TileCoordinate coordinate, int actionType){
		actionTypeMap.put(coordinate, actionType);	
	}
	public void setAction(TileCoordinate coordinate, int actionType, int cost){
		actionTypeMap.put(coordinate, actionType);
		costMap.put(coordinate, cost);
	}
	
	public void prepare(Unit u){
		clear();
		Pathfinding.updateActionMapMoves(u, this);
		
		for(Unit otherUnit : State.activeState.unitList.values()){
			actionTypeMap.remove(otherUnit.tileCoordinate);
			if(otherUnit.ownerId != u.ownerId){
				if(Pathfinding.getDistance(u.tileCoordinate, otherUnit.tileCoordinate) == 1){
					setAction(otherUnit.tileCoordinate, ACTION_ATTACK_MELEE, u.getAPCostAttack());
				} else if(Pathfinding.getDistance(u.tileCoordinate, otherUnit.tileCoordinate) <= u.getMaxAttackRange()){
					setAction(otherUnit.tileCoordinate, ACTION_ATTACK_RANGED, u.getAPCostAttack());
				}
			}
		}
		
		for(Tile[] tilerow : State.activeState.map.tiles) {
			for(Tile tile : tilerow) {
				if (getActionType(tile.coordinates) != ACTION_NULL && !Fog.isVisible(tile.coordinates, State.activeState.currentPlayerId)) {
					setActionType(tile.coordinates, ACTION_NULL);
				}
			}
		}
			
		isValid = true;
	}
	
	public List<TileCoordinate> getAllowedTiles() {
		List<TileCoordinate> ret = new ArrayList<TileCoordinate>();
		System.out.println("test entryset bla bla: "+actionTypeMap.entrySet().size());
		for(Entry<TileCoordinate, Integer> tile: actionTypeMap.entrySet()) {
			if(tile.getValue() != ACTION_NULL && Fog.isVisible(tile.getKey(), State.activeState.currentPlayerId)) 
				ret.add(tile.getKey());
		}
		System.out.println("test allowed bla bla: "+ret.size());
		return ret;
	}
	
	public void clear(){
		actionTypeMap.clear();
		isValid=false;
	}
	
}
