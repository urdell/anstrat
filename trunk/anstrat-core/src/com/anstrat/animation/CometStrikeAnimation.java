package com.anstrat.animation;

import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TerrainType;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GTile;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CometStrikeAnimation extends Animation {
	private GTile target;
	private boolean started = false;
	private float timePassed = 0;
	private Vector2 start = null;
	private Vector2 end = null;
	
	com.badlogic.gdx.graphics.g2d.Animation animation1 = null;
	com.badlogic.gdx.graphics.g2d.Animation animation2 = null;
	
	public CometStrikeAnimation(TileCoordinate target) {
		animation1 = Assets.getAnimation("meteorstrike");
		animation2 = Assets.getAnimation("meteorimpact");
		
		
				
		this.target = GEngine.getInstance().getMap().getTile(target);
		length = animation1.animationDuration*7+animation2.animationDuration;
		lifetimeLeft = length;
		
		start = this.target.getPosition();
		start.x += 3*GEngine.getInstance().map.TILE_WIDTH*3;
		start.y += 3*GEngine.getInstance().map.TILE_HEIGHT*3;
		
		end = this.target.getPosition();
	}
	
	@Override
	public void run(float deltaTime) {
		// Run once
		if(!started){
			started = true;	
		}
		if(lifetimeLeft <= 0f){
			Unit centerTarget = StateUtils.getUnitByTile(target.tile.coordinates);
			if(centerTarget != null){
				GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(centerTarget));
				State.activeState.unitList.remove(centerTarget.id);
			}
			if (!target.tile.terrain.equals(TerrainType.DEEP_WATER)) {
				target.tile.terrain = TerrainType.CRATER;
				target.tile.coordinates = target.tile.coordinates; 
				target.setTexture(TerrainType.CRATER);
			}
			List<Tile> list = State.activeState.map.getNeighbors(target.tile);
			for (Tile t: list) {
				Unit unit = StateUtils.getUnitByTile(t.coordinates);
				if (unit != null) {
					if(unit.currentHP <= 0){
						GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(unit));
						State.activeState.unitList.remove(unit.id);
					}
					GEngine.getInstance().getUnit(unit).updateHealthbar();
				}
			}
		}
		
		timePassed += deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		float var = (lifetimeLeft-animation2.animationDuration)/(length-animation2.animationDuration);
		
		if (var < 0)
			var = 0;
		
		GMap map = GEngine.getInstance().map;
		Vector2 position = new Vector2();
		position.x = end.x-(end.x-start.x)*var; 
		position.y = end.y+(end.y-start.y)*var;
		
		float width = map.TILE_WIDTH;
		float height = map.TILE_HEIGHT;
		
		
		
		if (lifetimeLeft > animation2.animationDuration){
			TextureRegion region = this.animation1.getKeyFrame(timePassed, true);
			batch.draw(region, position.x, position.y, width, height);
		}
		else {
			TextureRegion region = this.animation2.getKeyFrame(timePassed-animation1.animationDuration*7, false);
			batch.draw(region, position.x-(width/2), position.y-(width/2), width*2, height*2);
		
		}
	}
	

}
