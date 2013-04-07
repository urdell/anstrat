package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;

public class ShieldWallAnimation extends Animation {

	GUnit unit;
	com.badlogic.gdx.graphics.g2d.Animation prepare = null;
	boolean started = false;
	
	public ShieldWallAnimation(Unit source){
		this.unit = GEngine.getInstance().getUnit(source);
		prepare = Assets.getAnimation("swordsman-prepare");
		length = prepare.animationDuration;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		if(!started){
			started = true;
			unit.playCustom(Assets.getAnimation("swordsman-prepare"), false);
			unit.updateHealthbar();
		}
		
		if(lifetimeLeft < 0){
			unit.playCustom(Assets.getAnimation("swordsman-ability"), true);
		}
	}

	@Override
	public boolean isVisible() {
		return Fog.isVisible(unit.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}	
}