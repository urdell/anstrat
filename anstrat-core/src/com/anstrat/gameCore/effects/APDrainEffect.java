package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public class APDrainEffect extends Effect implements TriggerOnAttack {

	private static final long serialVersionUID = 1L;

	public APDrainEffect(){
		name = "AP drain";
		iconName = "ap";
		this.description = "This unit has increased ap drain when attacking";
	}
	
	@Override
	public void triggerOnAttack(Unit source, Unit target) {
		target.currentAP = Math.max(0, target.currentAP-1); // drain 1 AP
	}
}
