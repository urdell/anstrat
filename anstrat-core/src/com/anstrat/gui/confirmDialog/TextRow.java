package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextRow extends ConfirmRow{

	private String text;
	
	public TextRow(String text){
		this.text = text;
	}
	
	@Override
	public void draw(float x, float y, SpriteBatch batch) {
		
		float scale =Assets.MENU_FONT.getScaleX();
		Assets.MENU_FONT.setScale(0.9f);
		Assets.MENU_FONT.setColor(0.4f, 0.4f, 0.4f, 1f);
		Assets.MENU_FONT.draw(batch, text, x+ROW_WIDTH/2f-Assets.MENU_FONT.getBounds(text).width/2f, y+ROW_HEIGHT);
		Assets.MENU_FONT.setScale(scale);
		
	}

	
	
}
