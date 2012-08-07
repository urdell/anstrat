package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public class HelsCurseEffect extends Effect implements TriggerOnTurnStart{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int damage;
	
	public HelsCurseEffect(int damage){
		this.name = "Poison";
		this.iconName = "sword";
		this.damage = damage;
	}
	
	@Override
	public void triggerOnTurnStart(Unit u) {
		u.currentHP -= damage;
	}
}
