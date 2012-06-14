package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class HealthBar {
	
	public static float BAR_WIDTH = 85;
	public static float BAR_HEIGHT = 8;
	public static float AP_SIZE = 30;
	public static float DISTANCE_TO_UNIT_CENTER = 40;
	
	public int currentAP = 2;
	public int maxAP = 8;
	public int APReg = 4;

	private final float outlineWidth = 1f;
	protected Sprite bar, background, outline;
	protected float alpha = 1f;
	/**
	 * Center of the GUnit it is attached to
	 */
	protected float x, y; 
	
	/** The value of this bar, between 0 and 1 */
	public float health = 1f;
	
	public HealthBar(){
		
		bar = new Sprite(Assets.WHITE);
		bar.setBounds(0f, 0f, (BAR_WIDTH-AP_SIZE)*health, BAR_HEIGHT);
		
		background = new Sprite(Assets.WHITE);
		background.setBounds(0f, 0f, BAR_WIDTH-AP_SIZE, BAR_HEIGHT);
		
		outline = new Sprite(Assets.WHITE);
		outline.setBounds(0f, 0f, BAR_WIDTH-AP_SIZE + (2f * outlineWidth), BAR_HEIGHT + (2f * outlineWidth));
		
		setColors(Color.GREEN, new Color(0f, 0.3f, 0f, 1f), Color.BLACK);
	}
	
	/**
	 * Sets the bar colors.
	 * @param fgCol Foreground color.
	 * @param bgCol Background color.
	 * @param olColor Outline color.
	 */
	public void setColors(Color fgCol, Color bgCol, Color olColor)	{
		bar.setColor(fgCol);
		background.setColor(bgCol);
		outline.setColor(olColor);
	}
	
	public void draw(SpriteBatch batch, float parentAlpha){
		
		outline.draw(batch, alpha * parentAlpha);
		background.draw(batch, alpha * parentAlpha);
		bar.draw(batch, alpha * parentAlpha);
		
		APPieDisplay.draw(x-BAR_WIDTH/2, y+AP_SIZE, AP_SIZE, currentAP, maxAP, APReg, 2, batch);
	}
	/**
	 * 
	 * @param pos center of unit the bar is attached to
	 */
	public void setPosition(Vector2 pos){
		x = pos.x;
		y = pos.y;
		bar.setPosition(pos.x-BAR_WIDTH/2+AP_SIZE, pos.y+DISTANCE_TO_UNIT_CENTER);
		background.setPosition(pos.x-BAR_WIDTH/2+AP_SIZE, pos.y+DISTANCE_TO_UNIT_CENTER);
		outline.setPosition(pos.x-BAR_WIDTH/2+AP_SIZE-outlineWidth, pos.y+DISTANCE_TO_UNIT_CENTER-outlineWidth);
	}
	public void setHealth(float health){
		this.health = health;
		bar.setBounds(x-BAR_WIDTH/2+AP_SIZE, y+DISTANCE_TO_UNIT_CENTER, (BAR_WIDTH-AP_SIZE)*health, BAR_HEIGHT);
	}
	
}
