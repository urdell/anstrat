package com.anstrat.gameCore;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.anstrat.geography.Map;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;

public class TestAdjecantAndOrientation {

	private Map mapFlat, mapPointyOddCols, mapPointyEvenCols;
	private TileCoordinate origin;
	
	@Before
	public void setUp() {
		mapFlat = new Map(10, 10, true);
		mapPointyOddCols = new Map(10, 5, false);
		mapPointyEvenCols = new Map(2, 2, false);
		origin = new TileCoordinate(1, 1);
	}
	
	@Test
	public void testAdjacentFlat(){
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 1)) == Map.ADJACENT_NW);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 0)) == Map.ADJACENT_N);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 1)) == Map.ADJACENT_NE);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 2)) == Map.ADJACENT_SW);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 2)) == Map.ADJACENT_S);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 2)) == Map.ADJACENT_SE);
	}
	
	@Test
	public void testNonAdjacentTilesFlat(){
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 0)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(0, 3)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 3)) == Map.NOT_ADJACENT);
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(2, 0)) == Map.NOT_ADJACENT);
	}
	
	@Test
	public void testNotAdjacentWithSelfFlat(){
		assertTrue(mapFlat.getAdjacentOrientation(origin, new TileCoordinate(1, 1)) == Map.NOT_ADJACENT);
	}
	
	@Test
	public void testNearbyTilesAreAdjacentFlat(){
		testNearbyTilesAreAdjacent(mapFlat);
	}

	@Test
	public void testAdjacentTilesOddColumnsPointy(){
		testAdjacentTilesPointy(mapPointyOddCols);
	}
	
	@Test
	public void testNonAdjacentTilesOddColumnsPointy(){
		testNonAdjacentTilesPointy(mapPointyOddCols);
	}
	
	@Test
	public void testNotAdjacentOddColumnsWithSelfPointy(){
		testNotAdjacentWithSelfPointy(mapPointyOddCols);
	}
	
	@Test
	public void testNearbyTilesOddColumnsAreAdjacentPointy(){
		testNearbyTilesAreAdjacent(mapPointyOddCols);
	}
	
	@Test
	public void testAdjacentTilesEvenColumnsPointy(){
		testAdjacentTilesPointy(mapPointyEvenCols);
	}
	
	@Test
	public void testNonAdjacentTilesEvenColumnsPointy(){
		testNonAdjacentTilesPointy(mapPointyEvenCols);
	}
	
	@Test
	public void testNotAdjacenEvenColumnsWithSelfPointy(){
		testNotAdjacentWithSelfPointy(mapPointyEvenCols);
	}
	
	@Test
	public void testNearbyTilesEvenColumnsAreAdjacentPointy(){
		testNearbyTilesAreAdjacent(mapPointyEvenCols);
	}
	
	private void testAdjacentTilesPointy(Map map){
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(1, 0)) == Map.ADJACENT_NW);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(2, 0)) == Map.ADJACENT_NE);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(0, 1)) == Map.ADJACENT_W);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(2, 1)) == Map.ADJACENT_E);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(1, 2)) == Map.ADJACENT_SW);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(2, 2)) == Map.ADJACENT_SE);
	}
	
	private void testNonAdjacentTilesPointy(Map map){
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(0, 0)) == Map.NOT_ADJACENT);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(3, 0)) == Map.NOT_ADJACENT);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(0, 2)) == Map.NOT_ADJACENT);
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(3, 2)) == Map.NOT_ADJACENT);
	}
	
	private void testNotAdjacentWithSelfPointy(Map map){
		assertTrue(map.getAdjacentOrientation(origin, new TileCoordinate(1, 1)) == Map.NOT_ADJACENT);
	}
	
	private void testNearbyTilesAreAdjacent(Map map){
		for(Tile tile : map.getNeighbors(origin)){
			assertTrue(map.getAdjacentOrientation(origin, tile.coordinates) != Map.NOT_ADJACENT);
		}
	}
}
