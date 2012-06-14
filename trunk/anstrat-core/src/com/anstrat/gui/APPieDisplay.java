package com.anstrat.gui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * 
 * @author Anton
 * All occurances access this class in a static way to avoid multiple instances.
 */
public class APPieDisplay {
	
	
	/**
	 * Draws a pie-chart for displaying ap.
	 * First version may only support drawing on the map (Y is downwards)
	 * @param x top left corner
	 * @param y top left corner
	 * @param width
	 * @param height
	 * @param currentAP current ap of the display
	 * @param maxAP maximum ap the display can show
	 * @param apReg how much ap is regenerated next turn (use for displaying this to user)
	 * @param attackCost used to display if the unit can attack with this amount of AP
	 * @param batch
	 */
	public static void draw(float x, float y, float width, float height, 
			int currentAP, int maxAP, int apReg, int nextAttackCost, SpriteBatch batch){
		/* pseudocode 
		 * drawBackground();
		 * drawNextTurnAP(); // what is regenerated next turn. Gray
		 * drawCurrentAP(); // Teal
		 * drawOverlay(); // Black borders for entire pie
		 * drawAttackArrow(); // Small red arrow pointing at attack cost. Do not draw if 0
		 * */
		
		
		
	}

}
