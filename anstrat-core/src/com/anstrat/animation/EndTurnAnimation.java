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
		length = 1.5f;
		lifetimeLeft = 2f;
		x=Gdx.graphics.getWidth();
		y=60 * Main.percentHeight;
	}
	
	@Override
	public void run(float deltaTime) {
		//y = y + deltaTime*150;
		//x = x-deltaTime*Gdx.graphics.getWidth()*1.0f;
		
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
		tintColor.a = 1-Math.max(0, Math.max(lifetimeLeft-1.6f, 0.4f-lifetimeLeft))/0.4f; // Fades 0.4 seconds before end and after start
		batch.setColor(tintColor);
		//batch.draw(background, 0, y-16*Main.percentHeight, Gdx.graphics.getWidth(), 25*Main.percentHeight);
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch.setColor(Color.WHITE);
		
		String text = GEngine.getInstance().state.players[GEngine.getInstance().state.currentPlayerId].getDisplayName();
		
		float scale =Assets.MENU_FONT.getScaleX();
		Assets.MENU_FONT.setScale(1.25f);
		Assets.MENU_FONT.setColor(tintColor);
		float width = Assets.MENU_FONT.getBounds(text).width;
		Assets.MENU_FONT.draw(batch, text, Gdx.graphics.getWidth()/2f-width/2f, Gdx.graphics.getHeight()/2f);
		Assets.MENU_FONT.setScale(scale);
		Assets.MENU_FONT.setColor(Color.WHITE);
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return true;
	}
}