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
		//lines.add(new MapLine(gMap.getTile(startPos), gMap.getTile(path.path.get(0)), 0));
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
