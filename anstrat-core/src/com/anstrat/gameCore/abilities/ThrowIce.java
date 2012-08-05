package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anstrat.animation.Animation;
import com.anstrat.animation.ChainingAxeAnimation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.ThrowIceAnimation;
import com.anstrat.animation.UpdateBarAnimation;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.DamageRow;
import com.anstrat.gui.confirmDialog.TextRow;

public class ThrowIce extends TargetedAbility{
	
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 5;
	private static final int RANGE = 2;

	
	public ThrowIce(){
		super("Throwing Ice block","Throws a Ice block which will cause AoE damage in adjacent Tiles",AP_COST, RANGE);
	}
	
	

	public boolean isAllowed(Unit source, TileCoordinate coordinates) {
		Unit targetUnit = StateUtils.getUnitByTile(coordinates);
		
		return super.isAllowed(source, coordinates) 
				&& targetUnit != null
				&& targetUnit.ownerId != source.ownerId;
	}

	@Override
	public void activate(Unit source, TileCoordinate coordinate) {
		float splashReduction = 0.5f;
		Map<Unit, Integer> map = new HashMap<Unit, Integer>();
		super.activate(source, coordinate);
		
		Unit targetUnit = StateUtils.getUnitByTile(coordinate);
		targetUnit.currentHP -= source.getAttack();
		map.put(targetUnit, source.getAttack());
		Animation updateBarAnimation = new UpdateBarAnimation(source);
		GEngine.getInstance().animationHandler.enqueue(updateBarAnimation);
	
		
		List<Tile> adjacentTiles = new ArrayList<Tile>();
		adjacentTiles = State.activeState.map.getNeighbors(targetUnit.tileCoordinate);
		List<Unit> alreadyHit = new ArrayList<Unit>();
		targetUnit.resolveDeath();
		
		
		
		
			
		int splashDamage = (int)(source.getAttack()*splashReduction);	
		for (Tile adjacentTile : adjacentTiles){
			
			Unit unit = StateUtils.getUnitByTile(adjacentTile.coordinates);
			if (unit != null){
				if (unit.ownerId != source.ownerId){					
					unit.currentHP -= splashDamage;
					unit.resolveDeath();
					map.put(unit, splashDamage);
				}
			}
		}
		
		Animation throwIceAnimation = new ThrowIceAnimation(targetUnit.tileCoordinate, map);
		GEngine.getInstance().animationHandler.enqueue(throwIceAnimation);	
	}
	
	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(source.getAttack(), source.getAttack());
		return ConfirmDialog.abilityConfirm(position, nameRow, apRow, damageRow);
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
