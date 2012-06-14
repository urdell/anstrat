package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public interface TriggerOnDefend {
	
	/**
	 * 
	 * @param defender the unit with the effect
	 * @param attacker the attacking unit.
	 */
	public void triggerOnDefend(Unit defender, Unit attacker);

}
