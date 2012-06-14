package com.anstrat.gameCore;


/**
 * 
 * @author Anton
 * Contains any important information about a single attack and all it's consequences.
 */
public class CombatLog {
	
	public Unit attacker = null;
	public Unit defender = null;
	public int newDefenderHP = 0;	
	public int attackDamage = -1;
	public int newAttackerAP = 0;
	
	/**
	 * Needs to be filled manually
	 */
	public CombatLog(){
		
	}
	
	public CombatLog(Unit attacker, Unit defender, int attackDamage, int newDefenderHP, int newAttackerAP){
		this.attacker = attacker;
		this.defender = defender;
		this.attackDamage = attackDamage;
		this.newDefenderHP = newDefenderHP;
		this.newAttackerAP = newAttackerAP;
	}

	
	
	
}
