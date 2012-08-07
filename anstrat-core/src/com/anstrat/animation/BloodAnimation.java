package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class BloodAnimation extends Animation {

	private Vector2 position;
	private GUnit defender;
	private boolean left;
	private float timePassed = 0;
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	
	public BloodAnimation(GUnit defender, boolean directionLeft)
	{
		animation = Assets.getAnimation("blood-big");
		position = defender.getPosition();
		this.left = directionLeft;
		this.defender = defender;
		length = animation.animationDuration;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		timePassed += deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch)
	{
		super.draw(deltaTime, batch);
		
		GMap map = GEngine.getInstance().map;
		
		float width = map.TILE_WIDTH;
		float height = map.TILE_HEIGHT;

		TextureRegion region = this.animation.getKeyFrame(timePassed, false);
		
		float rw = Math.abs(region.getRegionWidth());
		float rh = Math.abs(region.getRegionHeight());
		
		float scaleFactor = 1.5f;
		
		if(left)
		{
			batch.draw(region, position.x+rw*scaleFactor/2f+width/3f, position.y+rh*scaleFactor/2f, 
					-rw*scaleFactor, -rh*scaleFactor);
		}
		else
		{
			batch.draw(region, position.x-rw*scaleFactor/2f-width/3f, position.y-rh*scaleFactor/2f, 
					rw*scaleFactor, rh*scaleFactor);
		}
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(defender.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}

}
