package com.anstrat.animation;

import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.math.Vector2;

public class DeathAnimation extends Animation {

	private GUnit unit;
	private int state;	// Animation state, one of JUMP_UP, JUMP_DOWN, FALLING or SINK_AND_FADE
	private float timeLeft;
	private boolean started;
	private float jumpUpStep, jumpDownStep, sinkStep, fadeStep;
	
	public DeathAnimation(Unit unit){
		this.unit = GEngine.getInstance().getUnit(unit);
		this.state = JUMP_UP;
		
		// Set life time and start duration
		for(float d : DURATIONS) this.lifetimeLeft += d;
		timeLeft = DURATIONS[0];
		
		jumpUpStep = 15 / DURATIONS[JUMP_UP];
		jumpDownStep = 15 / DURATIONS[JUMP_DOWN];
		sinkStep = 30 / DURATIONS[SINK_AND_FADE];
		fadeStep = 1 / DURATIONS[SINK_AND_FADE];
	}
	
	private static final int JUMP_UP = 0;
	private static final int JUMP_DOWN = 1;
	private static final int SINK_AND_FADE = 2;
	
	// Duration of each animation state
	private static final float[] DURATIONS = new float[]{0.1f, 0.1f, 3.5f};
	
	@Override
	public void run(float deltaTime) {
		if(!started){
			started = true;
			unit.playDeath();
		}
		
		if(state >= DURATIONS.length){
			return;
		}
		
		timeLeft -= deltaTime;
		
		// Check if we're done with the current state
		if(timeLeft < 0f){
			state++;
			
			// Set new duration
			if(state < DURATIONS.length){
				timeLeft = DURATIONS[state];
			}
		}
		
		switch(state){
			case JUMP_UP:{
				Vector2 position = unit.getPosition();
				position.y -= jumpUpStep * deltaTime;
				unit.setPosition(position);
				break;
			}
			case JUMP_DOWN: {
				Vector2 position = unit.getPosition();
				position.y += jumpDownStep * deltaTime;
				unit.setPosition(position);
				break;
			}
			case SINK_AND_FADE: {
				
				float alpha = unit.getAlpha() - fadeStep * deltaTime;
				if(alpha < 0) alpha = 0;
				
				unit.setAlpha(alpha);
				
				Vector2 position = unit.getPosition();
				position.y += sinkStep * deltaTime;
				unit.setPosition(position);
				break;
			}
		}
	}
	
	public void removeGUnit()
	{
		GEngine.getInstance().gUnits.remove(unit.unit.id);
	}
}
