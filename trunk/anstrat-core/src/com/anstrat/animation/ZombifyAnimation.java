package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ZombifyAnimation extends Animation {
	private GUnit target, zombie;
	private boolean started = false;
	private float timePassed = 0;
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	
	public ZombifyAnimation(Unit target, Unit newZombie) {
		animation = Assets.getAnimation("zombify");
		
		this.target = GEngine.getInstance().getUnit(target);
		this.zombie = new GUnit(newZombie);
		this.zombie.setAlpha(0f);
		
		
		this.zombie.setPosition(this.target.getPosition());
		length = animation.animationDuration;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		// Run once
		if(!started){
			started = true;	
			
			
		}
		
		float var = (lifetimeLeft-(length/2))/(length/2);
		if(lifetimeLeft < length/2)
			var = 0;
		
		target.setAlpha(var);
		
		if(lifetimeLeft <= 0) {
			GEngine.getInstance().gUnits.remove(target.unit.id);
			GEngine.getInstance().gUnits.put(zombie.unit.id, this.zombie);
			zombie.setAlpha(1f);
		}
		System.out.println("timepassed"+timePassed);
		timePassed += deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		GMap map = GEngine.getInstance().map;
		Vector2 position = target.getPosition();
		
		float width = map.TILE_WIDTH;//*target.scale;
		float height = map.TILE_HEIGHT;//*target.scale;
		
		TextureRegion region = this.animation.getKeyFrame(timePassed, false);

		System.out.println(region.getRegionX());
		batch.draw(region, position.x-(width/2), position.y-(height/2), width, height);
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(zombie.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}
