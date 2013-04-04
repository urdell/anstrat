package com.anstrat.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GTile;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ThrowIceAnimation extends Animation {

	private GTile target;
	private GUnit gsource;
	private Unit source;
	private boolean started = false, started2 = false, 
			initialImpact = false, shardImpact = false;
	private float timePassed = 0;
	private Vector2 end = null;
	private List<GTile> splashTiles = null;
	private float xoffset = 0;
	private float yoffset = 0;
	private Vector2 startPos, currPos, targetPos;
	com.badlogic.gdx.graphics.g2d.Animation j_ability = null;
	com.badlogic.gdx.graphics.g2d.Animation j_ability_effect = null;
	com.badlogic.gdx.graphics.g2d.Animation shard_effect = null;
	private float preparation_time = 0.85f, shard_time = 1.25f;
	private Sprite shard_sprite = null;
	private int NUM_SHARDS = 24;
	private float[] shard_times = new float[NUM_SHARDS];
	private float[] shard_sizes = new float[NUM_SHARDS];
	private float[] shard_rotation = new float[NUM_SHARDS];
	private float[] shard_speed = new float[NUM_SHARDS];
	Random rand = new Random();
	
	Animation shake1 = null, shake2= null, move = null;
	private Map<Unit, Integer> units;
	
	public ThrowIceAnimation(Unit source, TileCoordinate target, Map<Unit, Integer> units) {		
		GEngine engine = GEngine.getInstance();
		
		for(int i=0;i<NUM_SHARDS;i++){
			shard_times[i] = rand.nextFloat()/5f;
			shard_sizes[i] = (rand.nextFloat()+0.5f)/2f;
			shard_rotation[i] = rand.nextFloat()*6f;
			shard_speed[i] = (rand.nextFloat()*1.2f + 0.3f) * 2f;
		}
		
		j_ability = Assets.getAnimation("jotun-ability");
		j_ability_effect = Assets.getAnimation("jotun-ability-effect");
		shard_effect = Assets.getAnimation("jotun-ability-effect-2");
				
		startPos = engine.map.getTile(source.tileCoordinate).getCenter();
		targetPos = engine.map.getTile(target).getCenter();
		
		splashTiles = new ArrayList<GTile>();
		
		for(Tile tile : engine.map.map.getNeighbors(target)){
			splashTiles.add(engine.map.getTile(tile.coordinates));
		}
		
		xoffset = targetPos.x - startPos.x;
		yoffset = targetPos.y - startPos.y;
		
		this.units = units;
				
		this.target = engine.getMap().getTile(target);
		length = preparation_time + j_ability_effect.animationDuration + shard_time + 0.75f;
		lifetimeLeft = length;
		
		gsource = engine.getUnit(source);
		gsource.setFacingRight(this.target.getPosition().x >= this.gsource.getPosition().x);
		
		end = this.target.getPosition();
		
		gsource.playCustom(j_ability, false);
		
		//xdiff = 3*GEngine.getInstance().map.TILE_WIDTH;
		//ydiff = 3*GEngine.getInstance().map.TILE_HEIGHT;
		
		//shake1 = new ShakeCamAnimation(new Vector2(GEngine.getInstance().camera.position.x, GEngine.getInstance().camera.position.y), GEngine.getInstance().map.TILE_WIDTH/25, GEngine.getInstance().map.TILE_HEIGHT/25, length/8, 12);
		//shake2 = new ShakeCamAnimation(new Vector2(GEngine.getInstance().camera.position.x, GEngine.getInstance().camera.position.y), GEngine.getInstance().map.TILE_WIDTH/15, GEngine.getInstance().map.TILE_HEIGHT/15, 1.1f*animation2.animationDuration, 20);
		
	}
	
	@Override
	public void run(float deltaTime) {
		// Run once
		if(!started){
			started = true;	
			//GEngine.getInstance().animationHandler.runParalell(shake1);
		}
		
		if(!initialImpact && lifetimeLeft - shard_time - 0.75f <= 0f){
			initialImpact = true;
			Unit unit = StateUtils.getUnitByTile(target.tile.coordinates);
			if(unit != null){
				unit.resolveDeath();
				if(unit.currentHP <= 0){
					GEngine.getInstance().animationHandler.runParalell(new DeathAnimation(unit,
							GEngine.getInstance().getUnit(unit).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
				}
				FloatingTextAnimation animation = new FloatingTextAnimation(unit.tileCoordinate, String.valueOf(units.get(unit)), Color.RED);
				GEngine.getInstance().animationHandler.runParalell(animation);
				GEngine.getInstance().getUnit(unit).updateHealthbar();
				units.remove(unit);
			}
		}
		
		if(!shardImpact && lifetimeLeft - shard_time / 1.64f - 0.75f <= 0f){
			shardImpact = true;
			Set<Unit> list = units.keySet();
			for (Unit unit: list) {
				if (unit != null) {
					if(unit.currentHP <= 0){
						GEngine.getInstance().animationHandler.runParalell(new DeathAnimation(unit,
								GEngine.getInstance().getUnit(unit).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
					}
					FloatingTextAnimation animation = new FloatingTextAnimation(unit.tileCoordinate, String.valueOf(units.get(unit)), Color.RED);
					GEngine.getInstance().animationHandler.runParalell(animation);
					GEngine.getInstance().getUnit(unit).updateHealthbar();
				}
			}
		}
		//if (lifetimeLeft <= animation2.animationDuration && started2 == false) {
		//	started2 = true;
		//	//GEngine.getInstance().animationHandler.runParalell(shake2);
		//}
		timePassed += deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		if(timePassed > preparation_time && timePassed < preparation_time + j_ability_effect.animationDuration) {			
			TextureRegion srcregion = j_ability_effect.getKeyFrame(timePassed-preparation_time, false);
			
			float amtOffset = (timePassed-preparation_time) / j_ability_effect.animationDuration;
			
			float baseScale = 0.75f;
			float calcScale = 2f-2f*(float)Math.pow((amtOffset*2f-1),2);
			float iceScale = baseScale*calcScale;
			
			// TODO should not be here, ekis' fault
			batch.setColor(Color.WHITE);
			batch.draw(srcregion, startPos.x-srcregion.getRegionWidth() * iceScale / 2 + xoffset*amtOffset, 
					startPos.y-srcregion.getRegionHeight() *iceScale / 2 + yoffset*amtOffset, 
					srcregion.getRegionWidth()*iceScale, srcregion.getRegionHeight()*iceScale);
			
			//batch.draw(srcregion, startPos.x-srcregion.getRegionWidth() / 2 + xoffset*amtOffset, 
			//		startPos.y-srcregion.getRegionHeight() / 2 + yoffset*amtOffset);
		}
		if(timePassed > preparation_time + j_ability_effect.animationDuration){
			Vector2 offsets = new Vector2();
			batch.setColor(Color.WHITE);
			//for(GTile splashing : splashTiles){
			for(int i=0;i<NUM_SHARDS;i++){
				if(timePassed > preparation_time + j_ability_effect.animationDuration + shard_times[i]){
					float angle = (float) i * 360f/24f;
					float amtOffset = (timePassed - preparation_time - j_ability_effect.animationDuration
							- shard_times[i])/shard_time;
					TextureRegion srcregion = shard_effect.getKeyFrame(timePassed-preparation_time, true);
					offsets.x = (float)Math.cos(Math.toRadians(angle)) * amtOffset * 
							GEngine.getInstance().map.TILE_SIDE_LENGTH * shard_speed[i];
					offsets.y = (float)Math.sin(Math.toRadians(angle)) * amtOffset * 
							GEngine.getInstance().map.TILE_SIDE_LENGTH * shard_speed[i];
					if(shard_sprite==null)
						shard_sprite = new Sprite(srcregion);
					shard_sprite.setRotation(angle+90f+360f*shard_rotation[i]*amtOffset);
					shard_sprite.setScale(shard_sizes[i]);
					shard_sprite.setPosition(target.getCenter().x - srcregion.getRegionWidth() / 2f + offsets.x, 
							target.getCenter().y - srcregion.getRegionHeight() / 2f + offsets.y);
					Color temp = shard_sprite.getColor();
					shard_sprite.setColor(temp.r, temp.g, temp.b, amtOffset>=1f?0:(float)Math.sqrt(Math.sqrt(1f - amtOffset)));
					shard_sprite.draw(batch);
					shard_sprite.setColor(temp);
				}
			}
		}
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return true;
	}


}
