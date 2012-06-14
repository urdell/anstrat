package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Map;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;

public abstract class TargetedAbility extends Ability{

	private static final long serialVersionUID = 1L;
	
	private final int range;
	
	public TargetedAbility(String name, String description, int apCost, int range) {
		super(name, description, apCost);
		this.range = range;
	}

	public List<TileCoordinate> getValidTiles(Unit source){
		Map map = State.activeState.map;
		List<TileCoordinate> validList = new ArrayList<TileCoordinate>();
		for(Tile[] row : map.tiles){
			for(Tile target : row){
				if(isAllowed(source, target.coordinates))
					validList.add(target.coordinates);
			}
		}
		return validList;
	}
	
	public boolean isAllowed(Unit source, TileCoordinate tc){
		return isAllowed(source) 
				&& Pathfinding.getDistance(source.tileCoordinate, tc) <= range;	// Target must be in range
	}
	
	public void activate(Unit source, TileCoordinate tc){
		super.activate(source);
	}
}
