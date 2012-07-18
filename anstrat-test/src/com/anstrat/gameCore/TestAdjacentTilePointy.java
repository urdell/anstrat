package com.anstrat.gameCore;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.anstrat.geography.Map;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;

public class TestAdjacentTilePointy {
	private Map mapOddCols, mapEvenCols;
	private TileCoordinate origin;
	
	@Before
	public void setUp() throws Exception {
		GEngine.FLAT_TILE_ORIENTATION = false;
		mapOddCols = new Map(10, 9);
		mapEvenCols = new Map(10, 10);
		origin = new TileCoordinate(1, 1);
	}
	
	@Test
	public void testAdjacentEvenCols(){
		testAdjacent(mapEvenCols);
	}
	
	@Test
	public void testAdjacentOddCols(){
		testAdjacent(mapOddCols);
	}
	
	private void testAdjacent(Map map){
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(1, 0)) == Map.ADJACENT_NW);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(2, 0)) == Map.ADJACENT_NE);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(0, 1)) == Map.ADJACENT_W);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(2, 1)) == Map.ADJACENT_E);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(1, 2)) == Map.ADJACENT_SW);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(2, 2)) == Map.ADJACENT_SE);
	}
	
	@Test
	public void testNonAdjacentEvenCols(){
		testNonAdjacent(mapEvenCols);
	}
	
	@Test
	public void testNonAdjacentOddCols(){
		testNonAdjacent(mapOddCols);
	}
	
	private void testNonAdjacent(Map map){
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(0, 0)) == Map.NOT_ADJACENT);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(3, 0)) == Map.NOT_ADJACENT);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(0, 2)) == Map.NOT_ADJACENT);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(3, 2)) == Map.NOT_ADJACENT);
	}
	
	@Test
	public void testNotAdjacentWithSelfEvenCols(){
		assertTrue(mapEvenCols.getAdjacentOrientation(origin, new TileCoordinate(1, 1)) == Map.NOT_ADJACENT);
	}
	
	@Test
	public void testNotAdjacentWithSelfOddCols(){
		assertTrue(mapOddCols.getAdjacentOrientation(origin, new TileCoordinate(1, 1)) == Map.NOT_ADJACENT);
	}
	
	@Test
	public void testNearbyTilesAreAdjacentEvenCols(){
		testNearbyTilesAreAdjacent(mapEvenCols);
	}
	
	@Test
	public void testNearbyTilesAreAdjacentOddCols(){
		testNearbyTilesAreAdjacent(mapOddCols);
	}
	
	private void testNearbyTilesAreAdjacent(Map map){
		for(Tile tile : map.getNeighbors(origin)){
			assertTrue(map.getAdjacentOrientation(origin, tile.coordinates) != Map.NOT_ADJACENT);
		}
	}
}
