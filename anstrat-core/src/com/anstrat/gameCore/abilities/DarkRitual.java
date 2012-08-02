package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.BerserkAnimation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.DebuffAnimation;
import com.anstrat.core.Assets;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.EffectFactory;
import com.anstrat.geography.Tile;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.badlogic.gdx.graphics.Color;

public class DarkRitual extends Ability{

	private static final int AP_DRAIN_AMOUNT = 2;
	
	public DarkRitual() {
		super("Dark Ritual", "Performs a dark ritual in which they offer their life for AP-loss to adjacent enemy units", 0);
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
		
		for (Tile adjacentTile : adjacentTiles){
			Unit unit = StateUtils.getUnitByTile(adjacentTile.coordinates);
			if (unit != null){
				if (unit.ownerId != source.ownerId){
					unit.currentAP -= AP_DRAIN_AMOUNT;
					if (unit.currentAP < 0){
						unit.currentAP = 0;
					}
					Animation animation = new DebuffAnimation(unit, Color.RED);
					GEngine.getInstance().animationHandler.enqueue(animation);
				}
			}
		}
		source.resolveDeath();
		Animation animation = new DeathAnimation(source, source.tileCoordinate);
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


