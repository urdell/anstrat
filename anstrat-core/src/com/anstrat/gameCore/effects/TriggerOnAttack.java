package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public interface TriggerOnAttack {

	/**
	 * Triggered when a unit with the effect attacks a target
	 * Logic is applied after damage calculations
	 * @param source the unit that has the effect
	 * @param target the target of the attack.
	 */
	public void triggerOnAttack(Unit source, Unit target);
	
}
