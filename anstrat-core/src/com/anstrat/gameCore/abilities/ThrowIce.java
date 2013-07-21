package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anstrat.animation.Animation;
import com.anstrat.animation.ThrowIceAnimation;
import com.anstrat.animation.UpdateBarAnimation;
import com.anstrat.gameCore.Combat;
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
	private static final float DAMAGEMULTIPLIER = 1.5f;

	public ThrowIce(){
		super("Ice Block", "Throws an ice block that does a lot of damage on the target and also damages nearby units.", AP_COST, RANGE);
		iconName = "throw-ice-button";
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
		int minDamage = Combat.minDamage(source, targetUnit, DAMAGEMULTIPLIER);
		int maxDamage = Combat.maxDamage(source, targetUnit, DAMAGEMULTIPLIER);
		int damage = State.activeState.random.nextInt( maxDamage-minDamage+1 ) + minDamage; // +1 because random is exclusive
		targetUnit.currentHP -= damage;
		map.put(targetUnit, damage);
		Animation updateBarAnimation = new UpdateBarAnimation(source);
		GEngine.getInstance().animationHandler.enqueue(updateBarAnimation);
	
		
		List<Tile> adjacentTiles = new ArrayList<Tile>();
		adjacentTiles = State.activeState.map.getNeighbors(targetUnit.tileCoordinate);
		
		int splashDamage = (int)(damage*splashReduction);	
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
		
		Animation throwIceAnimation = new ThrowIceAnimation(source, targetUnit.tileCoordinate, map, damage, splashDamage);
		GEngine.getInstance().animationHandler.enqueue(throwIceAnimation);	
	}
	
	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(
				Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER), 
				Combat.maxDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER));
		return ConfirmDialog.abilityConfirm(position, "confirm-ice", damageRow, ConfirmDialog.apcost, apRow);
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
