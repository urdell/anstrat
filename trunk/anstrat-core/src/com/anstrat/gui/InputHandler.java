package com.anstrat.gui;

import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;

/**
 * 
 * Takes 'raw' input and delegates to UI, ActionHandler etc.
 *
 */
public class InputHandler extends GestureAdapter {
	
	@Override
	public boolean tap(int x, int y, int count) {
		
		GEngine gEngine = GEngine.getInstance();
		
		// UI handles this input if it's hit
		if(!gEngine.userInterface.tap(x, y, count)){

			// If a tile is hit, delegate the tap as a tile click event
			GMap map = gEngine.getMap();
			GTile gTile = map.getTile(map.coordinate(new Vector2(x, y)));
			if(gTile != null) gEngine.actionHandler.click(gTile);
		}
				
		return false;
	}
}
