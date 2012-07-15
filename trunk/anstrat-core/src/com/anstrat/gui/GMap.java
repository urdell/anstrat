package com.anstrat.gui;

import com.anstrat.geography.Map;
import com.anstrat.geography.TileCoordinate;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GMap {

	public final int ySize, xSize;
	public final boolean flat;
	public final GTile tiles[][];
	
	public final float TILE_SIDE_LENGTH, TILE_WIDTH, TILE_HEIGHT;
	private OrthographicCamera camera;
	
	/**
	 * @param map the {@link Map} to display.
	 * @param camera the camera used.
	 * @param tileWidth the width of a tile in pixels. 
	 */
	public GMap(Map map, OrthographicCamera camera){
		this.camera = camera;
		this.flat = map.flat;
		this.xSize = map.getXSize();
		this.ySize = map.getYSize();
		
		tiles = new GTile[xSize][ySize];
		
		TILE_WIDTH = 128f;
		
		if(flat){
			TILE_SIDE_LENGTH = TILE_WIDTH / 2f;
			TILE_HEIGHT = 2 * (float)Math.cos(Math.toRadians(30)) * TILE_SIDE_LENGTH;
		}
		else{
			// Pointy
			TILE_SIDE_LENGTH = (TILE_WIDTH / 2f) / (float)Math.cos(Math.toRadians(30));
			TILE_HEIGHT = 2f * (float)Math.sin(Math.toRadians(30)) * TILE_SIDE_LENGTH + TILE_SIDE_LENGTH;
		}
		
		float h = (float) Math.sin(Math.toRadians(30)) * TILE_SIDE_LENGTH;
		float r =  (float) Math.cos(Math.toRadians(30)) * TILE_SIDE_LENGTH;
		
		for(int xIndex=0;xIndex<xSize;xIndex++){
			for(int yIndex=0;yIndex<ySize;yIndex++){
				float xPixel = xIndex * (flat ? (h + TILE_SIDE_LENGTH) : 2 * r);
				float yPixel = yIndex * (flat ? 2 * r : (h + TILE_SIDE_LENGTH));
				
				// Offset r if this is an odd row
				if(flat && xIndex % 2 != 0){
					yPixel += r;
				}
				
				if(!flat && yIndex % 2 != 0){
					xPixel += r;
				}
				
				Vector2 position = new Vector2(xPixel, yPixel);
				tiles[xIndex][yIndex] = new GTile(position, map.tiles[xIndex][yIndex], TILE_SIDE_LENGTH, TILE_WIDTH, TILE_HEIGHT, flat);
			}
		}
	}
	
	public float getWidth(){
		if(flat){
			return (xSize / 2f) * TILE_WIDTH + (xSize / 2f) * TILE_SIDE_LENGTH 
					+ (xSize % 2 == 0 ? 1 : 2) * (float) (Math.sin(Math.toRadians(30)) * TILE_SIDE_LENGTH);
		}
		else{
			return (xSize * TILE_WIDTH) + (TILE_WIDTH / 2f);
		}
	}
	
	public float getHeight(){
		if(flat){
			return ySize * TILE_HEIGHT + TILE_HEIGHT / 2f;
		}
		else{
			return (ySize / 2) * TILE_HEIGHT + (ySize / 2f) * TILE_SIDE_LENGTH 
				+ (ySize % 2 == 0 ? 1 : 2) * (float) (Math.sin(Math.toRadians(30)) * TILE_SIDE_LENGTH);
		}
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
		// Convert to world coordinates
		if(useWindowCoordinates){
			CameraUtil.windowToCameraCoordinates(camera, v, temp);
		}
		else{
			temp.x = v.x;
			temp.y = v.y;
			temp.z = 0f;
		}
		
		if(flat){
			return coordinateFlat(v);
		}
		else{
			return coordinatePointy(v);
		}
	}
	
	// TODO: Simplify, a lot is repeated between coordinateFlat and coordinatePointy
	private TileCoordinate coordinateFlat(Vector2 v){
		TileCoordinate result = new TileCoordinate();
		
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
	
	private TileCoordinate coordinatePointy(Vector2 v){
		TileCoordinate result = new TileCoordinate();
		
		// See http://www.gamedev.net/page/resources/_/technical/game-programming/coordinates-in-hexagon-based-tile-maps-r1800
		// for the meaning of r and h
		float sideLength = TILE_SIDE_LENGTH / camera.zoom;
		float h = (float) (Math.sin(Math.toRadians(30)) * sideLength);
		float r = (float) (Math.cos(Math.toRadians(30)) * sideLength);
		
		// Divide the map into equal sized rectangular sections
		int xSection = (int)(temp.x / (2 * r));
		int ySection = (int)(temp.y / (h + sideLength));
		
		// Get the coordinates local to the current section (where 0,0 = top-left corner of the current section)
		float xLocal = temp.x - xSection * (2 * r);
		float yLocal = temp.y - ySection * (h + sideLength);

		// There are two types of sections, type A for even columns, type B for odd columns
		boolean isTypeA = ySection % 2 == 0;
		float m = h / r; // gradient (slope) of the diagonal edges
		
		// Type A
		if(isTypeA){
			// Middle
			result.x = xSection;
			result.y = ySection;
			
			if(yLocal < (h - xLocal * m)){
				// Left edge
				result.x = xSection - 1;
				result.y = ySection - 1;
			}
			
			if(yLocal < (- h + xLocal * m)){
				// Right edge
				result.x = xSection;
				result.y = ySection - 1;
			}
		}
		// Type B
		else{
			if(xLocal >= r){
				if(yLocal < (2 * h -xLocal * m)){
					// Bottom-left
					result.x = xSection;
					result.y = ySection - 1;
				}
				else{
					// Bottom-right
					result.x = xSection;
					result.y = ySection;
				}
			}
			else{
				if(yLocal < (xLocal * m)){
					// Top-left
					result.x = xSection;
					result.y = ySection - 1;
				}
				else{
					// Top-right
					result.x = xSection - 1;
					result.y = ySection;
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
