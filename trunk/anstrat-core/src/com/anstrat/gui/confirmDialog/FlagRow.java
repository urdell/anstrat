package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.anstrat.gui.APPieDisplay;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FlagRow extends ConfirmRow {

	@Override
	public void draw(float x, float y, SpriteBatch batch) {
		TextureRegion flag = Assets.getTextureRegion("speed");
		TextureRegion arrow = Assets.getTextureRegion("rightArrow");
		batch.draw(flag, x+ROW_HEIGHT*0.5f, y, ROW_HEIGHT, ROW_HEIGHT);
		batch.draw(arrow, x+ROW_HEIGHT*1.5f, y, ROW_HEIGHT, ROW_HEIGHT);
		batch.draw(flag, x+ROW_HEIGHT*2.5f, y, ROW_HEIGHT, ROW_HEIGHT);
		
	}

}
