package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.AttackAnimation;
import com.anstrat.animation.BloodAnimation;
import com.anstrat.animation.ChainingAxeAnimation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.HealAnimation;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;

public class ChainingAxe extends TargetedAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 3;
	private static final int RANGE = 2;

	
	public ChainingAxe(){
		super("Chaining Axe","Throws a magic axe jumping between up to 4 adjacent enemies, damage is reduced by for each succesive hit",AP_COST, RANGE);
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
		System.out.println("first unit " + targetUnit.tileCoordinate + " : " + targetUnit.currentHP);
		
		Animation chainingAxeAttackAnimation = new ChainingAxeAnimation(source,targetUnit,source.getAttack(), firstUnit);
		GEngine.getInstance().animationHandler.enqueue(chainingAxeAttackAnimation);
		firstUnit = false;
		
		while(numberOfHits<4 && hadTarget){
			
			adjacentTiles = State.activeState.map.getNeighbors(lastHitTile);
			
			for (Tile adjacentTile : adjacentTiles){
				Unit unit = StateUtils.getUnitByTile(adjacentTile.coordinates);
				if (unit != null){
					if (unit.ownerId != source.ownerId && unit.currentHP > 0 && !alreadyHit.contains(unit)){
						System.out.println(unit.tileCoordinate + " : " + unit.currentHP);
						unit.currentHP -= (source.getAttack()-numberOfHits);
						System.out.println(unit.tileCoordinate + " : " + unit.currentHP);
						Animation chainingAxeAnimation = new ChainingAxeAnimation(lastHit, unit, (source.getAttack()-numberOfHits), firstUnit);
						GEngine.getInstance().animationHandler.enqueue(chainingAxeAnimation);
						// TODO Update healthbar, flyging axe, floating text animation & impactAnimation
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
		
		/*for (Unit deadUnit : deadUnits){
			System.out.println(deadUnit.tileCoordinate);
			Animation Deathanimation = new DeathAnimation(deadUnit, deadUnit.tileCoordinate);
			GEngine.getInstance().animationHandler.enqueue(Deathanimation);
		}*/
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
