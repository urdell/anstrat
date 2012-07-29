package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FullscreenTextAnimation extends Animation {

	private float x, y;
	private float speed = 0;
	private String reason;
	private float width;
	private float size = 2.0f;
	
	public FullscreenTextAnimation(String reason){
		length = 3;
		lifetimeLeft = length;
		x=Gdx.graphics.getWidth() / 2f;
		y=Gdx.graphics.getHeight() / 2f;
		this.reason = reason;
		Assets.MENU_FONT.setScale(size);
		width = Assets.MENU_FONT.getBounds(reason).width;
		Assets.MENU_FONT.setScale(1);
	}
	
	@Override
	public void run(float deltaTime) {
		speed += deltaTime*Gdx.graphics.getWidth()/100;
		y = y + speed;
		//x = x-deltaTime*Gdx.graphics.getWidth();
		
	}
	
	@Override
	public void drawFixed(float deltaTime, SpriteBatch batch){
		super.drawFixed(deltaTime, batch);
		Assets.MENU_FONT.setColor(Color.RED);
		Assets.MENU_FONT.setScale(size);
		Assets.MENU_FONT.draw(batch, reason, x-width/2, y);
		Assets.MENU_FONT.setScale(1);
	}
}
