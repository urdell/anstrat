package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.anstrat.gui.FancyNumbers;
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
		FancyNumbers.drawDamageRange(min, max, x+ROW_HEIGHT*0.9f, y+ROW_HEIGHT*0.1f, ROW_HEIGHT*0.9f, false, batch);
		
	}

}
