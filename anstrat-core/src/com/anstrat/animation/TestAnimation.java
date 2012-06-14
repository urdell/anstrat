package com.anstrat.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TestAnimation extends Animation {
	
	Sprite androidRobot = new Sprite(new Texture(Gdx.files.internal("android-texture.png")));

	
	
	public TestAnimation(){
		androidRobot.setScale(0.1f);
		length = 3;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		androidRobot.rotate(deltaTime*90);
		
	}
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		androidRobot.draw(batch);
	}

}
