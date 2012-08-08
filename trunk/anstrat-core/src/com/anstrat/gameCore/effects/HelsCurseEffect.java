package com.anstrat.gameCore.effects;

import com.anstrat.animation.Animation;
import com.anstrat.animation.FloatingTextAnimation;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.graphics.Color;

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
		Animation animation1 = new FloatingTextAnimation(u.tileCoordinate, "6", Color.RED);
		GEngine.getInstance().animationHandler.enqueue(animation1);
		u.currentHP -= damage;
	}
}
