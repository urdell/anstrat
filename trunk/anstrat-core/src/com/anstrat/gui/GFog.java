package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GFog {

	static float fogSize = 1.4f;
	
	public static void drawFog(SpriteBatch batch){
		GMap gMap = GEngine.getInstance().getMap();	
		if(gMap.map.fogEnabled){
			batch.setColor(Color.toFloatBits(1f, 1f, 1f, 0.65f));
			TextureRegion fogTexture = Assets.getTextureRegion("fogofwar");
			
			for(GTile[] row : gMap.tiles){
				for(GTile gTile : row){
					if(gTile.tile.visible == null){	// Temporary fix for outdated maps
						gTile.tile.visible = new int[2];
					}
					if( gTile.tile.visible[ GameInstance.activeGame.getUserPlayer().playerId ] < 1){
						
						batch.draw(fogTexture, gTile.getCenter().x-(gMap.TILE_WIDTH/2)*fogSize, gTile.getCenter().y-(gMap.TILE_HEIGHT/2)*fogSize, gMap.TILE_WIDTH*fogSize, gMap.TILE_HEIGHT*fogSize);
				
					}
				}
			}
			batch.setColor(Color.WHITE);
		}
		
	}
	
	
}
