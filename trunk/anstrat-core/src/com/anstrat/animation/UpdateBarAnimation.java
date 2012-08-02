package com.anstrat.animation;

import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;

public class UpdateBarAnimation extends Animation {
	
	GUnit updatedUnit;

	
	public UpdateBarAnimation(GUnit unit){
		updatedUnit = unit;
		length = 0f;
		lifetimeLeft = 0f;
	}
	public UpdateBarAnimation(Unit unit){
		this(GEngine.getInstance().getUnit(unit));
	}
	
	@Override
	public void run(float deltaTime) {
		updatedUnit.updateHealthbar();
		
	}

}
