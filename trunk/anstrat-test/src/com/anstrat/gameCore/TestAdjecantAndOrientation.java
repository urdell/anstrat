package com.anstrat.gameCore;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.anstrat.geography.Map;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;

public class TestAdjecantAndOrientation {

	private Map mapFlat, mapPointy;
	private TileCoordinate origin;
	
	@Before
	public void setUp() {
		mapFlat = new Map(10, 10, true);
		mapPointy = new Map(10, 10, false);
		origin = new TileCoordinate(1, 1);
	}
	
	@Test
	public void testAdjacentFlat(){
		// Test adjacent tiles
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 1)) == Map.ADJACENT_NW);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 0)) == Map.ADJACENT_N);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 1)) == Map.ADJACENT_NE);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 2)) == Map.ADJACENT_SW);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 2)) == Map.ADJACENT_S);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 2)) == Map.ADJACENT_SE);
		
		// Test non-adjacent tiles
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 0)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 3)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 3)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 0)) == Map.NOT_ADJACENT);
		
		// Same origin and target
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 1)) == Map.NOT_ADJACENT);
	}
	
	@Test
	public void testAdjacentPointy(){
		// Test adjacent tiles
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 0)) == Map.ADJACENT_NW);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 0)) == Map.ADJACENT_NE);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 1)) == Map.ADJACENT_W);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 1)) == Map.ADJACENT_E);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 2)) == Map.ADJACENT_SW);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 2)) == Map.ADJACENT_SE);
		
		// Test non-adjacent tiles
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 0)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(3, 0)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 2)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(3, 2)) == Map.NOT_ADJACENT);
		
		// Same origin and target
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 1)) == Map.NOT_ADJACENT);
	}
	
	@Test
	public void testNearbyTilesAreAdjacentFlat(){
		testNearbyTilesAreAdjacent(mapFlat);
	}
	
	@Test
	public void testNearbyTilesAreAdjacentPointy(){
		testNearbyTilesAreAdjacent(mapPointy);
	}
	
	private void testNearbyTilesAreAdjacent(Map map){
		for(Tile tile : map.getNeighbors(origin)){
			assertTrue(map.getAdjacentOrientation(origin, tile.coordinates) != Map.NOT_ADJACENT);
		}
	}
}
