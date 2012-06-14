package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public interface TriggerOnKill {

	
	/**
	 * Triggered when a unit with the effect kills a target
	 * @param source the unit that has the effect
	 * @param killedUnit the unit that got killed.
	 */
	public void triggerOnKill(Unit source, Unit killedUnit);
}
