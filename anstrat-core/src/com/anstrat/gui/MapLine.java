package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


/**
 * A single line between the center of two tiles
 * @author Anton
 * 
 */
public class MapLine {
	
	float size = 128;
	float lineWidth = 10;
	float centerX, centerY;
	
	public MapLine(GTile startTile, GTile endTile, int lineType){
		centerX = (startTile.getPosition().x + endTile.getPosition().x)/2;
		centerY = (startTile.getPosition().y + endTile.getPosition().y)/2;
		
	}
	
	
	public void draw(SpriteBatch batch){
		
		TextureRegion line = Assets.getTextureRegion("movement-line");
		batch.draw(line, centerX-size/2, centerY-size/2, size/2, size/2, size, size, 1f, 1f, 0);
		
	}

}
