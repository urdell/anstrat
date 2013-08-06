package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.State;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FloatingBackground {

	public TextureRegion background = Assets.getTextureRegion("background"); //runeBackground
	public OrthographicCamera camera;
	
	public FloatingBackground(OrthographicCamera camera){
		this.camera = camera;
	}
	
	public void draw(SpriteBatch batch){
		//background removed atm
		/*
		if(background != null){
			batch.setColor(Color.toFloatBits(0.2f, 0.2f, 0.2f, 1f));
			//if (State.activeState.map)
			int size = Math.max(State.activeState.map.getXSize(), State.activeState.map.getYSize());
			double multiplier = size/16;
			batch.draw(background, 0.4f*camera.position.x - 500, 0.4f*camera.position.y-500, (int)(1800d*multiplier), (int)(1800d*multiplier));
			//batch.draw(Assets.WHITE, 0.4f*camera.position.x - 500, 0.4f*camera.position.y-500, 1800, 1800);
			batch.setColor(Color.WHITE);
		}
		*/
	}
	
}
