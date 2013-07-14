package com.anstrat.gui.confirmDialog;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class XxxPicRow extends ConfirmRow {
	
	private TextureRegion pr0n;
	
	public XxxPicRow(TextureRegion pr0n){
		this.pr0n = pr0n;
	}
	
	@Override
	public void draw(float x, float y, SpriteBatch batch) {
		
		float scalebugfix = 0.75f, scalebugfix2 = 1.2f;
		batch.draw(pr0n, x+ROW_WIDTH/2f - pr0n.getRegionWidth()/2f*scalebugfix, y-ROW_HEIGHT/15f, 
				pr0n.getRegionWidth()*scalebugfix, ROW_HEIGHT*scalebugfix*scalebugfix2);
		
	}
}
