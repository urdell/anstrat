package com.anstrat.animation;

import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.FancyNumbers;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class FloatingNumberAnimation extends Animation {

	float x, y;
	int number;
	Color color;
	//float width = -1;
	float size = 1;
	private TileCoordinate origin;
	
	public FloatingNumberAnimation(TileCoordinate origin, int number, float size, Color color){
		GMap map = GEngine.getInstance().map;
		this.origin = origin;
		Vector2 pos = GEngine.getInstance().map.getTile(origin).getPosition();
		x = pos.x + map.TILE_WIDTH / 4f;
		y = pos.y + map.TILE_HEIGHT / 2f;
		length = 1.5f;
		lifetimeLeft = 1.5f;
		this.number = number;
		this.color = color;
		this.size = size;
	}
	
	@Override
	public void run(float deltaTime) {
		y = y - 40f * deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		//Assets.STANDARD_FONT.setScale(1.3f,1.3f);
		/*if(width == -1){
			width = Assets.STANDARD_FONT.getBounds(text).width;
		}*/
		//Assets.STANDARD_FONT.setColor(color);
		Color color2 = new Color(color);
		if (lifetimeLeft < 0.5f)
			color2.a = lifetimeLeft/(length*0.5f);
		FancyNumbers.drawNumber(number, x, y, size, true, color2, batch);
		//Assets.STANDARD_FONT.draw(batch, text, x-width/2, y);
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(origin,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}
