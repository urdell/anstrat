package com.anstrat.gameCore;

import com.anstrat.geography.Tile;


public abstract class StateVerifier {

	
	/**
	 * Prints any errors found in the given gamestate s.
	 * Does not modify anything.
	 * @param s
	 */
	
	// TODO fix 
	@Deprecated
	public static void verifyState(State s){
		
		boolean intactState = true;
		for(Tile[] tRow : s.map.tiles){
			for(Tile t : tRow){
				if(StateUtils.getUnitByTile(t.coordinates) != null){
					if(StateUtils.getUnitByTile(t.coordinates).tileCoordinate != t.coordinates){
						intactState = false;
						System.err.println("Found inconsistency in units and tiles not referencing each other!");
					}
				}
			}
		}
		
		for(Unit u : s.unitList.values()){
			if(u.tileCoordinate != null){
				if(StateUtils.getUnitByTile(u.tileCoordinate) != u){
					intactState = false;
					System.err.println("Found inconsistency in units and tiles not referencing each other!");
				}
			}
		}
		
		if(intactState){
			System.out.println("State appears to be intact");
		}
		else{
			System.out.println("WARNING: Found error in State. State is not intact");
		}
			
		
	}
}
