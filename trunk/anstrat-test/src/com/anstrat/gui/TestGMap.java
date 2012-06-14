package com.anstrat.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.anstrat.geography.Map;
import com.anstrat.geography.TileCoordinate;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class TestGMap {

	private GMap map;
	private final int SIDE_LENGTH = 10;
	
	@Before
	public void setUp() {
		int width = SIDE_LENGTH * 2;
		
		map = new GMap(new Map(3, 3), new OrthographicCamera());
	}

	@Test
	public void testCoordinateTopLeftMapCorner(){
		assertEquals(new TileCoordinate(-1, -1), map.coordinate(new Vector2(0, 0), false));
	}
	
	@Test
	public void testCoordinateTopRightMapCorner(){
		assertEquals(new TileCoordinate(3, -1), map.coordinate(new Vector2(map.getWidth(), 0), false));
	}
	
	@Test
	public void testCoordinateBottomLeftMapCorner(){
		assertEquals(new TileCoordinate(0, 3), map.coordinate(new Vector2(SIDE_LENGTH, map.getHeight()), false));
	}
	
	@Test
	public void testCoordinateBottomRightMapCorner(){
		assertEquals(new TileCoordinate(2, 3), map.coordinate(new Vector2(map.getWidth() - SIDE_LENGTH, map.getHeight()), false));
	}
	
	// Ensure 100% statement coverage
	@Test
	public void testCoordinateSectionARightSide(){
		float x = SIDE_LENGTH;
		float y = SIDE_LENGTH;
		
		assertEquals(new TileCoordinate(0, 0), map.coordinate(new Vector2(x, y), false));
	}
	
	@Test
	public void testCoordinateSectionALeftTopEdge(){
		// The height h and base r of a triangle in the corner of a hexagon
		float h = (float) (Math.sin(Math.toRadians(30)) * SIDE_LENGTH);
		float r = (float) (Math.cos(Math.toRadians(30)) * SIDE_LENGTH);
		
		// Offset to section 2,1
		float xOffset = 2 * SIDE_LENGTH + 2 * h;
		float yOffset = 2 * r;
		
		float x = xOffset + 0.05f * h;
		float y = yOffset + 0.5f * r;
		
		assertEquals(new TileCoordinate(1, 0), map.coordinate(new Vector2(x, y), false));
	}
	
	@Test
	public void testCoordinateSectionALeftBottomEdge(){
		// The height h and base r of a triangle in the corner of a hexagon
		float h = (float) (Math.sin(Math.toRadians(30)) * SIDE_LENGTH);
		float r = (float) (Math.cos(Math.toRadians(30)) * SIDE_LENGTH);
		
		// Offset to section 2,1
		float xOffset = 2 * SIDE_LENGTH + 2 * h;
		float yOffset = 2 * r;//2 * r;
		
		float x = (float) (xOffset + 0 * h);
		float y = (float) (yOffset + 1.9f * r);
		
		assertEquals(new TileCoordinate(1, 1), map.coordinate(new Vector2(x, y), false));
	}
	
	@Test
	public void testCoordinateSectionBTopLeft(){
		// The height h and base r of a triangle in the corner of a hexagon
		float h = (float) (Math.sin(Math.toRadians(30)) * SIDE_LENGTH);
		float r = (float) (Math.cos(Math.toRadians(30)) * SIDE_LENGTH);
		
		// Offset to place us at the top-left corner of the 1,1 section
		float xOffset = SIDE_LENGTH + h;
		float yOffset = 2 * r;
		
		float x = xOffset + h * 0.05f;
		float y = yOffset + r * 0.9f;
		
		assertEquals(new TileCoordinate(0, 1), map.coordinate(new Vector2(x, y), false));
	}
	
	@Test
	public void testCoordinateSectionBBottomLeft(){
		// The height h and base r of a triangle in the corner of a hexagon
		float h = (float) (Math.sin(Math.toRadians(30)) * SIDE_LENGTH);
		float r = (float) (Math.cos(Math.toRadians(30)) * SIDE_LENGTH);
		
		// Offset to place us at the top-left corner of the 1,1 section
		float xOffset = SIDE_LENGTH + h;
		float yOffset = 2 * r;
		
		float x = xOffset + h * 0.2f;
		float y = yOffset + r * 1.5f;
		
		assertEquals(new TileCoordinate(0, 1), map.coordinate(new Vector2(x, y), false));
	}
	
	@Test
	public void testCoordinateSectionBTopRight(){
		// The height h and base r of a triangle in the corner of a hexagon
		float h = (float) (Math.sin(Math.toRadians(30)) * SIDE_LENGTH);
		float r = (float) (Math.cos(Math.toRadians(30)) * SIDE_LENGTH);
		
		// Offset to place us at the top-left corner of the 1,1 section
		float xOffset = SIDE_LENGTH + h;
		float yOffset = 2 * r;
		
		float x = xOffset + 2 * h;
		float y = yOffset + 0.5f * r;
		
		assertEquals(new TileCoordinate(1, 0), map.coordinate(new Vector2(x, y), false));
	}
	
	@Test
	public void testCoordinateSectionBBottomRight(){
		// The height h and base r of a triangle in the corner of a hexagon
		float h = (float) (Math.sin(Math.toRadians(30)) * SIDE_LENGTH);
		float r = (float) (Math.cos(Math.toRadians(30)) * SIDE_LENGTH);
		
		// Offset to place us at the top-left corner of the 1,1 section
		float xOffset = SIDE_LENGTH + h;
		float yOffset = 2 * r;
		
		float x = xOffset + 2 * h;
		float y = yOffset + 1.7f * r;
		
		assertEquals(new TileCoordinate(1, 1), map.coordinate(new Vector2(x, y), false));
	}
	
	@Test
	public void testConstructorSettingSize(){
		GMap map = new GMap(new Map(5, 10), new OrthographicCamera());
		assertEquals(5, map.getXSize());
		assertEquals(10, map.getYSize());
	}
	
	private float getWidth(GMap map){
		// For every even column we get 2h + sideLength
		// For every odd column we get sideLength
		// If the total is even, we need to add h
		float h = (float) (Math.sin(Math.toRadians(30)) * map.TILE_SIDE_LENGTH);
		
		int numOddRows = map.getXSize() / 2;
		int numEvenRows = map.getXSize() - numOddRows;
		
		float width = numOddRows * map.TILE_SIDE_LENGTH + numEvenRows * (2 * h + map.TILE_SIDE_LENGTH);
		if(map.getXSize() % 2 == 0) width += h;
		
		return width;
	}
	
	private float getHeight(GMap map){
		// Height of every row is 2 * r
		// if we have even rows, we need to add r
		float r = (float) (Math.cos(Math.toRadians(30)) * map.TILE_SIDE_LENGTH);
		return map.getYSize() * 2 * r + r;
	}
	
	@Test
	public void testGetWidthEvenColumns(){
		GMap map = new GMap(new Map(10, 10), new OrthographicCamera());	
		assertTrue(Math.abs(getWidth(map) - map.getWidth()) < 0.05f);
	}
	
	@Test
	public void testGetWidthOddColumns(){
		GMap map = new GMap(new Map(9, 10), new OrthographicCamera());	
		float difference = Math.abs(getWidth(map) - map.getWidth());
		assertTrue(difference < 0.05f);
	}
	
	@Test
	public void testGetHeightEvenRows(){
		GMap map = new GMap(new Map(10, 10), new OrthographicCamera());	
		float difference = Math.abs(getHeight(map) - map.getHeight());
		assertTrue(difference < 0.05f);
	}
	
	@Test
	public void testGetHeightOddRows(){
		GMap map = new GMap(new Map(10, 9), new OrthographicCamera());	
		float difference = Math.abs(getHeight(map) - map.getHeight());
		assertTrue(difference < 0.05f);
	}
}

