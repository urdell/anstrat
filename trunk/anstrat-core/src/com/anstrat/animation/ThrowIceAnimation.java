package com.anstrat.animation;

import java.util.Map;
import java.util.Set;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TerrainType;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GTile;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ThrowIceAnimation extends Animation {

	private GTile target;
	private boolean started = false, started2 = false;
	private float timePassed = 0;
	private Vector2 end = null;
	private float xdiff = 0;
	private float ydiff = 0;
	com.badlogic.gdx.graphics.g2d.Animation animation1 = null;
	com.badlogic.gdx.graphics.g2d.Animation animation2 = null;
	
	Animation shake1 = null, shake2= null, move = null;
	private Map<Unit, Integer> units;
	
	public ThrowIceAnimation(TileCoordinate target, Map<Unit, Integer> units) {
		animation1 = Assets.getAnimation("meteorstrike");
		animation2 = Assets.getAnimation("meteorimpact");
		
		this.units = units;
				
		this.target = GEngine.getInstance().getMap().getTile(target);
		length = animation1.animationDuration*4+animation2.animationDuration;
		lifetimeLeft = length;
		
		
		end = this.target.getPosition();
		
		
		xdiff = 3*GEngine.getInstance().map.TILE_WIDTH;
		ydiff = 3*GEngine.getInstance().map.TILE_HEIGHT;
		
		shake1 = new ShakeCamAnimation(new Vector2(GEngine.getInstance().camera.position.x, GEngine.getInstance().camera.position.y), GEngine.getInstance().map.TILE_WIDTH/25, GEngine.getInstance().map.TILE_HEIGHT/25, length/8, 12);
		shake2 = new ShakeCamAnimation(new Vector2(GEngine.getInstance().camera.position.x, GEngine.getInstance().camera.position.y), GEngine.getInstance().map.TILE_WIDTH/15, GEngine.getInstance().map.TILE_HEIGHT/15, 1.1f*animation2.animationDuration, 20);
		
	}
	
	@Override
	public void run(float deltaTime) {
		// Run once
		if(!started){
			started = true;	
			GEngine.getInstance().animationHandler.runParalell(shake1);
		}
		
		if(lifetimeLeft <= 0f){
			Set<Unit> list = units.keySet();
			for (Unit unit: list) {
				if (unit != null) {
					if(unit.currentHP <= 0){
						GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(unit,
								GEngine.getInstance().getUnit(unit).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
					}
					FloatingTextAnimation animation = new FloatingTextAnimation(unit.tileCoordinate, String.valueOf(units.get(unit)), Color.RED);
					GEngine.getInstance().animationHandler.runParalell(animation);
					GEngine.getInstance().getUnit(unit).updateHealthbar();
				}
			}
		}
		if (lifetimeLeft <= animation2.animationDuration && started2 == false) {
			started2 = true;
			GEngine.getInstance().animationHandler.runParalell(shake2);
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
		position.x = end.x+xdiff*(float)Math.pow(var+0.2, 3); 
		position.y = end.y-ydiff*(float)Math.pow(var+0.2, 3);
		
		float width = map.TILE_WIDTH;
		float height = map.TILE_HEIGHT;
		
		float cw = 4*width*(0.75f+(10*(float)Math.pow(var, 4)));
		float ch = 4*height*(0.75f+(10*(float)Math.pow(var, 4)));
		
		if (lifetimeLeft > animation2.animationDuration){
			TextureRegion region = this.animation1.getKeyFrame(timePassed, true);
			batch.draw(region, position.x-cw/2+width/2, position.y-ch/2+height/2, cw, ch);
		}
		else {
			TextureRegion region = this.animation2.getKeyFrame(timePassed-animation1.animationDuration*4, false);
			batch.draw(region, position.x-(width/2), position.y-(width/2), width*2, height*2);
		
		}
	}


}
