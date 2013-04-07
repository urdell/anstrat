package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.RitualisticVortex;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;

public class DarkRitual extends Ability{

	private static final int AP_DRAIN_AMOUNT = 2;
	
	public DarkRitual() {
		super("Dark Ritual", "Performs a dark ritual sacrificing its life to drain Action Points from adjacent enemy units.", 0);
		iconName = "suicide-button";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isAllowed(Unit source) {
		return super.isAllowed(source);
	}
	
	@Override
	public void activate(Unit source) {
		super.activate(source);

		source.currentHP = 0;
		
		List<Tile> adjacentTiles = new ArrayList<Tile>();
		adjacentTiles = State.activeState.map.getNeighbors(source.tileCoordinate);
		
		List<Unit> units = new ArrayList<Unit>();
		
		for (Tile adjacentTile : adjacentTiles){
			Unit unit = StateUtils.getUnitByTile(adjacentTile.coordinates);
			if (unit != null){
				if (unit.ownerId != source.ownerId){
					units.add(unit);
					unit.currentAP -= AP_DRAIN_AMOUNT;
					if (unit.currentAP < 0){
						unit.currentAP = 0;
					}
				}
			}
		}
		source.resolveDeath();
		Animation animation = new RitualisticVortex(source, units);
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	
	@Override
	public String getIconName(Unit source) {
		if(!isAllowed(source)) return "heal-button-gray";
		if(GEngine.getInstance().selectionHandler.selectionType == SelectionHandler.SELECTION_TARGETED_ABILITY){
			return "heal-button-active";
		}
		return "heal-button";
	}
}


