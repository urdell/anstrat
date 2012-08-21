package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.State;
import com.anstrat.geography.Map;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * A single line between the center of two tiles
 * @author Anton
 * 
 */
public class MapLine {
	
	float lineDistance = 128; // default tileSize
	float lineWidth = 12;
	float lineSize = lineDistance+lineWidth;
	float centerX, centerY;
	float rotation = 0;
	Animation lineAnimation;
	Color lineTint;
	
	public MapLine(GTile startTile, GTile endTile, int lineType){
		
		switch(lineType){
		case 0: //movement
			lineAnimation = Assets.getAnimation("movement-line-arrows");
			lineTint = Color.YELLOW;
			break;
		case 1: //movement
			lineAnimation = Assets.getAnimation("attack-line");
			lineTint = Color.RED;
			break;
		}
		
		
		centerX = (startTile.getCenter().x + endTile.getCenter().x)/2;
		centerY = (startTile.getCenter().y + endTile.getCenter().y)/2;
		
		
		
		 // Works for pointy tiles, not flat
		switch(State.activeState.map.getAdjacentOrientation(startTile.tile.coordinates, endTile.tile.coordinates)){
		case Map.ADJACENT_E:
			rotation = 90;
			break;
		case Map.ADJACENT_W:
			rotation = -90;
			break;
		case Map.ADJACENT_NE:
			rotation = 30;
			break;
		case Map.ADJACENT_NW:
			rotation = -30;
			break;
		case Map.ADJACENT_SE:
			rotation = 150;
			break;
		case Map.ADJACENT_SW:
			rotation = -150;
			break;
		case Map.NOT_ADJACENT:
			float dx = (startTile.getCenter().x - endTile.getCenter().x);
			float dy = (startTile.getCenter().y - endTile.getCenter().y);
			lineDistance = (float) Math.sqrt( dx*dx + dy*dy );
			lineSize = lineDistance+lineWidth;
			rotation = (float) (Math.atan2(dy, dx) * 360f / (2*Math.PI))-90;
		}
		
	}
	
	
	public void draw(SpriteBatch batch){

		batch.setColor(lineTint);
		batch.draw( lineAnimation.getKeyFrame(GEngine.elapsedTime, true), 
				centerX-lineSize/2, centerY-lineSize/2, lineSize/2, lineSize/2, lineSize, lineSize, 128 / lineDistance, 1f, rotation);
		batch.setColor(Color.WHITE);
		
	}

}
