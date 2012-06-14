package com.anstrat.gui;

import com.anstrat.geography.Map;
import com.anstrat.geography.TileCoordinate;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GMap {

	private int ySize, xSize;
	public GTile tiles[][];
	
	// Calculate the pixel side length, width and height. 
	// As size is determined by camera zoom, pixel density must not be used.
	// (see http://www.gamedev.net/index.php?app=core&module=attach&section=attach&attach_rel_module=ccs&attach_id=1961)
	public static final float TILE_WIDTH = 128;
	public static final float TILE_SIDE_LENGTH = TILE_WIDTH / 2;
	public static final float TILE_HEIGHT = 2 * (float)Math.cos(Math.toRadians(30)) * TILE_SIDE_LENGTH;
	
	private OrthographicCamera camera;
	
	/**
	 * @param map the {@link Map} to display.
	 * @param camera the camera used.
	 * @param tileWidth the width of a tile in pixels. 
	 */
	public GMap(Map map, OrthographicCamera camera){
		this.camera = camera;
		
		this.xSize = map.getXSize();
		this.ySize = map.getYSize();
		
		tiles = new GTile[xSize][ySize];
		
		float h = (float) Math.sin(Math.toRadians(30)) * TILE_SIDE_LENGTH;
		float r =  (float) Math.cos(Math.toRadians(30)) * TILE_SIDE_LENGTH;
		
		for(int xIndex=0;xIndex<xSize;xIndex++){
			for(int yIndex=0;yIndex<ySize;yIndex++){
				float xPixel = xIndex * (h + TILE_SIDE_LENGTH);
				float yPixel = yIndex * 2 * r;
				
				// Offset r if this is an odd row
				if(xIndex % 2 != 0){
					yPixel += r;
				}
				
				Vector2 position = new Vector2(xPixel, yPixel);
				tiles[xIndex][yIndex] = new GTile(position, map.tiles[xIndex][yIndex], TILE_SIDE_LENGTH, TILE_WIDTH, TILE_HEIGHT);
			}
		}
	}
	
	public float getWidth(){
		int widths = xSize / 2;
		return widths * TILE_WIDTH + (xSize - widths) * TILE_SIDE_LENGTH 
				+ (xSize % 2 == 0 ? 1 : 2) * (float) (Math.sin(Math.toRadians(30)) * TILE_SIDE_LENGTH); 
	}
	
	public float getHeight(){
		return ySize * TILE_HEIGHT + TILE_HEIGHT / 2f;
	}
	
	public int getXSize(){
		return xSize;
	}
	
	public int getYSize(){
		return ySize;
	}
	
	public void render(GL10 gl){
		
		for(GTile[] tRow : tiles){
			for(GTile tile : tRow){
				if(camera.frustum.boundsInFrustum(tile.getBoundingBox())){
					tile.render(gl);
				}
			}
		}
		for(GTile[] tRow : tiles){
			for(GTile tile : tRow){
				if(camera.frustum.boundsInFrustum(tile.getBoundingBox())){
					tile.renderOutline(gl, Color.BLACK, 1.1f);
				}
			}
		}
	}
	
	/**
	 * Given a pixel coordinate of the GMap (ex. click position), gives the tile coordinate.
	 * @param v the x,y in window coordinates
	 */
	public TileCoordinate coordinate(Vector2 v){
		return coordinate(v, true);
	}
	
	private final Vector3 temp = new Vector3();
	
	/**
	 * @param v the coordinates to lookup
	 * @param useWindowCoordinates if true v is treated as window coordinates, otherwise as world coordinates
	 * @return the {@link TileCoordinate} that contains the given coordinates.
	 */
	public TileCoordinate coordinate(Vector2 v, boolean useWindowCoordinates){
		TileCoordinate result = new TileCoordinate();
		
		// Convert to world coordinates
		if(useWindowCoordinates){
			CameraUtil.windowToCameraCoordinates(camera, v, temp);
		}
		else{
			temp.x = v.x;
			temp.y = v.y;
			temp.z = 0f;
		}
		
		// See http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
		// for the meaning of r and h
		float sideLength = TILE_SIDE_LENGTH / camera.zoom;
		float h = (float) (Math.sin(Math.toRadians(30)) * sideLength);
		float r = (float) (Math.cos(Math.toRadians(30)) * sideLength);
		
		// Divide the map into equal sized rectangular sections
		int xSection = (int)(temp.x / (h + sideLength));
		int ySection = (int)(temp.y / (2 * r));
		
		// Get the coordinates local to the current section (where 0,0 = top-left corner of the current section)
		float xLocal = temp.x - xSection * (h + sideLength);
		float yLocal = temp.y - ySection * (2 * r);
		
		// There are two types of sections, type A for even columns, type B for odd columns
		boolean isTypeA = xSection % 2 == 0;
		float m = h / r; // gradient (slope) of the diagonal edges
		
		// Type A
		if(isTypeA){
			// Right side
			result.x = xSection;
			result.y = ySection;
			
			if(xLocal < (h - yLocal * m)){
				// Top edge
				result.x = xSection - 1;
				result.y = ySection - 1;
			}
			
			if(xLocal < (- h + yLocal * m)){
				// Bottom edge
				result.x = xSection - 1;
				result.y = ySection;
			}
		}
		// Type B
		else{
			if(yLocal >= r){
				if(xLocal < (2 * h - yLocal * m)){
					// Bottom-left
					result.x = xSection - 1;
					result.y = ySection;
				}
				else{
					// Bottom-right
					result.x = xSection;
					result.y = ySection;
				}
			}
			else{
				if(xLocal < (yLocal * m)){
					// Top-left
					result.x = xSection - 1;
					result.y = ySection;
				}
				else{
					// Top-right
					result.x = xSection;
					result.y = ySection - 1;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the {@link GTile} positioned at the given coordinate or <code>null</code>
	 * if the given coordinates are out of bounds.
	 * @param coordinate the {@link TileCoordinate} of the tile.
	 * @return the tile at the given position or <code>null</code> if none.
	 */
	public GTile getTile(TileCoordinate coordinate){
		if(coordinate.x < 0 || coordinate.x >= xSize || coordinate.y < 0 || coordinate.y >= ySize){
			return null;
		}
		
		return tiles[coordinate.x][coordinate.y];
	}
}
