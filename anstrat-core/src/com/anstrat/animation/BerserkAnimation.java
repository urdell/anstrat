package com.anstrat.animation;

import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BerserkAnimation extends Animation{

	boolean started = false;
	GUnit unit;
	
	public BerserkAnimation(Unit u){
		length = 0.5f;
		lifetimeLeft = length;
		GEngine ge = GEngine.getInstance();
		unit = ge.getUnit(u);
	}
	
	@Override
	public void run(float deltaTime) {
		if(!started){
			started = true;
			unit.updateHealthbar();
		}
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
	}
}
