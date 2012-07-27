package com.anstrat.gui.confirmDialog;

import java.util.LinkedList;
import java.util.List;

import com.anstrat.geography.Path;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.MapLine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Information drawn onto map, such as movement and attack lines.
 * @author Anton
 *
 */
public class ConfirmOverlay {

	public List<MapLine> lines = new LinkedList<MapLine>();
	
	public void showMove(TileCoordinate startPos, Path path){
		GMap gMap = GEngine.getInstance().map;
		gMap.getTile(startPos);
		lines.clear();
		TileCoordinate lastCoordinate = startPos;
		for(TileCoordinate currentCoordinate : path.path){
			lines.add(new MapLine(gMap.getTile(lastCoordinate), gMap.getTile(currentCoordinate), 0));
			lastCoordinate = currentCoordinate;
		}
		
	}
	
	public void clear(){
		lines.clear();
	}
	
	/**
	 * Layer below units
	 * @param batch
	 */
	public void drawBottomLayer(SpriteBatch batch){
		for(MapLine line : lines){
			line.draw(batch);
		}
	}
	
	/**
	 * Layer above units
	 * @param batch
	 */
	public void drawTopLayer(SpriteBatch batch){
		
	}
}
