package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class CostRow extends ConfirmRow {
	
	TextureRegion resource;
	int currentValue;
	int endValue;
	
	/**
	 * 
	 * @param currentValue
	 * @param cost
	 * @param isGold true for gold, otherwise mana.
	 */
	public CostRow(int currentValue, int cost, boolean isGold){
		this.currentValue = currentValue;
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
		TextureRegion arrow = Assets.getTextureRegion("rightArrow");
		
		batch.draw(resource, x+ROW_HEIGHT*0.0f, y, ROW_HEIGHT, ROW_HEIGHT);
		batch.draw(arrow, x+ROW_HEIGHT*2f, y, ROW_HEIGHT, ROW_HEIGHT);
		
		float scale =Assets.DESCRIPTION_FONT.getScaleX();
		Assets.UI_FONT.setScale(1.7f);
		Assets.UI_FONT.setColor(Color.WHITE);
		Assets.UI_FONT.draw(batch, ""+currentValue, x+ROW_HEIGHT, y+ROW_HEIGHT*0.85f);
		Assets.UI_FONT.draw(batch, ""+endValue, x+ROW_HEIGHT*3, y+ROW_HEIGHT*0.85f);
		Assets.UI_FONT.setScale(scale);
		
	}

}
