package com.anstrat.animation;

import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.math.Vector2;

public class TeleportAnimation extends Animation {
	
	private GUnit t1, t2;
	private boolean moved = false;
	public TeleportAnimation(Unit t1, Unit t2) {
		
		this.t1 = GEngine.getInstance().getUnit(t1);;
		this.t2 = GEngine.getInstance().getUnit(t2);;
		length = 1;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		if (lifetimeLeft > length/2) {
			float var = (lifetimeLeft-(length/2))/(length/2);
			if(var<0f)
				var = 0f;
			t1.setAlpha(var);
			t2.setAlpha(var);
		}
		else {
			if(!moved) {
				Vector2 temp = t1.getPosition();
				t1.setPosition(t2.getPosition());
				t2.setPosition(temp);
				moved = true;
			}
			float var = 1-lifetimeLeft/(length/2);
			System.out.println("var"+var);
			if(var>1f)
				var = 1f;
			t1.setAlpha(var);
			t2.setAlpha(var);
		}
		
	}
	

}
