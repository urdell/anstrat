package com.anstrat.gameCore;

import com.anstrat.animation.AttackAnimation;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.gameCore.effects.TriggerOnAttack;
import com.anstrat.gameCore.effects.TriggerOnDefend;
import com.anstrat.gameCore.effects.TriggerOnKill;
import com.anstrat.geography.Pathfinding;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class Combat {
	
	private static double randomness = 0.25;
	/**
	 * Calculates all AP, damage, dying etc.
	 * @param attacker
	 * @param defender
	 */
	public static void battle(Unit attacker, Unit defender){
		
		attacker.currentAP -= attacker.getAPCostAttack();
		
		for(Effect effectDefend : defender.effects){
			if(effectDefend instanceof TriggerOnDefend){
				((TriggerOnDefend) effectDefend).triggerOnDefend(defender, attacker);
			}
		}
		
		int minDamage = minDamage(attacker, defender);
		int maxDamage = maxDamage(attacker, defender);
		int damage = State.activeState.random.nextInt( maxDamage-minDamage+1 ) + minDamage; // +1 because random is exclusive
		
		Gdx.app.log("Combat", "Damage is "+damage);
		
		defender.currentHP -= damage;
		
		if(defender.currentHP <= 0){
			// GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(defender));
			State.activeState.unitList.remove(defender.id);
			for(Effect effectKill : attacker.effects){
				if(effectKill instanceof TriggerOnKill){
					((TriggerOnKill) effectKill).triggerOnKill(attacker, defender);
				}
			}
		}
		attacker.attacksThisTurn++;
		
		for(Effect effectAttack : attacker.effects){
			if(effectAttack instanceof TriggerOnAttack){
				((TriggerOnAttack) effectAttack).triggerOnAttack(attacker, defender);
			}
		}
		CombatLog combatLog = new CombatLog(attacker, defender, damage, defender.currentHP, attacker.currentAP);

		AttackAnimation animation = new AttackAnimation(combatLog);
		GEngine.getInstance().animationHandler.enqueue(animation);
		
		Gdx.app.log("Combat", "New health is "+defender.currentHP);
		
	}
	
	/**
	 * 
	 * @param attacker
	 * @param defender
	 * @return 
	 */
	public static boolean canAttack(Unit attacker, Unit defender){
		
		// Check range
		int range = Pathfinding.getDistance(attacker.tileCoordinate, defender.tileCoordinate);
		if(range > attacker.getMaxAttackRange() || range < attacker.getMinAttackRange()){
			return false;
		}
		
		// Check that attacker has enough AP
		return attacker.currentAP >= attacker.getAPCostAttack();
	}
	
	public static int minDamage(Unit attacker, Unit defender){
		float typeModifier = (float) UnitType.getAttackModifier(attacker.getUnitType(), defender.getUnitType());
		int damage = (int) (attacker.getAttack()*typeModifier*(1-randomness));
		if(damage < 0) { damage = 0; } // Remove negative damage
		return damage;
	}
	public static int maxDamage(Unit attacker, Unit defender){		
		float typeModifier = (float) UnitType.getAttackModifier(attacker.getUnitType(), defender.getUnitType());
		int damage = (int) (attacker.getAttack()*typeModifier*(1+randomness));
		if(damage < 0) { damage = 0; } // Remove negative damage
		return damage;
	}
}
