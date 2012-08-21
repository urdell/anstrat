package com.anstrat.gui.confirmDialog;

import java.util.LinkedList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.geography.Path;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.MapLine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Information drawn onto map, such as movement and attack lines.
 * @author Anton
 *
 */
public class ConfirmOverlay {

	private static final float checkpointSize = 40;
	private static final float numberSize = 25;
	public List<MapLine> lines = new LinkedList<MapLine>();
	/**
	 * Center of tiles that are being moved throuogh.
	 * Contains X, Y, cost
	 */
	public List<Vector3> checkpoints = new LinkedList<Vector3>();
	public Color checkpointTint = Color.WHITE;
	public boolean showHelpfulText = true;
	
	public void showMove(TileCoordinate startPos, Path path){
		GMap gMap = GEngine.getInstance().map;
		
		checkpointTint = Color.YELLOW;
		lines.clear();
		checkpoints.clear();
		TileCoordinate lastCoordinate = startPos;
		for(TileCoordinate currentCoordinate : path.path){
			
			lines.add(new MapLine(gMap.getTile(lastCoordinate), gMap.getTile(currentCoordinate), 0));
			Vector2 checkpointPosition = gMap.getTile(currentCoordinate).getCenter();
			int pathCost=0;
			if(GEngine.getInstance().selectionHandler.selectedUnit != null){
				pathCost = path.getSubPath(currentCoordinate).getPathCost(GEngine.getInstance().selectionHandler.selectedUnit.getUnitType());
			}
			Vector3 newCheckpoint = new Vector3(checkpointPosition.x, checkpointPosition.y, pathCost);
			checkpoints.add(newCheckpoint);
			lastCoordinate = currentCoordinate;
		}
	}
	
	public void showAttack(TileCoordinate startPos, TileCoordinate targetPos, int attackCost){
		GMap gMap = GEngine.getInstance().map;
		
		checkpointTint = Color.RED;
		lines.clear();
		checkpoints.clear();
		lines.add(new MapLine(gMap.getTile(startPos), gMap.getTile(targetPos), 1));
		Vector2 checkpointPosition = gMap.getTile(targetPos).getCenter();
		Vector3 newCheckpoint = new Vector3(checkpointPosition.x, checkpointPosition.y, attackCost);
		checkpoints.add(newCheckpoint);
	}
	
	public void clear(){
		lines.clear();
		checkpoints.clear();
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
		TextureRegion circle = Assets.getTextureRegion("movement-line-circle");
		
		for(Vector3 checkpoint : checkpoints){
			TextureRegion number = Assets.getTextureRegion("ap-"+(int)checkpoint.z);
			batch.setColor(checkpointTint);
			batch.draw( circle, checkpoint.x-checkpointSize/2, checkpoint.y-checkpointSize/2, checkpointSize, checkpointSize);
			batch.setColor(Color.toFloatBits(0.4f, 1f, 1f, 1f));
			batch.draw( number, checkpoint.x-numberSize/2, checkpoint.y-numberSize/2+numberSize, numberSize, -numberSize); // flip upside down and compensate position		
		}
		batch.setColor(Color.WHITE);
	}
}
