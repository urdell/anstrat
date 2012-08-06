package com.anstrat.geography;

import java.io.Serializable;

/**
 * The class that holds information about a single Tile 
 * including the coordinates, what unit resides in the the Tile, 
 * the terraintype etc.
 * @author Ekis
 *
 */
public class Tile implements Serializable{
	
	private static final long serialVersionUID = 2L;
	public TileCoordinate coordinates;
	public TerrainType terrain;
	
	public int[] visible;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param terrain
	 */
	public Tile(TileCoordinate coordinates, TerrainType terrain){
		this.coordinates = coordinates;
		this.terrain = terrain;
		visible = new int[2];
	}
}