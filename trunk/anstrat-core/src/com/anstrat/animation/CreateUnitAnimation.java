package com.anstrat.animation;

import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;

public class CreateUnitAnimation extends Animation {
	
	private GUnit unit;
	private boolean started = false;
	private float finalAlpha;
	
	public CreateUnitAnimation(GUnit unit) {
		this.unit = unit;
		unit.setAlpha(0);
		length = 1.0f;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		if(!started){
			started = true;
			finalAlpha = 1;
			GEngine.getInstance().updateUI();
			Animation animation = new MoveCameraAnimation(unit.getPosition());
			GEngine.getInstance().animationHandler.runParalell(animation);
		}
		
		unit.setAlpha(finalAlpha*(1-Math.max(lifetimeLeft, 0)));
	}
}
