package com.anstrat.geography;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.anstrat.gameCore.Building;
import com.anstrat.gui.GTile;

/**
 * The class that holds information about the map, including all the tiles and the size etc.
 * @author Ekis
 *
 */
public class Map implements Serializable{
	
	public final boolean flat = true;
	
	public static final int MAX_SIZE = 50;
	public static final int MIN_SIZE = 5;
	
	private static final long serialVersionUID = 1L;
	private int xsize, ysize;
	public Tile[][] tiles;
	public String name;
	private static final int[] NEIGHBORS_DX = { 0, 1, 1, 0, -1, -1 };
    private static final int[][] NEIGHBORS_DY = { 
            { -1, -1, 0, 1, 0, -1 }, { -1, 0, 1, 1, 1, 0 } };
    
    public HashMap<Integer,Building> buildingList;
	public int nextBuildingId;
	
	/**
	 * Creates an empty map of the given size filled with the standard terrain type.
	 * @param xsize
	 * @param ysize
	 */
	public Map(int xsize, int ysize)
	{
		
		this.xsize = xsize;
		this.ysize = ysize;
		tiles = new Tile[xsize][ysize];
		
		// Fill with field terrain
		for(int i=0;i<xsize;i++)
		{
			for(int j=0;j<ysize;j++)
			{
				tiles[i][j] = new Tile(new TileCoordinate(i, j), TerrainType.FIELD);
				
			}
		}

		buildingList = new HashMap<Integer,Building>();
		name = "Unnamed";
	}
	
	/**
	 * Creates a map of the given size, randomized with the given seed.
	 * @param xsize Number of columns in the map.
	 * @param ysize Number of rows in the map.
	 * @param the random seed used to generate the map, a reference to is not saved.
	 */
	public Map(int xsize, int ysize, Random random)
	{
		this.xsize = xsize;
		this.ysize = ysize;
		tiles = new Tile[xsize][ysize];
		buildingList = new HashMap<Integer,Building>();
		
		randomizeMap(random);
		name = "Random";
	}
	
	/**
	 * Checks whether two tiles are adjacent
	 */
	public boolean isAdjacent(TileCoordinate c1, TileCoordinate c2){
		for(Tile t : getNeighbors(c1)){
			if(t.coordinates.equals(c2)) return true;
		}
		
		return false;
	}
	
	/**
	 * Finds and returns a list of neighbor Tiles to the given Tile.
	 * @param tile The tile for which neighbors are requested.
	 * @return The neighboring Tiles in an ArrayList.
	 */
	public List<Tile> getNeighbors(Tile tile)
	{
		return getNeighbors(tile.coordinates);
	}
	
	/**
	 * Finds and returns a list of neighbor Tiles to the Tile at the given array position (x,y).
	 * @param x The tile's array x coordinate.
	 * @param y The tile's array y coordinate.
	 * @return The neighboring Tiles in an ArrayList.
	 */
	public List<Tile> getNeighbors(TileCoordinate coordinates)
	{
		List<Tile> neighbors = new ArrayList<Tile>();
		for(int i=0;i<6;i++)
		{
			int neighborx, neighbory;
			// Baserat på kod från http://blog.ruslans.com/2011/02/hexagonal-grid-math.html
			neighborx = (flat?coordinates.x:coordinates.y) + NEIGHBORS_DX[i];					
			neighbory = (flat?coordinates.y:coordinates.x) + NEIGHBORS_DY[(flat?coordinates.x:coordinates.y)%2][i];

			if(neighborx >= 0 && neighborx < (flat?xsize:ysize) && neighbory >= 0 && neighbory < (flat?ysize:xsize))
				neighbors.add(tiles[flat?neighborx:neighbory][flat?neighbory:neighborx]);
		}
		
		return neighbors;
	}
	
	public int getXSize(){
		return this.xsize;
	}
	
	public int getYSize(){
		return this.ysize;
	}
	
	/**
	 * Returns the {@link GTile} positioned at the given coordinate or <code>null</code>
	 * if the given coordinates are out of bounds.
	 * @param coordinate the {@link TileCoordinate} of the tile.
	 * @return the tile at the given position or <code>null</code> if none.
	 */
	public Tile getTile(TileCoordinate coordinate){
		if(coordinate.x < 0 || coordinate.x >= xsize || coordinate.y < 0 || coordinate.y >= ysize){
			return null;
		}
		
		return tiles[coordinate.x][coordinate.y];
	}
	
	
	/**
	 * Sets the building to the given coordinate.
	 * @param tilecoordinate on this coordinates
	 * @param building this building or null to remove the building at these coordinates.
	 */
	public void setBuilding(TileCoordinate tilecoordinate, Building building) {
		// Remove existing building on same tile
		Building existing = getBuildingByTile(tilecoordinate);
		if(existing != null) buildingList.remove(existing.id);
		
		if(building != null){
			buildingList.put(building.id, building);
			building.tileCoordinate = tiles[tilecoordinate.x][tilecoordinate.y].coordinates;
		}
	}
	
	/**
	 * Searches for and returns the building that is the players castle.
	 * @param playerId the players whose castle you are searching for
	 * @return the building, null if no building was found
	 */
	public Building getPlayersCastle(int playerId) {
		for(Building b : buildingList.values()) {
			if (b.type == Building.TYPE_CASTLE && b.controllerId == playerId)
				return b;
		}
		return null;
	}
	
	/**
	 * Gets the building (if any) placed on the given tile
	 * @param tile Tile to retrieve building from
	 * @return A Building if tile contains any, else null
	 */
	public Building getBuildingByTile(TileCoordinate tileCoordinates)
	{
		for(Building b : buildingList.values())
			if(b.tileCoordinate.equals(tileCoordinates)) return b;
		
		return null;
	}
	
	/**
	 * Searches for castles belonging to different players in the map.
	 * @return The number of castles.
	 */
	public int getCastleCount()
	{
		int castleCount = 0;
		
		for(int i=0;i<4;i++)
		{
			if(getPlayersCastle(i) != null)
				castleCount++;
		}
		
		return castleCount;
	}
	
	/**
	 * Checks if the size is valid.
	 * @param size The size to check.
	 * @return -1 represents too small, 1 represents too big, 0 represents OK.
	 */
	public static int isValidSize(int size){
		if(size < MIN_SIZE) return -1;
		else if(size > MAX_SIZE) return 1;
		else return 0;
	}
	
	private void randomizeMap(Random random){
		for(int i=0;i<tiles.length;i++)
		{
			for(int j=0;j<tiles[i].length;j++)
			{
				TerrainType type;
				switch(random.nextInt()%5){
				case 0:
					type = TerrainType.FOREST;
					break;
				case 1:
					type = TerrainType.DEEP_WATER;
					break;
				case 2:
					type = TerrainType.MOUNTAIN;
					break;
				default:
					type = TerrainType.FIELD;
				/*case 0:
					type = TerrainType.FOREST;
					break;
				case 1:
					type = TerrainType.DEEP_WATER;
					break;
				case 2:
					type = TerrainType.SNOW;
					break;
				case 3:
					type = TerrainType.FIELD;
					break;
				case 4:
					if(random.nextInt()%10 == 1)
						type = TerrainType.VOLCANO;
					else
						type = TerrainType.MOUNTAIN;
					break;
				default:
					type = TerrainType.FIELD;
					*/
				}
				tiles[i][j] = new Tile(new TileCoordinate(i,j),type);
			}
		}
		
		// Place villages in random locations
		int nrVillages = tiles.length * tiles[0].length / 13;  // One building every 13 tiles (number 13 taken from nothing)
		for(int i=0; i<nrVillages; i++){
			int rx = Math.abs(random.nextInt()%tiles.length);    // 0 to width
			int ry = Math.abs(random.nextInt()%tiles[0].length); // 0 to height
			setBuilding(
					new TileCoordinate(rx, ry), 
					new Building(Building.TYPE_VILLAGE, nextBuildingId++, -1));
		}
		
		int distanceFromBorder = (int)Math.sqrt(tiles.length * tiles[0].length)/4;
		if(distanceFromBorder >= tiles.length)
			distanceFromBorder = tiles.length - 1;
		if(distanceFromBorder >= tiles[0].length)
			distanceFromBorder = tiles[0].length - 1;
		TileCoordinate castle1pos = new TileCoordinate(0+distanceFromBorder, 0+distanceFromBorder);
		TileCoordinate castle2pos = new TileCoordinate(tiles.length-distanceFromBorder-1, tiles[0].length-distanceFromBorder-1);
		tiles[castle1pos.x][castle1pos.y].terrain = TerrainType.FIELD; //not sure if needed
		tiles[castle2pos.x][castle2pos.y].terrain = TerrainType.FIELD;
		setBuilding(castle1pos, new Building(Building.TYPE_CASTLE, nextBuildingId++, 0));
		setBuilding(castle2pos, new Building(Building.TYPE_CASTLE, nextBuildingId++, 1));
	}
}