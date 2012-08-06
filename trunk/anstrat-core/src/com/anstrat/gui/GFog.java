package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GFog {

	public static void drawFog(SpriteBatch batch){
		TextureRegion fogTexture = Assets.getTextureRegion("zombify-0003");
		GMap gMap = GEngine.getInstance().getMap();
		for(GTile[] row : gMap.tiles){
			for(GTile gTile : row){
				if(gTile.tile.visible == null){	// Temporary fix for outdated maps
					gTile.tile.visible = new int[2];
				}
				if( gTile.tile.visible[ GameInstance.activeGame.getUserPlayer().playerId ] < 1)
					batch.draw(fogTexture, gTile.getCenter().x-gMap.TILE_WIDTH/2, gTile.getCenter().y-gMap.TILE_HEIGHT/2, gMap.TILE_WIDTH, gMap.TILE_HEIGHT);
			}
		}
	}
	
	
}
