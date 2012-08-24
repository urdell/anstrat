package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.ChainingAxeAnimation;
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

public class ChainingAxe extends TargetedAbility {

	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 4;
	private static final int RANGE = 2;

	public ChainingAxe(){
		super("Chaining Axe","Throws a magic axe jumping between up to 4 adjacent enemies, damage is reduced by for each succesive hit",AP_COST, RANGE);
		iconName = "chainaxe-button";
	}
	
	public boolean isAllowed(Unit source, TileCoordinate coordinates) {
		Unit targetUnit = StateUtils.getUnitByTile(coordinates);
		
		return super.isAllowed(source, coordinates) 
				&& targetUnit != null
				&& targetUnit.ownerId != source.ownerId;
	}

	@Override
	public void activate(Unit source, TileCoordinate coordinate) {
		super.activate(source, coordinate);
		
		Unit targetUnit = StateUtils.getUnitByTile(coordinate);
		int numberOfHits = 1;
		targetUnit.currentHP -= source.getAttack();
		Unit lastHit = targetUnit;
		TileCoordinate lastHitTile = targetUnit.tileCoordinate;
		List<Tile> adjacentTiles = new ArrayList<Tile>();
		List<Unit> alreadyHit = new ArrayList<Unit>();
		boolean hadTarget = true;
		boolean firstUnit = true;
		targetUnit.resolveDeath();
		alreadyHit.add(targetUnit);
		
		Animation chainingAxeAttackAnimation = new ChainingAxeAnimation(source,targetUnit,source.getAttack(), firstUnit);
		GEngine.getInstance().animationHandler.enqueue(chainingAxeAttackAnimation);
		firstUnit = false;
		
		while(numberOfHits<4 && hadTarget){
			
			adjacentTiles = State.activeState.map.getNeighbors(lastHitTile);
			
			for (Tile adjacentTile : adjacentTiles){
				Unit unit = StateUtils.getUnitByTile(adjacentTile.coordinates);
				if (unit != null){
					if (unit.ownerId != source.ownerId && unit.currentHP > 0 && !alreadyHit.contains(unit)){
						unit.currentHP -= (source.getAttack()-numberOfHits);
						Animation chainingAxeAnimation = new ChainingAxeAnimation(lastHit, unit, (source.getAttack()-numberOfHits), firstUnit);
						GEngine.getInstance().animationHandler.enqueue(chainingAxeAnimation);
						lastHit = unit;
						lastHitTile = unit.tileCoordinate;
						unit.resolveDeath();
						alreadyHit.add(unit);
						hadTarget = true;
						numberOfHits++;
						break;
					}
					else{
						hadTarget = false;
					}	
				}
				else{
					hadTarget = false;
				}
			}
		}
		
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
