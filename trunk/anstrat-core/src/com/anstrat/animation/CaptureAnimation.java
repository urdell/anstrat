package com.anstrat.animation;

import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GBuilding;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GTile;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;

public class CaptureAnimation extends Animation{

	GUnit unit;
	GBuilding building;
	boolean started = false;
	
	public CaptureAnimation(Unit u, Building b){
		length = 1f;;
		lifetimeLeft = length;
		GEngine ge = GEngine.getInstance();
		unit = ge.getUnit(u);
		building = ge.getBuilding(b);
	}
	
	@Override
	public void run(float deltaTime) {
		
		if(!started){
			GEngine ge = GEngine.getInstance();
			ge.updateUI();
			
			// Movetotileanimation
			GTile tile = ge.map.getTile(building.building.tileCoordinate);
			if (!GEngine.getInstance().tileIsOnScreen(tile)) {
				Animation mAnimation = new MoveCameraAnimation(building.getScreenPos());
				ge.animationHandler.runParalell(mAnimation);
			}
			FloatingTextAnimation animation;
			if(unit.unit.ownerId == building.building.controllerId){  // It was captured
				animation = new FloatingTextAnimation(unit.unit.tileCoordinate, "Captured!", Color.RED);
			}
			else{	// Still being captured
				animation = new FloatingTextAnimation(unit.unit.tileCoordinate, "Capturing", Color.ORANGE);
			}
			ge.animationHandler.runParalell(animation);
			
			started = true;
			unit.updateHealthbar();	
		}	
	}
}
