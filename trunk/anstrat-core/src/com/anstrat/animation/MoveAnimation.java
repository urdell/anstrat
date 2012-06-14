package com.anstrat.animation;

import com.anstrat.core.User;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.math.Vector2;

/**
 * 
 * Interpolates the Unit between two tiles. End position will be at center of endTile.
 *
 */
public class MoveAnimation extends Animation {
	
	public static final float moveSpeed = 0.5f;
	private Vector2 start, current, end;
	GUnit gunit;
	float xoffset, yoffset, amtOffset, tiltSelector;
	private boolean isFirst, isLast, started, tiltEffect, shouldMoveCamera;
	
	public MoveAnimation(Unit unit, TileCoordinate startTile, TileCoordinate endTile){
		length = moveSpeed;
		lifetimeLeft = length;
		GEngine ge = GEngine.getInstance();
		start = ge.getMap().getTile(startTile).getCenter();
		end = ge.getMap().getTile(endTile).getCenter();
		gunit = ge.getUnit(unit);
		xoffset = end.x - start.x;
		yoffset = end.y - start.y;
		current = new Vector2();
		shouldMoveCamera = GEngine.getInstance().state.getCurrentPlayer().userID != User.globalUserID;
		this.tiltEffect = false; //never use tilteffect
	}
	
	public void setIsLast(){
		this.isLast = true;
	}
	
	public void setIsFirst(){
		this.isFirst = true;
	}

	@Override
	public void run(float deltaTime) {
		
		if(!started){
			gunit.updateHealthbar();
			gunit.setFacingRight(xoffset >= 0);
			GEngine.getInstance().updateUI();
			moveCamera();
			if(isFirst){
				gunit.playWalk();
			}
			
		}
		
		if(lifetimeLeft <= 0)
		{
			gunit.setRotation(0);
			gunit.setPosition(end);
			
			if(isLast){
				gunit.playIdle();
				moveCamera();
			}
		}
		else
		{
			if(tiltEffect){
				float tiltSpeed = 2.0f;
				float tiltAmt = 2.0f;
				
				tiltSelector = (moveSpeed-lifetimeLeft)%(1.0f/tiltSpeed);
				
				if(tiltSelector<0.25f/tiltSpeed)
					gunit.setRotation(tiltSelector*30f);
				else if(tiltSelector<0.5f/tiltSpeed)
					gunit.setRotation((0.5f/tiltSpeed-tiltSelector)*tiltAmt*18f);
				else if(tiltSelector<0.75f/tiltSpeed)
					gunit.setRotation((0.5f/tiltSpeed-tiltSelector)*tiltAmt*18f);
				else
					gunit.setRotation(-(1.0f/tiltSpeed-tiltSelector)*tiltAmt*18f);
			}
			amtOffset = (moveSpeed-lifetimeLeft)/moveSpeed;
			current.set(start.x + xoffset*amtOffset, start.y + yoffset*amtOffset);
			gunit.setPosition(current);
		}
		
		started = true;
	}
	
	private void moveCamera() {
		if (shouldMoveCamera) {
			Animation animation = new MoveCameraAnimation(end);
			GEngine.getInstance().animationHandler.runParalell(animation);
		}
	}
	
	//Implement after Unit and GUnit
	
	//public MoveAnimation()
}
