package com.anstrat.animation;

import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.State;
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
	private TileCoordinate startTile, endTile, lastTile;
	private int playerID;
	
	public MoveAnimation(Unit unit, TileCoordinate startTile, TileCoordinate endTile, TileCoordinate lastTile, int playerID){
		GEngine engine = GEngine.getInstance();
		this.playerID = playerID;
		this.startTile = startTile;
		this.endTile = endTile;
		this.lastTile = lastTile;
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
		
		gunit.unit.moveVisible = isVisible();
		gunit.unit.moveActive = true;
		
		// Run once
		if(!started){
			started = true;
			gunit.updateHealthbar();
			gunit.setFacingRight(xoffset >= 0);
			GEngine.getInstance().updateUI();
			moveCamPar();
			// Only start the walk animation once, at the start of the animation sequence
			if(isFirst){
				gunit.playWalk();
			}
			Fog.recalculateFog(playerID, State.activeState, endTile, lastTile);
		}
		
		if(lifetimeLeft <= 0){
			gunit.setRotation(0);
			gunit.setPosition(end);
			
			// Only stop the walk animation once, at the end of the animation sequence
			if(isLast){
				gunit.unit.moveVisible = true;
				gunit.unit.moveActive = false;
				gunit.playIdle();
				moveCamPar();
			}
		}
		else{
			amtOffset = (length-lifetimeLeft)/length;
			current.set(start.x + xoffset*amtOffset, start.y + yoffset*amtOffset);
			gunit.setPosition(current);
			// No reason to have length on this if not visible
			if(!isVisible()) lifetimeLeft = 0;
		}
	}
	
	public void moveCamera() {
		if(isVisible()) {
			Animation animation = new MoveCameraAnimation(end);
			GEngine.getInstance().animationHandler.enqueue(animation);
		}
	}
	
	public void moveCamPar() {
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
	
	//@Override
	//public void postAnimationAction(){
	//	Fog.recalculateFog((State.activeState.currentPlayerId+1)%2, State.activeState);
	//}
}