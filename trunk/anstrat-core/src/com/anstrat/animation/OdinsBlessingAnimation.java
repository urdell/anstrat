package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class OdinsBlessingAnimation extends Animation {
	private OdinsBlessing2Animation[] animations;
	private boolean started = false;
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	
	public OdinsBlessingAnimation(Unit[] target) {
		animation = Assets.getAnimation("speedup");
		this.animations = new OdinsBlessing2Animation[target.length];
		for(int i = 0; i < target.length; i++) {
			this.animations[i] = new OdinsBlessing2Animation(target[i]);
		}
		length = animation.animationDuration;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		// Run once
		if(!started){
			for(Animation animation : animations) {
				GEngine.getInstance().animationHandler.runParalell(animation);
			}
			started = true;	
		}
			
		if(lifetimeLeft <= 0f){
		}
		
	}
	

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return true;
	}
}
