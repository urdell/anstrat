package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class UberTextAnimation extends Animation {
	
	private TileCoordinate tc;
	private TextureRegion tr;
	private float x, y;
	private float alpha, timePasd, bugfix;
	
	public UberTextAnimation(TileCoordinate start, String textname){
		this.tc = start;
		this.tr = Assets.getTextureRegion(textname);
		Vector2 pos = GEngine.getInstance().map.getTile(start).getPosition();
		GMap map = GEngine.getInstance().map;
		x = pos.x + map.TILE_WIDTH / 2f;
		y = pos.y + map.TILE_HEIGHT / 2f;
		length = 0f;
		lifetimeLeft = 2.5f;
		bugfix = lifetimeLeft;
		alpha = 1f;
		timePasd = 0f;
	}
	
	public UberTextAnimation(TileCoordinate start, float xOffset, float yOffset, String textname){
		this.tc = start;
		this.tr = Assets.getTextureRegion(textname);
		Vector2 pos = GEngine.getInstance().map.getTile(start).getPosition();
		GMap map = GEngine.getInstance().map;
		x = pos.x + map.TILE_WIDTH / 2f;
		y = pos.y + map.TILE_HEIGHT / 2f;
		x = x+xOffset;
		y = y+yOffset;
		length = 0f;
		lifetimeLeft = 2.5f;
		bugfix = lifetimeLeft;
		alpha = 1f;
		timePasd = 0f;
	}
	
	
	@Override
	public void run(float delta){
		timePasd += delta;
		y = y - 40f * delta;
		if(timePasd > bugfix/2f)
			alpha = lifetimeLeft*2f/bugfix;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		//super.draw(deltaTime, batch);
		Color col = batch.getColor();
		batch.setColor(1f, 1f, 1f, alpha);
		batch.draw(tr, x-tr.getRegionWidth()/2, y-tr.getRegionHeight()/2, 
				tr.getRegionWidth(), -tr.getRegionHeight());
		batch.setColor(col);
	}
	
	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(tc,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}