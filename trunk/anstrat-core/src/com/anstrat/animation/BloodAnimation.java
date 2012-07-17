package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class BloodAnimation extends Animation {

	private Vector2 position;
	private boolean left;
	private float timePassed = 0;
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	
	public BloodAnimation(GUnit attacker, GUnit defender)
	{
		Gdx.app.log("Blood", "splatter ok");
		animation = Assets.getAnimation("blood-big");
		position = defender.getPosition();
		left = attacker.getPosition().x > position.x;
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
		Gdx.app.log("Blood", "splatter ok2");
		super.draw(deltaTime, batch);
		
		GMap map = GEngine.getInstance().map;
		
		float width = map.TILE_WIDTH;
		float height = map.TILE_HEIGHT;
		
		TextureRegion region = this.animation.getKeyFrame(deltaTime, false);
		batch.draw(region, position.x-(width/2), position.y-(height/2), 0, 0, 
				region.getRegionWidth(), region.getRegionHeight(), 100f, 100f, 0, false);
	}

}
