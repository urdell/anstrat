package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FancyNumbers {

	public static final float sideIncrement = 0.7f;
	
	public static void drawDamageRange(int min, int max, float x, float y, float size, boolean flipped, SpriteBatch batch){
		drawDamage(min, x, y, size, flipped, batch);
		if(min >= 10)
			x += 2*size*sideIncrement;
		else
			x += size*sideIncrement;
		TextureRegion dash = Assets.getTextureRegion("hurt-dash");
		batch.draw(dash, x, y, size, 	// if flipped, invert height
				flipped ? -size : size);
		x += size*sideIncrement;
		drawDamage(max, x, y, size, flipped, batch);
	}
	
	public static void drawDamage(int number, float x, float y, float size, boolean flipped, SpriteBatch batch){
		int tens = number/10;
		int ones = number%10;
		
		TextureRegion onesTexture, tensTexture;
		
		if(tens > 0)
		{
			tensTexture = Assets.getTextureRegion("hurt-"+tens);
			batch.draw(tensTexture, x, y, size, 	// if flipped, invert height
					flipped ? -size : size);
			x += size*sideIncrement;
		}
		onesTexture = Assets.getTextureRegion("hurt-"+ones);
		batch.draw(onesTexture, x, y, size, 	// if flipped, invert height
				flipped ? -size : size);
		
	}
}
