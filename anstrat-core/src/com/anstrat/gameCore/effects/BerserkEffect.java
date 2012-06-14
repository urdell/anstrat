package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;

public class BerserkEffect extends Effect implements AffectsAttack, TriggerOnAttack{

	private static final long serialVersionUID = 1L;

	private int attackIncrease = 7;
	
	public BerserkEffect(){
		this.name = "Berserk";
		this.iconName = "sword";
		this.description = "This unit does "+attackIncrease+" extra damage for the next attack.";
	}
	
	@Override
	public int attackIncrease(Unit unit) {
		return attackIncrease; 
	}

	@Override
	public void triggerOnAttack(Unit source, Unit target) {

		sheduledRemove = true;

		GEngine.getInstance().getUnit(source).animationSpeed = 1f;
	}
}
