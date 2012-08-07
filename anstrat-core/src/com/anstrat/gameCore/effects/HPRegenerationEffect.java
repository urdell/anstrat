package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public class HPRegenerationEffect extends Effect implements TriggerOnTurnStart{
	private static final long serialVersionUID = 1L;

	public HPRegenerationEffect(){
		this.name = "HPRegeneration";
		this.iconName = "sword";
		this.description = "This unit does heal "+ healAmount +"HP at the start of it's turn.";
	}
	private int healAmount = 2;
	
	@Override
	public void triggerOnTurnStart(Unit u) {
		u.currentHP += healAmount;
		if(u.currentHP > u.getMaxHP()){
			u.currentHP = u.getMaxHP();
		}
	}

}
