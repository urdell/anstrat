package com.anstrat.animation;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ThunderboltAnimation extends Animation {
	private GUnit target;
	private List<GUnit> affected;
	private boolean started = false;
	private float timePassed = 0;
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	private int damage;
	
	public ThunderboltAnimation(Unit target, List<Unit> affected, int damage) {
		animation = Assets.getAnimation("lightning");
		this.damage = damage;
		this.affected = new ArrayList<GUnit>();
		if (affected != null) {
			for(Unit unit : affected) {
				this.affected.add(GEngine.getInstance().getUnit(unit));
			}
		}
		this.target = GEngine.getInstance().getUnit(target);
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
			target.updateHealthbar();
			FloatingTextAnimation animation = new FloatingTextAnimation(target.unit.tileCoordinate, ""+damage, Color.RED);
			GEngine.getInstance().animationHandler.runParalell(animation);
			for(GUnit unit : this.affected) {
				unit.updateHealthbar();
				FloatingTextAnimation animation2 = new FloatingTextAnimation(target.unit.tileCoordinate, ""+damage, Color.RED);
				GEngine.getInstance().animationHandler.runParalell(animation2);
			}
		}
		
		timePassed += deltaTime;
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		GMap map = GEngine.getInstance().map;
		Vector2 position = target.getPosition();
		
		float width = map.TILE_WIDTH;
		float height = map.TILE_HEIGHT * 2f;
		
		TextureRegion region = this.animation.getKeyFrame(timePassed, false);
		batch.draw(region, position.x-(width/2), position.y-(2*map.TILE_HEIGHT), width, height);
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(target.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
	
}
