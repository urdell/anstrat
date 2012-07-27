package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.AttackAnimation;
import com.anstrat.animation.BloodAnimation;
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
		super("Chaining Axe","Throws an axe jumping between adjacent enemies up to 4 times",AP_COST, RANGE);
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
		TileCoordinate lastHit = coordinate;
		List<Tile> adjacentTiles = new ArrayList<Tile>();
		List<Unit> deadUnits = new ArrayList<Unit>();
		boolean hadTarget = true;
		
		CombatLog cl = new CombatLog();
		cl.attacker = source;
		cl.defender = targetUnit;
		cl.newAttackerAP = source.currentAP;
		cl.newDefenderHP = targetUnit.currentHP;
		cl.attackDamage = source.getAttack();
		Animation attackAnimation = new AttackAnimation(cl);
		GEngine.getInstance().animationHandler.enqueue(attackAnimation);
		
		
		while(numberOfHits<4 && hadTarget){
			
			adjacentTiles = State.activeState.map.getNeighbors(lastHit);
			
			for (Tile adjacentTile : adjacentTiles){
				Unit unit = StateUtils.getUnitByTile(adjacentTile.coordinates);
				if (unit != null){
					if (unit.ownerId != source.ownerId){
						unit.currentHP -= source.getAttack()-numberOfHits;
						
						
						
						boolean directionLeft = lastHit.x > unit.tileCoordinate.x;
						Animation bloodAnimation = new BloodAnimation(GEngine.getInstance().getUnit(unit), directionLeft);
						GEngine.getInstance().animationHandler.enqueue(bloodAnimation);
						// TODO Update healthbar, flyging axe, floating text animation & impactAnimation
						lastHit = unit.tileCoordinate;
						if(unit.resolveDeath()){
							deadUnits.add(unit);
						}
						hadTarget = true;
						numberOfHits++;
						break;
					}
					else{
						hadTarget = false;
					}
				}
			}
		}
		
		for (Unit deadUnit : deadUnits){
			System.out.println(deadUnit.tileCoordinate);
			Animation Deathanimation = new DeathAnimation(deadUnit, deadUnit.tileCoordinate);
			GEngine.getInstance().animationHandler.enqueue(Deathanimation);
		}
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
