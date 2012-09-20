package com.anstrat.gameCore.effects;

import com.anstrat.animation.Animation;
import com.anstrat.animation.FloatingTextAnimation;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.graphics.Color;

public class HuginAndMuninEffect extends Effect implements TriggerOnTurnStart, APRegenerationModifier, TriggerOnTurnEnd {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int nrOfRoundsSoFar = 0;
	private int nrOfRoundsTotal = 3;
	private int damage = 1;
	@Override
	public void triggerOnTurnStart(Unit u) {
		Animation animation1 = new FloatingTextAnimation(u.tileCoordinate, ""+damage, Color.RED);
		GEngine.getInstance().animationHandler.enqueue(animation1);
		u.currentHP -= damage;
	}

	@Override
	public int APRegModication() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public void triggerOnTurnEnd(Unit u) {
		
		if (nrOfRoundsTotal <= nrOfRoundsSoFar ){
			sheduledRemove = true;
		}
		else{
			nrOfRoundsSoFar++;
		}
		
	}

}
