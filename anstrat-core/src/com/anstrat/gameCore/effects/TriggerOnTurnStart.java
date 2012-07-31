package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public interface TriggerOnTurnStart {
	
	/**
	 * Must not resolve unit deaths
	 * @param u
	 */
	public void triggerOnTurnStart(Unit u);


}
