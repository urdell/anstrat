package com.anstrat.gameCore.effects;

import com.anstrat.animation.DeathAnimation;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.math.Vector2;

public class HelsCurseEffect extends Effect implements TriggerOnTurnStart{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int damage;
	
	public HelsCurseEffect(int damage){
		this.name = "Poison";
		this.iconName = "sword";
		this.damage = damage;
	}
	
	@Override
	public void triggerOnTurnStart(Unit u) {
		u.currentHP -= damage;
		if(u.currentHP <= 0){
			GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(u, GEngine.getInstance().getUnit(u).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
			State.activeState.unitList.remove(u.id);
		}
	}
}
