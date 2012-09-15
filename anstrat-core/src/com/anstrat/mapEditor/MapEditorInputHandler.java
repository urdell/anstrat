package com.anstrat.mapEditor;

import com.anstrat.gui.GMap;
import com.anstrat.gui.GTile;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;

public class MapEditorInputHandler extends GestureAdapter {

	@Override
	public boolean tap(float x, float y, int count, int button) {
		
		MapEditor mapEditor = MapEditor.getInstance();
		
		if(!mapEditor.userInterface.tap(x, y, count, button)){// UI handles this input if it's hit
			
			if(mapEditor.userInterface.panelTable.isVisible()){
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
