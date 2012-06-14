package com.anstrat.geography;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.anstrat.TestUtil;

public class TestTile {
	private Tile tile;
	
	@Before
	public void setUp() throws Exception {
		tile = new Tile(new TileCoordinate(0, 0), TerrainType.FIELD);
	}
	
	@Test
	public void testSerialization() throws FileNotFoundException, IOException, ClassNotFoundException{
		TestUtil.writeObject(tile, "tile.bin");
		Tile dTile = (Tile) TestUtil.readObject("tile.bin");
		assertTrue(isTileEqual(tile, dTile));
		new File("tile.bin").delete();
	}
	
	private boolean isTileEqual(Tile a, Tile b){
		return a.coordinates.equals(b.coordinates) &&
				a.terrain.equals(b.terrain);
	}
}
