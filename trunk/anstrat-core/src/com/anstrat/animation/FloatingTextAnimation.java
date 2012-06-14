package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FloatingTextAnimation extends Animation{

	float x, y;
	String text;
	Color color;
	float width = -1;
	
	public FloatingTextAnimation(TileCoordinate origin, String text, Color color){
		Vector2 pos = GEngine.getInstance().map.getTile(origin).getPosition();
		x = pos.x+GMap.TILE_WIDTH/2;
		y = pos.y+GMap.TILE_HEIGHT/2;
		length = 0;
		lifetimeLeft = 1.5f;
		this.text = text;
		this.color = color;
	}
	
	@Override
	public void run(float deltaTime) {
		y = y-40*deltaTime;
		
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		Assets.STANDARD_FONT.setScale(1.3f,1.3f);
		if(width == -1){
			width = Assets.STANDARD_FONT.getBounds(text).width;
		}
		Assets.STANDARD_FONT.setColor(color);
		Assets.STANDARD_FONT.draw(batch, text, x-width/2, y);
	}

}
