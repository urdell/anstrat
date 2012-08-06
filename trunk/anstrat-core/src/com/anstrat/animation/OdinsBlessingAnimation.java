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
	private GUnit[] target;
	private boolean started = false;
	private float timePassed = 0;
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	
	public OdinsBlessingAnimation(Unit[] target) {
		animation = Assets.getAnimation("speedup");
		this.target = new GUnit[target.length];
		for(int i = 0; i < target.length; i++) {
			this.target[i] = GEngine.getInstance().getUnit(target[i]);
		}
		length = animation.animationDuration;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		// Run once
		if(!started){
			started = true;	
		}
			
		if(lifetimeLeft <= 0f){
			for(GUnit target : this.target)
				target.updateHealthbar();
		}
		
		timePassed += deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		GMap map = GEngine.getInstance().map;
		TextureRegion region = this.animation.getKeyFrame(timePassed, false);
		float width = map.TILE_WIDTH;
		float height = map.TILE_HEIGHT;
		
		for(GUnit target : this.target) {
			Vector2 position = target.getPosition();
			batch.draw(region, position.x-(width/2), position.y-(height/2), width, height);
		}
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return true;
	}
}
