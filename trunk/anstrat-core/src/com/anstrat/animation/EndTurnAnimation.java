package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EndTurnAnimation extends Animation {

	private float x, y;
	private boolean started = false;
	private Color tintColor = Color.WHITE.cpy();
	
	public EndTurnAnimation(){
		length = 3;
		lifetimeLeft = 3.5f;
		x=Gdx.graphics.getWidth();
		y=60 * Main.percentHeight;
	}
	
	@Override
	public void run(float deltaTime) {
		//y = y + deltaTime*150;
		x = x-deltaTime*Gdx.graphics.getWidth()/2;
		
		if(!started){
			for(GUnit gUnit : GEngine.getInstance().gUnits.values()){
				gUnit.updateHealthbar();
			}
			GEngine.getInstance().updateUI();
			GEngine.getInstance().userInterface.updateCurrentPlayer();
			started = true;
		}
	}
	
	@Override
	public void drawFixed(float deltaTime, SpriteBatch batch){
		super.drawFixed(deltaTime, batch);
		
		TextureRegion background = Assets.getTextureRegion("EndTurnGradient");
		tintColor.a = 1-Math.max(0, Math.max(lifetimeLeft-2.9f, 0.6f-lifetimeLeft))/0.6f; // Fades 0.6 seconds before end and after start
		batch.setColor(tintColor);
		batch.draw(background, 0, y-16*Main.percentHeight, Gdx.graphics.getWidth(), 25*Main.percentHeight);
		batch.setColor(Color.WHITE);
		
		
		
		float scale =Assets.MENU_FONT.getScaleX();
		Assets.MENU_FONT.setScale(2.5f);
		Assets.MENU_FONT.setColor(Color.WHITE);
		Assets.MENU_FONT.draw(batch, "Turn ended!", x, y);
		Assets.MENU_FONT.setScale(scale);
	}

}
