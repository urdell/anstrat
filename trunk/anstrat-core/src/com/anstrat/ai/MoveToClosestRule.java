package com.anstrat.ai;



import java.util.ArrayList;
import java.util.List;

import com.anstrat.command.MoveCommand;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.ActionMap;

public class MoveToClosestRule extends Rule{

	@Override
	public void prepare(AIKnowledge knowledge) {
		State state = State.activeState;
		
		List<Tile> tilesNearEnemies = new ArrayList<Tile>();
		for(Unit u : knowledge.enemyUnits){  //find all tiles adjacent to enemies
			List<Tile> nearbyTiles = state.map.getNeighbors(u.tileCoordinate);
			tilesNearEnemies.addAll(nearbyTiles);
			for(Tile t : nearbyTiles){
				if(knowledge.consideredUnit.tileCoordinate.equals( t.coordinates )){ // Already adjacent
					command = null;
					value = 0;
					return;
				}
			}
		}
		int lowestAPCost = Integer.MAX_VALUE;
		TileCoordinate chosenCoordinate = null;
		for(Tile t : tilesNearEnemies){  //find the nearest tile, with regard to the unit considered by AIKnowledge
			if(knowledge.consideredActionMap.getActionType(t.coordinates) != ActionMap.ACTION_NULL &&
					knowledge.consideredActionMap.getCost(t.coordinates) < lowestAPCost){
				chosenCoordinate = t.coordinates;
				lowestAPCost = knowledge.consideredActionMap.getCost(t.coordinates);
			}
		}
		if(lowestAPCost != Integer.MAX_VALUE){  // can reach an enemy
			command = new MoveCommand(knowledge.consideredUnit, chosenCoordinate);
			value = 120 + (knowledge.consideredUnit.currentAP-lowestAPCost)*20;  // 120 base value, more value if you have more ap left
			if(!command.isAllowed())
				value = 0;
		}
		else{  //can not reach an enemy
			command = null;
			value = 0;
			return;
		}
		
	}
	
	
	

}
