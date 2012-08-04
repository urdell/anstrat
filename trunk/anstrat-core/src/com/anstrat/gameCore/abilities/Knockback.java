package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.AttackAnimation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.KnockbackAnimation;
import com.anstrat.animation.MoveAnimation;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TerrainType;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.DamageRow;
import com.anstrat.gui.confirmDialog.TextRow;

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
		boolean canMove = false;
		TileCoordinate knockedFrom = coordinate;
		super.activate(source, coordinate);
		
		Unit targetUnit = StateUtils.getUnitByTile(coordinate);
		
		int roll = State.activeState.random.nextInt(6)+1;
		
		targetUnit.currentHP -= source.getAttack()+roll;
		
		
		
		CombatLog cl = new CombatLog();
		cl.attacker = source;
		cl.defender = targetUnit;
		cl.newAttackerAP = source.currentAP;
		cl.newDefenderHP = targetUnit.currentHP;
		cl.attackDamage = source.getAttack()+roll;
		targetUnit.resolveDeath();
		if(targetUnit.isAlive){
			TileCoordinate knockbackCoordinate = getKnockBackCoordinate(source, targetUnit);
			canMove = knockBack(targetUnit, knockbackCoordinate);
		}
		System.out.println("canMove: " + canMove);
		if(canMove){
			System.out.println("Knockbackanimation");
			Animation knockbackAnimation = new KnockbackAnimation(cl);
			GEngine.getInstance().animationHandler.enqueue(knockbackAnimation);
			
		}
		if(!canMove){
			System.out.println("AttackAnimation");
			Animation attackAnimation = new AttackAnimation(cl);
			GEngine.getInstance().animationHandler.enqueue(attackAnimation);
		}
		
	}
	
	/**
	 * 2 horisontal cases, 4 diagonal cases depending on odd or even y-coordinate, 
	 * 4 unique diagonal cases
	 * @param source
	 * @param targetUnit
	 * @return TileCoordinate to be knockbacked too
	 */

	public static TileCoordinate getKnockBackCoordinate(Unit source, Unit targetUnit) {
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
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(source.getAttack()+1, source.getAttack()+6);
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
