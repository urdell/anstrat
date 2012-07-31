package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.geography.TerrainType;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GTile;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class FreezeAnimation extends Animation {
	private GTile target;
	private boolean started = false;
	private float timePassed = 0;
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	
	public FreezeAnimation(TileCoordinate target, int damage) {
		animation = Assets.getAnimation("speedup");
				
		this.target = GEngine.getInstance().getMap().getTile(target);
		length = animation.animationDuration;
		lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		// Run once
		if(!started){
			started = true;	
		}
		
		if(lifetimeLeft <= 0f){
			if (target.tile.terrain.equals(TerrainType.HILL) || target.tile.terrain.equals(TerrainType.ROCKYHILL)) {
				target.tile.terrain = TerrainType.SNOWHILL;
				target.setTexture(TerrainType.SNOWHILL);
			}
			else if (target.tile.terrain.equals(TerrainType.MOUNTAIN)) {
				target.tile.terrain = TerrainType.SNOWMOUNTAIN;
				target.setTexture(TerrainType.SNOWMOUNTAIN);
			}
			else if (target.tile.terrain.equals(TerrainType.FOREST) || target.tile.terrain.equals(TerrainType.SNOWFOREST)) {
				target.tile.terrain = TerrainType.SNOWFOREST;
				target.setTexture(TerrainType.SNOWFOREST);
			}
			else if (target.tile.terrain.equals(TerrainType.FIELD) || target.tile.terrain.equals(TerrainType.ROCKYGROUND)) {
				target.tile.terrain = TerrainType.SNOW;
				target.setTexture(TerrainType.SNOW);
			}
			else if (target.tile.terrain.equals(TerrainType.DEEP_WATER) || target.tile.terrain.equals(TerrainType.SHALLOW_WATER)) {
				target.tile.terrain = TerrainType.SNOW;
				target.setTexture(TerrainType.SNOW);
			}
		}
		timePassed += deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		
		GMap map = GEngine.getInstance().map;
		Vector2 position = target.getCenter();
		
		float width = map.TILE_WIDTH;
		float height = map.TILE_HEIGHT;
		
		TextureRegion region = this.animation.getKeyFrame(timePassed, false);
		batch.draw(region, position.x-(width/2), position.y-(height/2), width, height);
	}
	

}
