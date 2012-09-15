package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public class HealthBar {
	
	public static float BAR_WIDTH = 103;
	public static float BAR_HEIGHT = 42;
	public static float AP_SIZE = 30;
	public static float DISTANCE_TO_UNIT_CENTER = 15;
	public static float TEXT_SCALE = 0.65f;
	public static float TEXT_Y_OFFSET = 58f;
	public static float TEXT_X_OFFSET = 20f;
	public static float TEXT_X_BUGFIX = 6f;
	
	public int currentAP = 2;
	public int maxAP = 8;
	public int APReg = 4;

	//private final float outlineWidth = 1f;
	protected Sprite newbar, background, outline; //bar
	protected TextureRegion health_tens, health_ones;
	protected float alpha = 1f;
	/**
	 * Center of the GUnit it is attached to
	 */
	protected float x, y; 
	
	/** The value of this bar, between 0 and 1 */
	public float healthPercentage = 1f;
	public int healthRemaining = 0;
	private Rectangle scissors = new Rectangle();
	private Rectangle clipBounds = new Rectangle(x-BAR_WIDTH/2, y+DISTANCE_TO_UNIT_CENTER,BAR_WIDTH,BAR_HEIGHT);
	private Rectangle hax = new Rectangle(-2,-2,2,2);
	
	public HealthBar(){
		newbar = new Sprite(Assets.getTextureRegion("healthbar"));
		newbar.setBounds(0f, 0f, BAR_WIDTH, BAR_HEIGHT);
		newbar.flip(false, true);
		
		//bar = new Sprite(Assets.WHITE);
		//bar.setBounds(0f, 0f, (BAR_WIDTH-AP_SIZE)*health, BAR_HEIGHT);
		
		background = new Sprite(Assets.getTextureRegion("healthbar"));
		//background.setBounds(0f, 0f, BAR_WIDTH-AP_SIZE, BAR_HEIGHT);
		background.setBounds(0f, 0f, BAR_WIDTH, BAR_HEIGHT);
		background.flip(false, true);
		
		//outline = new Sprite(Assets.WHITE);
		//outline.setBounds(0f, 0f, BAR_WIDTH-AP_SIZE + (2f * outlineWidth), BAR_HEIGHT + (2f * outlineWidth));
		
		//setColors(Color.GREEN, new Color(0f, 0.3f, 0f, 1f), Color.BLACK);
	}
	
	/**
	 * Sets the bar colors.
	 * @param fgCol Foreground color.
	 * @param bgCol Background color.
	 * @param olColor Outline color.
	 */
	public void setColors(Color fgCol, Color bgCol, Color olColor)	{
		//bar.setColor(fgCol);
		newbar.setColor(fgCol);
		background.setColor(bgCol);
		//outline.setColor(olColor);
	}
	
	public void drawHealthBar(SpriteBatch batch, float parentAlpha){		
		//outline.draw(batch, alpha * parentAlpha);
		background.draw(batch, alpha * parentAlpha);
		//background.draw(batch, 1f);
		//bar.draw(batch, alpha * parentAlpha);
		
		batch.flush();

		ScissorStack.calculateScissors(GEngine.getInstance().camera, batch.getTransformMatrix(), clipBounds, scissors);
		if(!ScissorStack.pushScissors(scissors))
			ScissorStack.pushScissors(hax);
		
		newbar.draw(batch, alpha * parentAlpha);
		
		batch.flush();
		
		ScissorStack.popScissors();
	}
	
	public void drawAPPie(SpriteBatch batch, float parentAlpha)
	{
		APPieDisplay.draw(x-BAR_WIDTH/2, y+AP_SIZE, AP_SIZE, currentAP, maxAP, APReg, 2, batch, false, parentAlpha);
	}
	
	public void drawHealth(SpriteBatch batch, float parentAlpha)
	{
		Color oldColor = batch.getColor();
		batch.setColor(Color.GREEN);
		
		int healthTens = healthRemaining/10;
		int healthOnes = healthRemaining - healthTens*10;
		
		if(healthTens > 0)
		{
			health_tens = Assets.getTextureRegion("ap-"+healthTens);
			health_ones = Assets.getTextureRegion("ap-"+healthOnes);
			
			batch.draw(health_tens, x+TEXT_X_OFFSET-health_tens.getRegionWidth()*TEXT_SCALE+TEXT_X_BUGFIX*TEXT_SCALE, 
					y+TEXT_Y_OFFSET, 0, 0, health_tens.getRegionWidth(), -health_tens.getRegionHeight(), TEXT_SCALE, TEXT_SCALE, 0f);
			batch.draw(health_ones, x+TEXT_X_OFFSET-TEXT_X_BUGFIX*TEXT_SCALE, y+TEXT_Y_OFFSET, 0, 0, 
					health_ones.getRegionWidth(), -health_ones.getRegionHeight(), TEXT_SCALE, TEXT_SCALE, 0f);
		}
		else if(healthOnes > 0)
		{
			health_ones = Assets.getTextureRegion("ap-"+healthOnes);

			batch.draw(health_ones, x+TEXT_X_OFFSET-health_ones.getRegionWidth()*TEXT_SCALE/2f, y+TEXT_Y_OFFSET, 0, 0, 
					health_ones.getRegionWidth(), -health_ones.getRegionHeight(), TEXT_SCALE, TEXT_SCALE, 0f);
		}
		batch.setColor(oldColor);
	}
	
	/**
	 * 
	 * @param pos center of unit the bar is attached to
	 */
	public void setPosition(Vector2 pos){
		x = pos.x;
		y = pos.y;
		//bar.setPosition(pos.x-BAR_WIDTH/2+AP_SIZE, pos.y+DISTANCE_TO_UNIT_CENTER);
		newbar.setPosition(pos.x-BAR_WIDTH/2, pos.y+DISTANCE_TO_UNIT_CENTER);
		clipBounds.set(x-BAR_WIDTH*healthPercentage/2f,y+DISTANCE_TO_UNIT_CENTER,BAR_WIDTH*healthPercentage,BAR_HEIGHT);
		background.setPosition(pos.x-BAR_WIDTH/2, pos.y+DISTANCE_TO_UNIT_CENTER);
		//outline.setPosition(pos.x-BAR_WIDTH/2+AP_SIZE-outlineWidth, pos.y+DISTANCE_TO_UNIT_CENTER-outlineWidth);
	}

	public void setHealth(float healthPercentage, int healthRemaining){
		this.healthPercentage = healthPercentage;
		this.healthRemaining = healthRemaining;
		clipBounds.set(x-BAR_WIDTH*healthPercentage/2f,y+DISTANCE_TO_UNIT_CENTER,BAR_WIDTH*healthPercentage,BAR_HEIGHT);
		//bar.setBounds(x-BAR_WIDTH/2+AP_SIZE, y+DISTANCE_TO_UNIT_CENTER, (BAR_WIDTH-AP_SIZE)*health, BAR_HEIGHT);
		//newbar.setBounds(x-BAR_WIDTH/2+AP_SIZE, y+DISTANCE_TO_UNIT_CENTER, (BAR_WIDTH-AP_SIZE)*health, BAR_HEIGHT);
	}
	
}
