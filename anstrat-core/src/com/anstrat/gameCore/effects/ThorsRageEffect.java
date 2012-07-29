package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public class ThorsRageEffect extends Effect implements AffectsAttack {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int damageIncrease = 5;
	
	public ThorsRageEffect() {
		this.name = "Thor's rage";
		this.description = "This unit is enraged and has increased damage";
		this.iconName = "sword";
	}
	
	@Override
	public int attackIncrease(Unit unit) {
		
		return damageIncrease;
	}
	
}
