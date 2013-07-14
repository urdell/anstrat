package com.anstrat.geography;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GTile;
import com.badlogic.gdx.Gdx;

/**
 * The class that holds information about the map, including all the tiles and the size etc.
 * @author Ekis
 *
 */
public class Map implements Serializable{
	
	public static final int MAX_SIZE = 50;
	public static final int MIN_SIZE = 5;
	
	private static final long serialVersionUID = 1L;
	private int xsize, ysize;
	public Tile[][] tiles;
	public String name;
	private static final int[] NEIGHBORS_DX = { 0, 1, 1, 0, -1, -1 };
	private static final int[][] NEIGHBORS_DY = { { -1, -1, 0, 1, 0, -1 }, { -1, 0, 1, 1, 1, 0 } };
	public HashMap<Integer,Building> buildingList;
	public int nextBuildingId;
	
	public boolean fogEnabled = true;

	/** Creates an empty map of the given size filled with the standard terrain type. */
	public Map(int xSize, int ySize){
		this.xsize = xSize;
		this.ysize = ySize;
		tiles = new Tile[xsize][ysize];
		buildingList = new HashMap<Integer,Building>();
		
		// Fill with field terrain
		for(int i=0;i<xsize;i++){
			for(int j=0;j<ysize;j++){
				tiles[i][j] = new Tile(new TileCoordinate(i, j), TerrainType.FIELD);
			}
		}

		name = "Unnamed";
	}
	
	/**
	 * Creates a map of the given size, randomized with the given seed.
	 * @param xSize Number of columns in the map.
	 * @param ySize Number of rows in the map.
	 * @param the random seed used to generate the map, a reference to is not saved.
	 */
	public Map(int xSize, int ySize, Random random){
		this(xSize, ySize);
		// Can't use Gdx here since this code is used by the server too
		// if (xSize > 15 || ySize > 15)
		//	Gdx.app.log("Map", "Warning: Big map, may take a long time to generate (bad o(n2)-algorithms)");
		while (!randomizeFairMap(random));
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
		boolean flat = GEngine.FLAT_TILE_ORIENTATION;
		
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
	
	public static final int NOT_ADJACENT = -1;
	public static final int ADJACENT_SW = 0;
	
	/** Only applicable for flat tiles */
	public static final int ADJACENT_S = 1;
	/** Only applicable for pointy tiles */
	public static final int ADJACENT_E = 1;
	public static final int ADJACENT_SE = 2;
	public static final int ADJACENT_NE = 3;
	/** Only applicable for pointy tiles */
	public static final int ADJACENT_W = 4;
	/** Only applicable for flat tiles */
	public static final int ADJACENT_N = 4;
	public static final int ADJACENT_NW = 5;
	
	private static final int[] FLAT_TO_POINTY = {
		2, // SW -> SE
		1, // S -> E
		3, // SE -> NE
		5, // NE - NW
		4, // N -> W
		0, // NW -> SW
	};
	
	/**
	 * Returns the edge that joins the two tiles from the origin tile's perspective. <br>
	 * Remember that this method is not symmetric, getAdjacentOrientation(t1, t2) is not equal to <br>
	 * getAdjacentOrientation(t2, t1).
	 * @param origin 
	 * @param target
	 * @return
	 */
	public int getAdjacentOrientation(TileCoordinate origin, TileCoordinate target){
		int result = NOT_ADJACENT;
		
		if(!GEngine.FLAT_TILE_ORIENTATION){
			origin = flatToPointy(new TileCoordinate(origin));
			target = flatToPointy(new TileCoordinate(target));
		}
		
		if(origin.x % 2 == 1){		// bottom peak
			if(origin.x - 1 == target.x && origin.y == target.y)   //left
				result =  ADJACENT_NW;
			else if(origin.x == target.x && origin.y - 1 == target.y)   //up
				result = ADJACENT_N;
			else if(origin.x + 1 == target.x && origin.y == target.y)   //right
				result = ADJACENT_NE;
			else if(origin.x - 1 == target.x && origin.y + 1 == target.y)   //left+down
				result = ADJACENT_SW;
			else if(origin.x == target.x && origin.y + 1 == target.y)   //down
				result = ADJACENT_S;
			else if(origin.x + 1 == target.x && origin.y + 1 == target.y)   //right+down
				result = ADJACENT_SE;
		}
		else if(origin.x%2==0){		// top peak
			if(origin.x - 1 == target.x && origin.y == target.y)   //left
				result = ADJACENT_SW;
			else if(origin.x == target.x && origin.y - 1 == target.y)   //up
				result = ADJACENT_N;
			else if(origin.x + 1 == target.x && origin.y == target.y)   //right
				result = ADJACENT_SE;
			else if(origin.x - 1 == target.x && origin.y - 1 == target.y)   //left+up
				result = ADJACENT_NW;
			else if(origin.x == target.x && origin.y + 1 == target.y)   //down
				result = ADJACENT_S;
			else if(origin.x + 1 == target.x && origin.y - 1 == target.y)   //right+up
				result = ADJACENT_NE;
		}
		
		if(result != NOT_ADJACENT && !GEngine.FLAT_TILE_ORIENTATION){
			result = FLAT_TO_POINTY[result];
		}
		
		return result;
	}
	
	/*
	private TileCoordinate pointyToFlat(TileCoordinate t){
		int oldX = t.x;
		t.x = t.y;
		t.y = xsize - oldX - (xsize % 2 == 0 ? 0 : 1);
		return t;
	}
	*/
	
	private TileCoordinate flatToPointy(TileCoordinate t){
		int oldY = t.y;
		t.y = t.x;
		t.x = ysize - oldY - (ysize % 2 == 0 ? 0 : 1);
		return t;
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
	
	private boolean randomizeFairMap(Random random) {
		
		int x = tiles.length;
		int y = tiles[0].length;
		// Can't use Gdx here since this code is used by the server too
		// if (x%2 == 1 || y%2 == 1) {
		//	Gdx.app.log("Map", "Varning: Generating map with odd numbers of x and/or y coordinates: Will likely result in unfair map");
		// }
		//The different parts of the map, first equals fourth, but mirrored, and second equals third but mirrored. Therefore the map will be fair. The castles will be placed in first and fourth. 
		//[first][second]
		//[third][fourth]
		Tile[][] first = new Tile[x/2][y/2];
		randomizeTerrain(random, first);
		Tile[][] second = new Tile[x-first.length][y-first[0].length];
		randomizeTerrain(random, second);
		Tile[][] third = mirror(second);
		Tile[][] fourth = mirror(first);
		placeOutTiles(first, 0,0);
		placeOutTiles(second, first.length,0);
		placeOutTiles(third, 0,first[0].length);
		placeOutTiles(fourth, third.length,second[0].length);
		
		// Place villages in random locations
		int nrVillages = numberOfVillages();	
		for(int i=0; i<nrVillages; i++){
			
			int rx = Math.abs(random.nextInt(tiles.length/2));	//don't know why Math.abs, it's from older code. Surely there must be a reason for it so it's left
			int ry = Math.abs(random.nextInt(tiles[0].length)); 	// 0 to height
			setBuilding(
					new TileCoordinate(rx, ry), 
					new Building(Building.TYPE_VILLAGE, nextBuildingId++, -1));
			setBuilding(
					new TileCoordinate(tiles.length-rx-1, tiles[0].length-ry-1), 
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
		
		setBuilding(castle2pos, new Building(Building.TYPE_CASTLE, nextBuildingId++, 1));
		
		Unit unit = new Unit(UnitType.AXE_THROWER, 1, 1);
		unit.tileCoordinate = castle1pos;

		for(Building b : buildingList.values()) {
			if (Pathfinding.getUnitPath(unit, b.tileCoordinate, this).path.size() < 2) {
				buildingList.clear();
				unit = null;
				return false;
			}
		}
		setBuilding(castle1pos, new Building(Building.TYPE_CASTLE, nextBuildingId++, 0));
		return true;
	}
	
	private int numberOfVillages() {
		int nr = 0;
		
		for(int i = 0, j = 26; (i+j) < tiles.length*tiles[0].length; i += j, j++, nr++); // one village per 26 tiles, 2 for 27 more, 3 for 28 more... 
		
		return nr; 
	}
	
	/*private void randomizeMap(Random random){
		
		randomizeTerrain(random, this.tiles);
		
		
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
		
		setBuilding(castle2pos, new Building(Building.TYPE_CASTLE, nextBuildingId++, 1));
		
		Unit unit = new Unit(UnitType.AXE_THROWER, 1, 1);
		unit.tileCoordinate = castle1pos;

		for(Building b : buildingList.values()) {
			if (Pathfinding.getUnitPath(unit, b.tileCoordinate, this).path.size() < 2) {
				buildingList.clear();
				unit = null;
				randomizeMap(random);
				break;
			}
		}
		setBuilding(castle1pos, new Building(Building.TYPE_CASTLE, nextBuildingId++, 0));
	}
	*/
	
	private void placeOutTiles(Tile[][] t, int startx, int starty) {
		for(int i = 0; i < t.length; i++) {
			for(int j = 0; j < t[0].length; j++) {
				tiles[startx+i][starty+j] = new Tile(new TileCoordinate(startx+i, starty+j), t[i][j].terrain);
			}
		}
	}
	
	private void randomizeTerrain(Random random, Tile[][] tiles) {
		
		int totTiles = tiles.length*tiles[0].length;
		//These are the min values for each terraintype. Total should not be more than 1
		//P.S. make sure you dont set impassable terrain values too high
		double mDeepWater = 0.05, mWater = 0.05, mForest = 0.1, 
				mField = 0.2, mMountain = 0.05, mVulcano = 0.0;
		//Min number of tiles for each terrain
		int nDeepWater = (int) (mDeepWater*totTiles), nWater = (int) (mWater*totTiles),
				nForest = (int) (mForest*totTiles), nField = (int) (mField*totTiles),
				nMountain = (int) (mMountain*totTiles), nVulcano = (int) (mVulcano*totTiles);
		
		int totLeft = totTiles - (nDeepWater+nWater+nForest+nField+nMountain+nVulcano);
		//These are the likeliness they appear more than their min value.
		double rDeepWater = 0.1, rWater = 0.1, rForest = 0.2, rMountain = 0.12, rVulcano= 0.02;
		double rField = 1-(rDeepWater+rWater+rForest+rMountain+rVulcano); //takes what's left
				
		//deciding how much of each terrain there will be
		rDeepWater *=random.nextDouble(); rWater *= random.nextDouble(); 
		rForest *= random.nextDouble(); rMountain *= random.nextDouble(); 
		rVulcano *= random.nextDouble(); rField *= random.nextDouble();
		double rTot = rDeepWater+rWater+rForest+rMountain+rVulcano+rField;
		rDeepWater = rDeepWater / rTot; rWater = rWater/rTot; rForest = rForest/rTot;
		rMountain = rMountain/rTot; rVulcano = rVulcano/rTot;
		nDeepWater += (int) (rDeepWater*totLeft);
		nWater += (int) (rWater*totLeft);
		nForest += (int) (rForest*totLeft);
		nMountain += (int) (rMountain*totLeft);
		nVulcano += (int) (rVulcano*totLeft);
		
		nField = totTiles-(nDeepWater+nWater+nForest+nMountain+nVulcano);
		
		HashMap<TerrainType, Integer> terrains = new HashMap<TerrainType, Integer>();
		terrains.put(TerrainType.DEEP_WATER, nDeepWater);
		terrains.put(TerrainType.FIELD, nField);
		terrains.put(TerrainType.SHALLOW_WATER, nWater);
		terrains.put(TerrainType.FOREST, nForest);
		terrains.put(TerrainType.VOLCANO, nVulcano);
		terrains.put(TerrainType.MOUNTAIN, nMountain);
		
		double neighbourModifier = 0.1;
		for(int i = 0; i < tiles.length; i++) {
			for(int j = 0; j < tiles[0].length; j++) {
				TerrainType type = null;
				double r = random.nextDouble();
				if (i > 0 && j > 0) {
					//neighbouring tile's terraintypes, 
					//affects the chance of current tile to have same terraintype
					TerrainType t1 = tiles[i-1][j-1].terrain;
					TerrainType t2 = tiles[i-1][j].terrain;
					TerrainType t3 = tiles[i][j-1].terrain;
					
					if (r < neighbourModifier) {
						type = t1;
					}
					else if (r < 2*neighbourModifier) {
						type = t2;
					}
					else if (r < 3*neighbourModifier) {
						type = t3;
					}
					else {
						type = getRandomTerrainType(terrains, totTiles, random);
					}
				}
				else if (i > 0) { //first row
					if(r < neighbourModifier) {
						type  = tiles[i-1][j].terrain;
					}
					else {
						type = getRandomTerrainType(terrains, totTiles, random);
					}
				}
				else if (j > 0) { //first column
					if (r < neighbourModifier) {
						type = tiles[i][j-1].terrain;
					}
					else {
						type = getRandomTerrainType(terrains, totTiles, random);
					}
				}
				else { //first tile
					type = getRandomTerrainType(terrains, totTiles, random);
				}
				totTiles--;
				terrains.put(type, terrains.get(type)-1);
				tiles[i][j] = new Tile(new TileCoordinate(i,j),type);
			}
		}
	}
	
	private Tile[][] mirror(Tile[][] tiles) {
		
		int x = tiles.length;
		int y = tiles[0].length;
		Tile[][] ret = new Tile[x][y];
		for(int i = 0; i < x; i++) {
			for(int j = 0; j < y; j++) {
				ret[x-i-1][y-j-1] = tiles [i][j];
			}
		}
		return ret;
	}
	
	private TerrainType getRandomTerrainType(HashMap<TerrainType, Integer> terrains, int totTiles, Random random) {
		
		TerrainType type;
		
		int r = random.nextInt(totTiles+1);
		int total = terrains.get(TerrainType.VOLCANO);
		if (r < total) {
			type = TerrainType.VOLCANO;
		}
		else if (r < (total += (int)terrains.get(TerrainType.SHALLOW_WATER))) {
			type = TerrainType.SHALLOW_WATER;
		}
		else if (r < (total += (int)terrains.get(TerrainType.DEEP_WATER))) {
			type = TerrainType.DEEP_WATER;
		}
		else if (r < (total += (int)terrains.get(TerrainType.FOREST))) {
			type = TerrainType.FOREST;
		}
		else if (r < (total += (int)terrains.get(TerrainType.MOUNTAIN))) {
			type = TerrainType.MOUNTAIN;
		}
		else {
			type = TerrainType.FIELD;
		}
		
		return type;
	}
	
	
}