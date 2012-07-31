package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public interface TriggerOnTurnEnd {
	
	/**
	 * Must not resolve unit deaths
	 * @param u
	 */
	public void triggerOnTurnEnd(Unit u);

}
