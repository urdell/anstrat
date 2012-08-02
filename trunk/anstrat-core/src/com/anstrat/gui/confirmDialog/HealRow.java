package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.anstrat.gui.FancyNumbers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HealRow extends ConfirmRow {

	TextureRegion heart = Assets.getTextureRegion("hp");
	int startHealth, endHealth;
	
	public HealRow(int startHealth, int endHealth){
		this.startHealth = startHealth;
		this.endHealth = endHealth;
	}
	
	@Override
	public void draw(float x, float y, SpriteBatch batch) {
		
		batch.draw(heart, x+ROW_HEIGHT*0.0f, y, ROW_HEIGHT, ROW_HEIGHT);
		batch.setColor(Color.GREEN);

		FancyNumbers.drawValueDecrement(startHealth, endHealth, x+ROW_HEIGHT*0.9f, y+ROW_HEIGHT*0.2f, ROW_HEIGHT*0.7f, false, batch);
		batch.setColor(Color.WHITE);
	}

}
