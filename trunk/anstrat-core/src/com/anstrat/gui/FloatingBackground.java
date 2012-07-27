package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FloatingBackground {

	public TextureRegion background = Assets.getTextureRegion("runeBackground");
	public OrthographicCamera camera;
	
	public FloatingBackground(OrthographicCamera camera){
		this.camera = camera;
	}
	
	public void draw(SpriteBatch batch){
		
		if(background != null){
			batch.draw(background, 0.4f*camera.position.x - 500, 0.4f*camera.position.y-500, 1800, 1800);
		}
		
	}
	
}
