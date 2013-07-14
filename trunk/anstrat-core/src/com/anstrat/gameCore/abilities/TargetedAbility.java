package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Map;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.TextRow;

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
	
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		return ConfirmDialog.abilityConfirm(position, "confirm-ability", nameRow, apRow);
	}
	
	public void activate(Unit source, TileCoordinate tc){
		super.activate(source);
	}
}
