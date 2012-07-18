package com.anstrat.geography;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.anstrat.gui.GEngine;

public class TestMapPointy {
	private Map map;
	
	@Before
	public void setUp() throws Exception {
		map = new Map(3, 3);
		GEngine.FLAT_TILE_ORIENTATION = false;
	}
	
	@Test
	public void testGetNeighborMapCenter(){
		Collection<Tile> tiles = map.getNeighbors(new TileCoordinate(1, 1));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 0)));
		assertTrue(containsTile(tiles, new TileCoordinate(2, 0)));
		assertTrue(containsTile(tiles, new TileCoordinate(0, 1)));
		assertTrue(containsTile(tiles, new TileCoordinate(2, 1)));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 2)));
		assertTrue(containsTile(tiles, new TileCoordinate(2, 2)));
		assertEquals(6, tiles.size());
	}

	@Test
	public void testGetNeighborsMapLeftEdge(){
		Collection<Tile> tiles = map.getNeighbors(new TileCoordinate(0, 1));
		assertTrue(containsTile(tiles, new TileCoordinate(0, 0)));
		assertTrue(containsTile(tiles, new TileCoordinate(0, 2)));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 1)));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 2)));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 0)));
		assertEquals(5, tiles.size());
	}
	
	@Test
	public void testGetNeighborsMapRightEdge(){
		Collection<Tile> tiles = map.getNeighbors(new TileCoordinate(2, 1));
		assertTrue(containsTile(tiles, new TileCoordinate(2, 0)));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 1)));
		assertTrue(containsTile(tiles, new TileCoordinate(2, 2)));
		assertEquals(3, tiles.size());
	}
	
	@Test
	public void testGetNeighborsMapCorner(){
		// Top-left corner (0,0) should have neighbors: (1,0) and (0,1)
		Collection<Tile> tiles = map.getNeighbors(new TileCoordinate(0, 0));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 0)));
		assertTrue(containsTile(tiles, new TileCoordinate(0, 1)));
		assertEquals(2, tiles.size());
		
		// Bottom-left corner (0,2) should have neighbors: (0,1) and (1,2)
		tiles = map.getNeighbors(new TileCoordinate(0, 2));
		assertTrue(containsTile(tiles, new TileCoordinate(0, 1)));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 2)));
		assertEquals(2, tiles.size());
		
		// Top-right corner (2,0) should have neighbors: (1,0), (1,1) and (2,1)
		tiles = map.getNeighbors(new TileCoordinate(2, 0));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 0)));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 1)));
		assertTrue(containsTile(tiles, new TileCoordinate(2, 1)));
		assertEquals(3, tiles.size());
		
		// Bottom-right corner (2,2) should have neighbors: (1,2), (1,1) and (2,1)
		tiles = map.getNeighbors(new TileCoordinate(2, 2));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 2)));
		assertTrue(containsTile(tiles, new TileCoordinate(1, 1)));
		assertTrue(containsTile(tiles, new TileCoordinate(2, 1)));
		assertEquals(3, tiles.size());
	}
	
	private boolean containsTile(Collection<Tile> tiles, TileCoordinate c){
		for(Tile tile : tiles){
			if(tile.coordinates.equals(c)){
				return true;
			}
		}
		
		return false;
	}
}
