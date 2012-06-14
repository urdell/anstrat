package com.anstrat.gameCore;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.anstrat.TestUtil;
import com.anstrat.geography.TileCoordinate;

public class TestAdjecantAndOrientation {

	
	@Test	
	public void TestAdjecantNE(){
		TileCoordinate t1 = new TileCoordinate(5, 5);
		TileCoordinate t2 = new TileCoordinate(6, 5);
		assertTrue(StateUtils.NearbyOrientation(t1, t2) == StateUtils.ADJECANT_NE);
	}
	@Test	
	public void TestAdjecantNW(){
		TileCoordinate t1 = new TileCoordinate(5, 5);
		TileCoordinate t2 = new TileCoordinate(4, 5);
		assertTrue(StateUtils.NearbyOrientation(t1, t2) == StateUtils.ADJECANT_NW);
	}
	@Test	
	public void TestAdjecantSE(){
		TileCoordinate t1 = new TileCoordinate(5, 5);
		TileCoordinate t2 = new TileCoordinate(6, 6);
		assertTrue(StateUtils.NearbyOrientation(t1, t2) == StateUtils.ADJECANT_SE);
	}
	
	
}
