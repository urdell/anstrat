package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

@Deprecated
/**
 * A basic graphical bar for displaying a value.
 * @author Erik
 *
 */
public class GBar extends Widget {
	
	private final float outlineWidth = 1f;
	protected Sprite bar, background, outline;
	protected float alpha = 1f;
	protected float barWidth, barHeight;
	
	/** The value of this bar, between 0 and 1 */
	protected float value = 1f;
	
	protected boolean isVertical;
	
	public GBar(float barWidth, float barHeight, float scale){
		
		// Bar width
		this.barWidth = barWidth;
		this.barHeight = barHeight;
		this.setScale(scale);
		
		bar = new Sprite(Assets.WHITE);
		bar.setBounds(0f, 0f, barWidth, barHeight);
		
		background = new Sprite(Assets.WHITE);
		background.setBounds(0f, 0f, barWidth, barHeight);
		
		outline = new Sprite(Assets.WHITE);
		outline.setBounds(0f, 0f, barWidth + (2f * outlineWidth) / this.getScaleX(), barHeight + (2f * outlineWidth) / this.getScaleY());
		
		bar.setScale(scale);
		background.setScale(scale);
		outline.setScale(scale);
		
		this.setWidth(outline.getWidth());
		this.setHeight(outline.getHeight());
		
		
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
	
	public void render(SpriteBatch batch){
		draw(batch, 1f);
	}
	
	public void draw(SpriteBatch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		
		outline.draw(batch, alpha * parentAlpha);
		background.draw(batch, alpha * parentAlpha);
		bar.draw(batch, alpha * parentAlpha);
	}
	
	@Override
	public void layout() {
		update();
		if(this.getParent() != null) super.layout();
	}
	
	/**
	 * @param x the x coordinate of the outline's upper-left corner
	 * @param y the y coordinate of the outline's upper-left corner
	 */
	protected void update(float x, float y){
		
		float outlineWidthX = outlineWidth * this.getScaleX();
		float outlineWidthY = outlineWidth * this.getScaleY();
		
		// Update bar position
		background.setPosition(x + outlineWidthX, y + outlineWidthY);
		outline.setPosition(x, y);
		
		// Update bar value
		float currentBarWidth = isVertical ? barHeight : barWidth * value;
		float currentBarHeight = isVertical ? barWidth * value : barHeight;
		bar.setBounds(x + outlineWidthX, (isVertical ? y + (1 - value) * barWidth * this.getScaleY() : y) + outlineWidthY, currentBarWidth, currentBarHeight);
	}
	
	/**
	 * @param value a value between 0 and 1.
	 */
	public void setValue(float value){
		this.value = value;
		update();
		layout();
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
	}
	
	public void setIsVertical(boolean flag){
		this.isVertical = flag;

		// Size of bar will be set in update(), as it depends on the current value
		background.setSize(flag ? barHeight : barWidth, flag ? barWidth : barHeight);
		//outline.setSize(flag ? height : width, flag ? width : height);
		outline.setSize(flag ? getHeight() : getWidth(), flag ? getWidth() : getHeight());
		
		update();
	}
	
	public float getHeight(){
		return super.getHeight();
	}
	
	public float getWidth(){
		return super.getWidth();
	}
	
	/** Updates the position and size of the bar, call this when the position of its parent has changed */
	public void update(){
		Vector2 aa = new Vector2(this.getX()+this.getOriginX(),this.getY()+this.getOriginY());
		//this.stageToScreenCoordinates(aa);
		this.localToStageCoordinates(aa);
		update(aa.x, aa.y);
		
		System.out.println("COORD: "+aa.x +","+ aa.y);
	}
}
