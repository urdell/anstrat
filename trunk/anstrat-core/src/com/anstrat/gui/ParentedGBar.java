package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * A GBar that is attached to a {@link Sprite}, e.g a health bar.
 */
public class ParentedGBar extends GBar {

	private static final float WIDTH = 0.5f;	// Bar width as fraction of a tile's width
	private static final float HEIGHT = 0.07f;	// Bar height as fraction of a tile's height
	
	private static final float HEXAGON_SIDE_LENGTH = 12;
	
	private float xRelativeOrigin, yRelativeOrigin;
	
	public String text;
	private Sprite originParent;
	private Vector2 barTopLeftCache;
	private boolean drawBarText = true;
	
	/**
	 * @param originParent the sprite this bar should attach to, determines scale and location
	 */
	public ParentedGBar(Sprite originParent){
		// Adjust scale and size to tile size, keep the relative sizes regardless of pixel density
		super(
				(GMap.TILE_WIDTH * WIDTH) / originParent.getScaleX(),
				(GMap.TILE_HEIGHT * HEIGHT) / originParent.getScaleY(),
				originParent.getScaleX());
		
		this.originParent = originParent;
		update();
	}
	
	public void setDrawBarText(boolean flag){
		this.drawBarText = flag;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		super.draw(batch, parentAlpha);
		batch.flush();
		
		if(drawBarText){
			
			// Render text
			if(text != null){
				// Get the coordinates of the left edge of the bar
				Assets.STANDARD_FONT.setScale(0.4f, 0.3f);
				TextBounds bounds = Assets.STANDARD_FONT.getBounds(text);
				float fontX, fontY;
				
				if(!isVertical){
					fontX = barTopLeftCache.x - bounds.width;
					fontY = barTopLeftCache.y - bounds.height / 2f + height * scaleY / 2f;
				}
				else{
					fontX = barTopLeftCache.x - bounds.width / 2f + height * scaleY / 2f;
					fontY = barTopLeftCache.y - bounds.height;
				}
				
				Assets.STANDARD_FONT.setColor(1f, 1f, 1f, alpha);
				Assets.STANDARD_FONT.draw(batch, text, fontX, fontY);		
			}
			
			float h = (float)Math.sin(Math.toRadians(30));
			float hexX = isVertical ? barTopLeftCache.x + height * scaleX / 2f : barTopLeftCache.x - h * 12;
			float hexY = isVertical ? barTopLeftCache.y - HEXAGON_SIDE_LENGTH / 2f : barTopLeftCache.y + height * scaleY / 2f;
			
			// Render hexagon icon
			GL10 gl = Gdx.gl10;
			gl.glColor4f(0f, 0f, 0f, 1f);	// Black
			gl.glPushMatrix();
			
			gl.glTranslatef(hexX, hexY, 0f);
			gl.glScalef(12f, 12f, 12f);
			
			if(isVertical) gl.glRotatef(90, 0f, 0f, 1f);
			
			// Any terrain type hexagon will do, as we're not using a texture
			Assets.terrainMeshes[0].render(GL10.GL_TRIANGLES, 0, 12);
			
			gl.glPopMatrix();
		}
	}
	
	public void update(){
		float centeringOffsetX = (-(isVertical ? height : width) / 2f) * scaleX;
		float centeringOffsetY = (-(isVertical ? width : height) / 2f) * scaleY;
		this.x = originParent.getX() + originParent.getOriginX() + xRelativeOrigin + centeringOffsetX;
		this.y = originParent.getY() + originParent.getOriginY() + yRelativeOrigin + centeringOffsetY;
		
		update(x, y);
		barTopLeftCache = getBarTopLeftCoordinates();
	}
	
	/**
	 * The x,y values represent the location of the bar's center relative
	 * to the parent sprite's origin.
	 * For example, if the origin is in the middle of the sprite, x=0 and y=0 would place
	 * the bar's center in the middle of the sprite.
	 * @param x
	 * @param y
	 */
	public void setPositionRelativeOrigin(float x, float y){
		// Set origins to the center of the parent
		this.xRelativeOrigin = x;
		this.yRelativeOrigin = y;
		
		bar.setOrigin(-x, -y);
		background.setOrigin(-x, -y);
		outline.setOrigin(-x, -y);
	}
	
	// Returns the top left coordinates of the bar, which is a 
	// bit complicated due to that we've scaled it with an origin not in the center of the sprite
	private Vector2 getBarTopLeftCoordinates(){
		
		// From the getVertices() method in Sprite
		float localX = -outline.getOriginX();
		float localY = -outline.getOriginY();
		float worldOriginX = outline.getX() - localX;
		float worldOriginY = outline.getY() - localY;
		localX *= outline.getScaleX();
		localY *= outline.getScaleY();
		
		return new Vector2(localX + worldOriginX, localY + worldOriginY);
	}
}
