package com.anstrat.mapEditor;

import com.anstrat.gui.GMap;
import com.anstrat.gui.GTile;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;

public class MapEditorInputHandler extends GestureAdapter {

	@Override
	public boolean tap(int x, int y, int count) {
		
		MapEditor mapEditor = MapEditor.getInstance();
		
		if(!mapEditor.userInterface.tap(x, y, count)){// UI handles this input if it's hit
			
			if(mapEditor.userInterface.panelTable.visible){
				mapEditor.userInterface.showPanel(null, false);
				return false;
			}

			GMap map = mapEditor.gMap;
			GTile gTile = map.getTile(map.coordinate(new Vector2(x, y)));
			
			if(gTile!=null)
				mapEditor.actionHandler.click(gTile);
			
		}
				
		return false;
	}
}
