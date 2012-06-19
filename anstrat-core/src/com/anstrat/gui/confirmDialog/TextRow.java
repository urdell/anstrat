package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextRow extends ConfirmRow{

	private String text;
	
	public TextRow(String text){
		this.text = text;
	}
	
	@Override
	public void draw(float x, float y, SpriteBatch batch) {
		
		float scale =Assets.MENU_FONT.getScaleX();
		Assets.MENU_FONT.setScale(1.5f);
		Assets.MENU_FONT.setColor(Color.WHITE);
		Assets.MENU_FONT.draw(batch, text, x, y+ROW_HEIGHT);
		Assets.MENU_FONT.setScale(scale);
		
	}

	
	
}
