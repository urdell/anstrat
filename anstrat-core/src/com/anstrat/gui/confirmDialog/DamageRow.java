package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.anstrat.gui.FancyNumbers;
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

		batch.draw(Assets.getTextureRegion("attack-symbol"), x+ROW_HEIGHT*0.0f, y, ROW_HEIGHT, ROW_HEIGHT);
		
		/*float scale =Assets.DESCRIPTION_FONT.getScaleX();
		Assets.UI_FONT.setScale(1.8f);
		Assets.UI_FONT.setColor(Color.RED);
		Assets.UI_FONT.draw(batch, min+"-"+max, x+ROW_HEIGHT*1.1f, y+ROW_HEIGHT);
		Assets.UI_FONT.setScale(scale);*/
		
		FancyNumbers.drawDamageRange(min, max, x+ROW_HEIGHT*0.9f, y+ROW_HEIGHT*0.1f, ROW_HEIGHT*0.9f, false, batch);
		
	}

}
