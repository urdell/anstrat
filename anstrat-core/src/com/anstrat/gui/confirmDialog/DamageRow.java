package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DamageRow extends ConfirmRow {

	private int min, max;
	
	public DamageRow(int min, int max){
		this.min = min;
		this.max = max;
	}
	
	@Override
	public void draw(float x, float y, SpriteBatch batch) {

		float scale =Assets.MENU_FONT.getScaleX();
		Assets.MENU_FONT.setScale(1.5f);
		Assets.MENU_FONT.setColor(Color.RED);
		Assets.MENU_FONT.draw(batch, min+"-"+max, x, y+ROW_HEIGHT);
		Assets.MENU_FONT.setScale(scale);
		
	}

}
