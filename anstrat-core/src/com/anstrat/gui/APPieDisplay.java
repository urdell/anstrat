package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * 
 * @author Anton
 * All occurances access this class in a static way to avoid multiple instances.
 */
public class APPieDisplay {
	
	public static boolean ONLY_USE_NUMBER_8 = false;
	
	
	/**
	 * 
	 * Draws a pie-chart for displaying ap.
	 * First version may only support drawing on the map (Y is downwards)
	 * @param x top left corner
	 * @param y top left corner
	 * @param size
	 * @param currentAP current ap of the display
	 * @param maxAP maximum ap the display can show
	 * @param apReg how much ap is regenerated next turn (use for displaying this to user)
	 * @param attackCost used to display if the unit can attack with this amount of AP
	 * @param batch
	 * @param UI if the flipped ui is calling the method
	 */
	public static void draw(float x, float y, float size, 
			int currentAP, int maxAP, int apReg, int nextAttackCost, SpriteBatch batch, boolean UI, float alpha){
		
		if(ONLY_USE_NUMBER_8)
			maxAP = 8;
		
		//batch.setColor(Color.toFloatBits(1f, 1f, 1f, 1f));
		//float alpha = batch.getColor().a;
		
		Color c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, alpha);
		TextureRegion background = Assets.getTextureRegion("APPie-bg");
		batch.draw(background, x, y, size/2, size/2, size, size, 1f, 1f, 0f);
		
		int pieceNumber;
		batch.setColor(0f, 1f, 1f, alpha);
		c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, alpha);
		for(pieceNumber=0; pieceNumber < currentAP; pieceNumber++ ){
			drawPiece(x, y, size, maxAP, pieceNumber, batch, UI);
		}
		batch.setColor(Color.toFloatBits(0.0f, 0.3f, 0.3f, alpha));
		c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, alpha);
		for(;pieceNumber < currentAP+apReg && pieceNumber < maxAP; pieceNumber++ ){
			drawPiece(x, y, size, maxAP, pieceNumber, batch, UI);
		}
		batch.setColor(1f, 1f, 1f, alpha);
		c = batch.getColor();
		batch.setColor(c.r, c.g, c.b, alpha);
		
		TextureRegion foreground = Assets.getTextureRegion("APPie-front-"+maxAP);
		float frontRotation = 180f;
		if(UI)
			frontRotation = 0;
		batch.draw(foreground, x, y, size/2, size/2, size, size, 1f, 1f, frontRotation);
		
		//Number modification
		float midCircleSize = size*0.6f;
		batch.draw(background, x+size/2-midCircleSize/2, y+size/2-midCircleSize/2, midCircleSize/2, midCircleSize/2, midCircleSize, midCircleSize, 1f, 1f, frontRotation); // Background for number
		
		c = Color.WHITE;
		batch.setColor(c.r, c.g, c.b, alpha);
		if(UI) // Flipping and position dependant on if in UI
			FancyNumbers.drawApNumber(currentAP, x+size/2-midCircleSize*0.475f, y+midCircleSize*0.38f, midCircleSize*0.95f, false, batch);
		else
			FancyNumbers.drawApNumber(currentAP, x+size/2-midCircleSize*0.475f, y+midCircleSize*1.3f, midCircleSize*0.95f, true, batch);
		
		//End of number modification
		
		
		//batch.dr
		/* pseudocode 
		 * drawBackground();
		 * drawNextTurnAP(); // what is regenerated next turn. Gray
		 * drawCurrentAP(); // Teal
		 * drawOverlay(); // Black borders for entire pie
		 * drawAttackArrow(); // Small red arrow pointing at attack cost. Do not draw if 0
		 * */
	}
	private static void drawPiece(float x, float y, float size, int maxAP, int pieceNumber, SpriteBatch batch, boolean UI){
		TextureRegion piece = Assets.getTextureRegion("APPie-piece-"+maxAP); // change to correct number later
		float rotation = 360/maxAP;
		float initialRotation;
		
		if(UI){
			initialRotation = 0;
			batch.draw(piece, x, y, size/2, size/2, size, size, 1f, 1f, initialRotation - rotation*pieceNumber);
		}
		else{
			initialRotation = 180+rotation;
			batch.draw(piece, x, y, size/2, size/2, size, size, 1f, 1f, initialRotation + rotation*pieceNumber);
		}
	}
}