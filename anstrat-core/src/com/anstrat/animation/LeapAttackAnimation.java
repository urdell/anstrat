package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class LeapAttackAnimation extends Animation {
	private Vector2 start, current, end;
	private GUnit gunit, target;
	private int damage;
	
	private float xoffset, yoffset, amtOffset;
	private boolean enterFinished, finishStarted, started, hurt;
	private TileCoordinate startTile, endTile;
	
	public LeapAttackAnimation(Unit unit, Unit target, int damage, TileCoordinate startTile, TileCoordinate endTile){
		GEngine engine = GEngine.getInstance();
		
		this.startTile = startTile;
		this.endTile = endTile;
		gunit = engine.getUnit(unit);
		this.target = engine.getUnit(target);
		this.damage = damage;
		
		start = engine.getMap().getTile(startTile).getCenter();
		end = engine.getMap().getTile(endTile).getCenter();
		current = new Vector2();
		Vector2 distance = end.cpy().sub(start);
		
		xoffset = distance.x;
		yoffset = distance.y;
		
		length = 1.5f;
		lifetimeLeft = length;
		enterFinished = false;
		finishStarted = false;
		hurt = false;
	}
	

	@Override
	public void run(float deltaTime) {
		
		// Run once
		if(!started){
			started = true;
			gunit.updateHealthbar();
			gunit.setFacingRight(xoffset >= 0);
			GEngine.getInstance().updateUI();
			moveCamera();
			gunit.playCustom(Assets.getAnimation("berserker-ability-enter"), true);
			
		}
		
		if(!hurt && lifetimeLeft <= length / 2f){
			hurt = true;
			target.playHurt();
		}
		
		if(lifetimeLeft <= 1.0f && !enterFinished) {
			enterFinished = true;
			gunit.playCustom(Assets.getAnimation("berserker-ability"), true);
			target.updateHealthbar();
			FloatingNumberAnimation animation2 = new FloatingNumberAnimation(target.unit.tileCoordinate, damage, 40f, Color.RED);
			GEngine.getInstance().animationHandler.runParalell(animation2);
			if(!target.unit.isAlive){
				Animation deathAnimation = new DeathAnimation(target.unit, startTile);
				GEngine.getInstance().animationHandler.runParalell(deathAnimation);
			}
		}
		else if(lifetimeLeft <= 0.5f && !finishStarted) {
			finishStarted = true;
			gunit.playCustom(Assets.getAnimation("berserker-ability-finish"), true);
		}
		
		if(lifetimeLeft <= 0){
			gunit.setRotation(0);
			gunit.setPosition(end);
			gunit.playIdle();	
		}
		
		else if (lifetimeLeft <= 1.0f && lifetimeLeft >= 0.5f){
			amtOffset = (0.5f-(lifetimeLeft-0.5f))/0.5f;
			current.set(start.x + xoffset*amtOffset, start.y + yoffset*amtOffset);
			gunit.setPosition(current);
		}
	}
	
	private void moveCamera() {
		if(isVisible()) {
			Animation animation = new MoveCameraAnimation(end);
			GEngine.getInstance().animationHandler.runParalell(animation);
		}
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(startTile,  GameInstance.activeGame.getUserPlayer().playerId) ||
				Fog.isVisible(endTile,  GameInstance.activeGame.getUserPlayer().playerId);
		
	}
}
