package com.anstrat.geography;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.ActionMap;
import com.anstrat.gui.GEngine;

public final class Pathfinding {
	
	public static HashMap<Unit, List<TileCoordinate>> ranges = new HashMap<Unit, List<TileCoordinate>>();
	
	/**
	 * Plain shortest path without any regard to terrain.
	 * Uses the three-axis system explained at:
	 *  http://keekerdc.com/2011/03/hexagon-grids-coordinate-systems-and-distance-calculations/
	 * @author Kalle
	 * @param startPos
	 * @param endPos
	 * @return
	 */
	public static int getDistance(TileCoordinate startPos, TileCoordinate endPos){
		boolean flat = GEngine.FLAT_TILE_ORIENTATION;
		
		// Adapted from the version below to support both flat and pointy map hexagon orientation
		int startPosX = flat ? startPos.x : startPos.y;
		int startPosY = flat ? startPos.y : startPos.x;
		int endPosX = flat ? endPos.x : endPos.y;
		int endPosY = flat ? endPos.y : endPos.x;
		
		int start = startPosX % 2 == 0 ? startPosY - startPosX / 2 : startPosY - (startPosX - 1) / 2;
		int end = endPosX % 2 == 0 ? endPosY - endPosX / 2 : endPosY - (endPosX - 1) / 2;
		
		int zStart = 0 - startPosX - start;
		int zEnd   = 0 - endPosX - end;
		
		int dx = Math.abs(flat ? (startPos.x - endPos.x) : start - end);
		int dy = Math.abs(flat ? start - end : startPos.y - endPos.y);
		int dz = Math.abs(zStart-zEnd);
		
		return Math.max(dz, Math.max(dx, dy));
		
		/*
		//Convert y-coordinates to "diagonal y" coordinates, by shifting them down based on x coordinates 
		int yStart = startPos.x%2==0 ? startPos.y-startPos.x/2 : startPos.y-(startPos.x-1)/2;
		int yEnd   = endPos.x%2==0 ? endPos.y-endPos.x/2 : endPos.y-(endPos.x-1)/2;
		
		//Calculate z-axis coordinates
		int zStart = 0-startPos.x-yStart;
		int zEnd   = 0-endPos.x-yEnd;
		
		int dx = Math.abs(startPos.x-endPos.x);
		int dy = Math.abs(yStart-yEnd);
		int dz = Math.abs(zStart-zEnd);
		
		return Math.max(dz, Math.max(dx, dy));
		*/
	}
	
	/**
	 * 
	 * @param startPos
	 * @param endPos
	 * @return the Path to the given endPos.
	 */
	public static Path getPath(TileCoordinate startPos, TileCoordinate endPos){
		return new Path(calc(null, startPos, endPos, null));
	}
	
	/**
	 * 
	 * @param startPos
	 * @param endPos
	 * @return the Path to the given endPos.
	 */
	public static Path getPath(TileCoordinate startPos, TileCoordinate endPos, Map map){
		return new Path(calc(map, null, startPos, endPos, null));
	}
	
	
	
	/**
	 * Calculates the range of a unit.
	 * @author Kalle
	 * @param unit The unit
	 * @return List of tiles within range of the unit 
	 */	
	public static List<TileCoordinate> getUnitRange(Unit unit)
	{
		if(!ranges.containsKey(unit))
			ranges.put(unit,calc(unit, null, null, null));
		return ranges.get(unit);
	}
	
	/**
	 * Returns the path from a unit to the specified target tile.
	 * @param unit The start point.
	 * @param target target tile.
	 * @return A list of all tiles in the path. In order.
	 */
	public static Path getUnitPath(Unit unit, TileCoordinate target)
	{
		return new Path(calc(unit, null, target, null));
	}
	
	/**
	 * Returns the path from a unit to the specified target tile.
	 * @param unit The start point.
	 * @param target target tile.
	 * @return A list of all tiles in the path. In order.
	 */
	public static Path getUnitPath(Unit unit, TileCoordinate target, Map map)
	{
		return new Path(calc(map, unit, null, target, null));
	}
	
	/**
	 * Returns the path from a unit to the specified target tile.
	 * @param unit The start point.
	 * @param target target tile.
	 * @return A list of all tiles in the path. In order.
	 */
	public static Path updateActionMapMoves(Unit unit, ActionMap targetMoveStorage)
	{
		return new Path(calc(unit, null, null, targetMoveStorage));
	}
	
	
	/**
	 * Uses Dijkstra's algorithm to calculate paths/unit ranges.
	 * 
	 * @param unit
	 * @param start
	 * @param target
	 * @return
	 */
	private static List<TileCoordinate> calc(Unit unit, TileCoordinate start, TileCoordinate target, ActionMap targetMoveStorage)
	{
		return calc(null, unit, start, target, targetMoveStorage);
	}
	
	/**
	 * Uses Dijkstra's algorithm to calculate paths/unit ranges.
	 * 
	 * @param unit
	 * @param start
	 * @param target
	 * @return
	 */
	private static List<TileCoordinate> calc(Map map, Unit unit, TileCoordinate start, TileCoordinate target, ActionMap targetMoveStorage)
	{
		final HashMap<TileCoordinate, Integer> distances = new HashMap<TileCoordinate, Integer>();
		HashMap<TileCoordinate, TileCoordinate> previous = new HashMap<TileCoordinate, TileCoordinate>();
		ArrayList<TileCoordinate> visited = new ArrayList<TileCoordinate>();
		
		
		//Keep unsettled nodes in a priority queue so we can easily get the closest.
		PriorityQueue<TileCoordinate> unsettled = new PriorityQueue<TileCoordinate>(6, new Comparator<TileCoordinate>(){
	        public int compare(TileCoordinate a, TileCoordinate b) {
	            return notNull(distances.get(a)) > notNull(distances.get(b)) ? +1 : -1;
	        }
	    });
		boolean isRange = unit!=null && target==null;
		boolean isPath  = target!=null;
		boolean isNoState = map != null;
		//If calculating from unit, let its tile be the source.
		TileCoordinate source = unit!=null ? unit.tileCoordinate : start;
		if (!isNoState)
			map = State.activeState.map;
		// Return null if invalid path
		if(isPath && !isNoState && (StateUtils.getUnitByTile(target)!=null || target.equals(source)))
			return null;
		
		//Add starting tile
		distances.put(source, 0);
		unsettled.add(source);
		
		while(!unsettled.isEmpty())
		{
			TileCoordinate closest = unsettled.poll();
			
			//If we have a target and we found it.
			if(isPath && closest.equals(target))
				break;
			
			visited.add(closest);
			
			//Add neighbors
			for(Tile neighbor : map.getNeighbors(closest))
			{
				Unit unitOnNeighbor = null;
				if (!isNoState) 
					unitOnNeighbor = StateUtils.getUnitByTile(neighbor.coordinates);
				int terrainPenalty = unit != null ? unit.getUnitType().getTerrainPenalty(neighbor.terrain) : Integer.MAX_VALUE;
				int distance = Math.max(notNull(distances.get(closest)) + terrainPenalty, terrainPenalty);
				
				// Enemy unit on neighboring tile? Don't add that tile's neighbors in the future.
				if(unit != null && unitOnNeighbor != null && unitOnNeighbor.ownerId != unit.ownerId){

					unsettled.remove(neighbor.coordinates);
					
					if(distance <= unit.currentAP){
						visited.add(neighbor.coordinates);		//So we can still mark the unit later
					}
					continue;
				}
				
				// Unit can't go there? Skip.
				if(isRange && (distance > unit.currentAP ))
					continue;
				
				//Shorter distance? Add/update data.
				if(distance < notNull(distances.get(neighbor.coordinates)))
				{
					distances.put(neighbor.coordinates, distance);
					previous.put(neighbor.coordinates, closest);
					
					
					// Reorder v in the Queue
					unsettled.remove(neighbor.coordinates);
					unsettled.add(neighbor.coordinates);
				}
			}
		}
		
		if(targetMoveStorage != null){
			for(Entry<TileCoordinate, Integer> entry : distances.entrySet())
				targetMoveStorage.setAction(entry.getKey(), ActionMap.ACTION_MOVE, entry.getValue());
		}
		
		//If we were looking for a path, reuse visited and put path info there instead
		if(isPath){
			visited.clear();
			visited.add(target);
			for(TileCoordinate tc = previous.get(target); tc!=null && !tc.equals(source); tc = previous.get(tc))
				visited.add(0,tc);
			visited.remove(source);
		}
		return visited;
	}
	
	/**
	 * Replaces null with Integer.MAX_VALUE
	 * Used by the main pathfinding algorithm.
	 * @param i The {@link Integer} to check
	 * @return
	 */
	private static final Integer notNull(Integer i)
	{
    	return i==null ? Integer.MAX_VALUE : i;
	}
}
