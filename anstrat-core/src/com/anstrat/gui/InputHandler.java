package com.anstrat.gui;

import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
			
			if(gEngine.actionHandler.showingConfirmDialog)
				if(handleConfirmDialog(x, y))	// Hit one of the buttons on the confirm dialog -> do not continue
					return false;

			// If a tile is hit, delegate the tap as a tile click event
			GMap map = gEngine.getMap();
			GTile gTile = map.getTile(map.coordinate(new Vector2(x, y)));
			if(gTile != null) gEngine.actionHandler.click(gTile, getQuadrant(x, y));
		}
				
		return false;
	}
	
	/**
	 * returns the clicked quadrant by ConfirmDialog constants.
	 * @param x
	 * @param y
	 * @return
	 */
	public int getQuadrant(int x, int y){
		GEngine gEngine = GEngine.getInstance();
		Vector3 out = new Vector3();
		CameraUtil.windowToCameraCoordinates(gEngine.uiCamera, new Vector2(x, y), out);
		if(out.x < Gdx.graphics.getWidth()/2){ // left
			if(out.y > Gdx.graphics.getHeight()/2) // top
				return ConfirmDialog.TOP_LEFT;
			else //bottom
				return ConfirmDialog.BOTTOM_LEFT;
		}
		else{ //right
			if(out.y > Gdx.graphics.getHeight()/2) // top
				return ConfirmDialog.TOP_RIGHT;
			else //bottom
				return ConfirmDialog.BOTTOM_RIGHT;
		}
	}
	
	/**
	 * 
	 * @return true if hit the dialog
	 */
	private boolean handleConfirmDialog(int x, int y){
		GEngine gEngine = GEngine.getInstance();
		Vector3 out = new Vector3();
		CameraUtil.windowToCameraCoordinates(gEngine.uiCamera, new Vector2(x, y), out);
		if(gEngine.confirmDialog.showingConfirmButtons){
			if(gEngine.confirmDialog.okBounds.contains(out.x, out.y)){
				gEngine.actionHandler.confirmPress();
				return true;
			}
			if(gEngine.confirmDialog.cancelBounds.contains(out.x, out.y)){
				gEngine.actionHandler.confirmCancelPress();
				return true;
			}
		}
		else{	// If no button, then entire dialogBox confirms
			if(gEngine.confirmDialog.dialogBounds.contains(out.x, out.y)){
				gEngine.actionHandler.confirmPress();
				return true;
			}
		}
		return false;
	}
}
