package com.anstrat.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class GenericVisualAnimation extends Animation{
	
	com.badlogic.gdx.graphics.g2d.Animation gdxAnimation;
	Vector2 position;
	int size;

	public GenericVisualAnimation( com.badlogic.gdx.graphics.g2d.Animation gdxAnimation, Vector2 position, int size ){
		this.gdxAnimation = gdxAnimation;
		this.position = position;
		this.size = size;
		
		length = gdxAnimation.animationDuration;
		lifetimeLeft = length;
		
	}
	
	@Override
	public void run(float deltaTime) {
		
		
	}

	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		TextureRegion region = gdxAnimation.getKeyFrame(length - lifetimeLeft, false);
		batch.draw(region, position.x-size/2, position.y-size/2, size, size);
			
	}
	
}
