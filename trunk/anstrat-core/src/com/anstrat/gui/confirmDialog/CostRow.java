package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.anstrat.gui.FancyNumbers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CostRow extends ConfirmRow {
	
	TextureRegion resource;
	int currentValue;
	int endValue;
	boolean isGold;
	Color manaColor = new Color(0.5f, 0.5f, 1f, 1f);
	
	/**
	 * 
	 * @param currentValue
	 * @param cost
	 * @param isGold true for gold, otherwise mana.
	 */
	public CostRow(int currentValue, int cost, boolean isGold){
		this.currentValue = currentValue;
		this.isGold = isGold;
		endValue = currentValue-cost;
		if(isGold){
			resource = Assets.getTextureRegion("gold");
		}
		else{
			resource = Assets.getTextureRegion("mana");
		}
	}

	@Override
	public void draw(float x, float y, SpriteBatch batch) {
		
		batch.draw(resource, x+ROW_HEIGHT*0.0f, y, ROW_HEIGHT, ROW_HEIGHT);
		if(isGold)
			batch.setColor(Color.YELLOW);
		else
			batch.setColor(manaColor);
		FancyNumbers.drawValueDecrement(currentValue, endValue, x+ROW_HEIGHT*0.9f, y+ROW_HEIGHT*0.2f, ROW_HEIGHT*0.7f, false, batch);
		batch.setColor(Color.WHITE);
	}

}
