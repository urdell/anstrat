package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.ChainingAxeAnimation;
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

public class ChainingAxe extends TargetedAbility {

	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 4;
	private static final int RANGE = 2;
	private static final float DAMAGEMULTIPLIER = 1.2f;

	public ChainingAxe(){
		super("Chain Axe", "A chain attack that hits the targeted enemy and continues to hit adjacent targets. Damage is reduced for each successive hit.",AP_COST, RANGE);
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
		int minDamage = Combat.minDamage(source, targetUnit, DAMAGEMULTIPLIER);
		System.out.println(minDamage);
		int maxDamage = Combat.maxDamage(source, targetUnit, DAMAGEMULTIPLIER);
		System.out.println(maxDamage);
		int damage = State.activeState.random.nextInt( maxDamage-minDamage+1 ) + minDamage; // +1 because random is exclusive
		targetUnit.currentHP -= damage;
		Unit lastHit = targetUnit;
		TileCoordinate lastHitTile = targetUnit.tileCoordinate;
		List<Tile> adjacentTiles = new ArrayList<Tile>();
		List<Unit> alreadyHit = new ArrayList<Unit>();
		boolean hadTarget = true;
		boolean firstUnit = true;
		targetUnit.resolveDeath();
		alreadyHit.add(targetUnit);
		
		Animation chainingAxeAttackAnimation = new ChainingAxeAnimation(source,targetUnit,damage, firstUnit);
		GEngine.getInstance().animationHandler.enqueue(chainingAxeAttackAnimation);
		firstUnit = false;
		
		while(numberOfHits<4 && hadTarget){
			
			adjacentTiles = State.activeState.map.getNeighbors(lastHitTile);
			
			for (Tile adjacentTile : adjacentTiles){
				Unit unit = StateUtils.getUnitByTile(adjacentTile.coordinates);
				if (unit != null){
					if (unit.ownerId != source.ownerId && unit.currentHP > 0 && !alreadyHit.contains(unit)){
						minDamage = Combat.minDamage(source, unit, DAMAGEMULTIPLIER-numberOfHits*0.2f);
						maxDamage = Combat.maxDamage(source, unit, DAMAGEMULTIPLIER-numberOfHits*0.2f);
						damage = State.activeState.random.nextInt( maxDamage-minDamage+1 ) + minDamage; // +1 because random is exclusive
						unit.currentHP -= damage;
						Animation chainingAxeAnimation = new ChainingAxeAnimation(lastHit, unit, damage, firstUnit);
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
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(
				Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER), 
				Combat.maxDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER));
		return ConfirmDialog.abilityConfirm(position, "confirm-chain", damageRow, ConfirmDialog.apcost, apRow);
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
