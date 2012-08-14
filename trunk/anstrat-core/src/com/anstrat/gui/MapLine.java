package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.State;
import com.anstrat.geography.Map;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * A single line between the center of two tiles
 * @author Anton
 * 
 */
public class MapLine {
	
	float tileSize = 128;
	float lineWidth = 12;
	float lineSize = tileSize+lineWidth;
	float centerX, centerY;
	float rotation = 0;
	
	public MapLine(GTile startTile, GTile endTile, int lineType){
		
		centerX = (startTile.getCenter().x + endTile.getCenter().x)/2;
		centerY = (startTile.getCenter().y + endTile.getCenter().y)/2;
		
		 // Works for pointy tiles, not flat
		switch(State.activeState.map.getAdjacentOrientation(startTile.tile.coordinates, endTile.tile.coordinates)){
		case Map.ADJACENT_E:
			rotation = -90;
			break;
		case Map.ADJACENT_W:
			rotation = 90;
			break;
		case Map.ADJACENT_NE:
			rotation = -150;
			break;
		case Map.ADJACENT_NW:
			rotation = 150;
			break;
		case Map.ADJACENT_SE:
			rotation = -30;
			break;
		case Map.ADJACENT_SW:
			rotation = 30;
			break;
		}
		
	}
	
	
	public void draw(SpriteBatch batch){
		
		TextureRegion line = Assets.getTextureRegion("movement-arrows");
		batch.setColor(Color.YELLOW);
		batch.draw(line, centerX-lineSize/2, centerY-lineSize/2, lineSize/2, lineSize/2, lineSize, lineSize, 1f, 1f, rotation);
		batch.setColor(Color.WHITE);
		
	}

}
