package com.anstrat.animation;

import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.math.Vector2;

public class DeathAnimation extends Animation {

	private GUnit unit;
	private float timeElapsed;
	private boolean started;
	private final static float moveSpeed = 0.5f;
	private Vector2 direction;
	
	public DeathAnimation(Unit unit, Vector2 direction){
		this.unit = GEngine.getInstance().getUnit(unit);
		timeElapsed = 0;
		lifetimeLeft = 2f;
		this.direction = direction;
	}
	
	@Override
	public void run(float deltaTime) {
		if(!started){
			started = true;
			unit.playDeath();
		}
		
		unit.setPosition(new Vector2(unit.getPosition().x+direction.x*timeElapsed*moveSpeed,
				unit.getPosition().y+direction.y*timeElapsed*moveSpeed));
		timeElapsed += deltaTime;
	}
	
	public void removeGUnit()
	{
		GEngine.getInstance().gUnits.remove(unit.unit.id);
	}
}