package com.anstrat.animation;

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
	
	private Vector2 start, current, end;
	private GUnit gunit;
	private float xoffset, yoffset, amtOffset;
	private boolean isFirst, isLast, started;
	
	public MoveAnimation(Unit unit, TileCoordinate startTile, TileCoordinate endTile){
		GEngine engine = GEngine.getInstance();
		
		gunit = engine.getUnit(unit);
		
		start = engine.getMap().getTile(startTile).getCenter();
		end = engine.getMap().getTile(endTile).getCenter();
		current = new Vector2();
		Vector2 distance = end.cpy().sub(start);
		
		xoffset = distance.x;
		yoffset = distance.y;
		
		// Set animation length proportional to on the unit's movement speed
		float distanceInTiles = distance.len() / engine.map.TILE_WIDTH;
		length = distanceInTiles / unit.getUnitType().movementSpeed;
		lifetimeLeft = length;
	}
	
	// Whether or not this is the last MoveAnimation in a sequence
	public void setIsLast(){
		this.isLast = true;
	}
	
	// Whether or not this is the first MoveAnimation in a sequence
	public void setIsFirst(){
		this.isFirst = true;
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
			
			// Only start the walk animation once, at the start of the animation sequence
			if(isFirst){
				gunit.playWalk();
			}
		}
		
		if(lifetimeLeft <= 0){
			gunit.setRotation(0);
			gunit.setPosition(end);
			
			// Only stop the walk animation once, at the end of the animation sequence
			if(isLast){
				gunit.playIdle();
				moveCamera();
			}
		}
		else{
			amtOffset = (length-lifetimeLeft)/length;
			current.set(start.x + xoffset*amtOffset, start.y + yoffset*amtOffset);
			gunit.setPosition(current);
		}
	}
	
	private void moveCamera() {
		Animation animation = new MoveCameraAnimation(end);
		GEngine.getInstance().animationHandler.runParalell(animation);
	}
}
