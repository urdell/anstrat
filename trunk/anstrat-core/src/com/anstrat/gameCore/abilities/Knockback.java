package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.AttackAnimation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.MoveAnimation;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TerrainType;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;

public class Knockback extends TargetedAbility {

	
	/**
	 * 
	 */
	private static final int AP_COST = 3;
	private static final int RANGE = 1;
	private static final long serialVersionUID = 1L;
	
	public Knockback() {
		super("Knockback", "Makes an attack knocking the enemy back if possible", AP_COST, RANGE);
		
	}

	public boolean isAllowed(Unit source, TileCoordinate coordinates) {
		Unit targetUnit = StateUtils.getUnitByTile(coordinates);
		
		return super.isAllowed(source, coordinates) 
				&& targetUnit != null
				&& targetUnit.ownerId != source.ownerId;
	}

	@Override
	public void activate(Unit source, TileCoordinate coordinate) {
		boolean moved = false;
		TileCoordinate knockedFrom = coordinate;
		super.activate(source, coordinate);
		
		Unit targetUnit = StateUtils.getUnitByTile(coordinate);
		
		int roll = State.activeState.random.nextInt(6)+1;
		
		targetUnit.currentHP -= source.getAttack()+roll;
		if(targetUnit.resolveDeath() == false){
			TileCoordinate knockbackCoordinate = getKnockBackCoordinate(source, targetUnit);
			moved = knockBack(targetUnit, knockbackCoordinate);
		}
		
		
		CombatLog cl = new CombatLog();
		cl.attacker = source;
		cl.defender = targetUnit;
		cl.newAttackerAP = source.currentAP;
		cl.newDefenderHP = targetUnit.currentHP;
		cl.attackDamage = source.getAttack()+roll;
		Animation attackAnimation = new AttackAnimation(cl);
		GEngine.getInstance().animationHandler.enqueue(attackAnimation);
		
		if(moved){
			Animation moveAnimation = new MoveAnimation(targetUnit,knockedFrom, targetUnit.tileCoordinate);
			GEngine.getInstance().animationHandler.enqueue(moveAnimation);
		}
		
	}
	
	/**
	 * 2 horisontal cases, 4 diagonal cases depending on odd or even y-coordinate, 
	 * 4 unique diagonal cases
	 * @param source
	 * @param targetUnit
	 * @return TileCoordinate to be knockbacked too
	 */

	private TileCoordinate getKnockBackCoordinate(Unit source, Unit targetUnit) {
		TileCoordinate KnockBackCoordinate = new TileCoordinate();
		TileCoordinate att = source.tileCoordinate;
		TileCoordinate def = targetUnit.tileCoordinate;
		
		if(att.y == def.y){
			KnockBackCoordinate.y = def.y;
			if(att.x-def.x==1){
				KnockBackCoordinate.x = def.x-1;
			}
			else{
				KnockBackCoordinate.x = def.x+1;
			}
		}
		
		if(att.x == def.x && att.y-def.y ==1){
			if(def.y%2==0){
				KnockBackCoordinate.x = def.x-1;
			}
			else{
				KnockBackCoordinate.x = def.x+1;
			}
			KnockBackCoordinate.y = def.y-1;
		}
		
		if(att.x==def.x && att.y-def.y==-1){
			if(def.y%2==0){
				KnockBackCoordinate.x = def.x-1;
			}
			else{
				KnockBackCoordinate.x = def.x+1;	
			}
			KnockBackCoordinate.y = def.y+1;
		}
		
		//1
		if(att.x-def.x==1 && att.y-def.y==1){
			KnockBackCoordinate.x = def.x;
			KnockBackCoordinate.y = def.y-1;
		}
		
		//2
		if(att.x-def.x==-1 && att.y-def.y==-1){
			KnockBackCoordinate.x = def.x;
			KnockBackCoordinate.y = def.y+1;
		}
		
		//3
		if(att.x-def.x==1 && att.y-def.y==-1){
			KnockBackCoordinate.x = def.x;
			KnockBackCoordinate.y = def.y+1;
		}
		
		//4
		if(att.x-def.x==-1 && att.y-def.y==1){
			KnockBackCoordinate.x = def.x;
			KnockBackCoordinate.y = def.y-1;
		}	
			
		return KnockBackCoordinate;
	}

	private boolean knockBack(Unit targetUnit, TileCoordinate knockbackCoordinate) {
		if(StateUtils.getUnitByTile(knockbackCoordinate) == null){
			if(State.activeState.map.getTile(knockbackCoordinate).terrain.penalty != Integer.MAX_VALUE){ 
				targetUnit.tileCoordinate = knockbackCoordinate;
				return true;
			}
		}
		return false;
		
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
