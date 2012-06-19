package com.anstrat.guiComponent;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class ColorTable extends Table {
	private Texture background;
	float xOff = 0, yOff = 0, wOff = 0, hOff = 0;
	
	public ColorTable(Color color){
		super(Assets.SKIN);
		this.setColor(color);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.8f *parentAlpha); //0.5f * 
		batch.draw(background, x+xOff, y+yOff, width-wOff, height-hOff);
		super.draw(batch, parentAlpha);
	}
	
	@Override
	public void setBackground (NinePatch background) {
		if(background==null)
			return;
		
		xOff = background.getLeftWidth()/2;
		yOff = background.getBottomHeight()/2;
		wOff = xOff + background.getRightWidth()/2;
		hOff = yOff + background.getTopHeight()/2;
		
		super.setBackground(background);
	}

	public void setColor(Color color){
		Pixmap p = new Pixmap(8, 8, Format.RGB565);
		p.setColor(color);
		p.fill();
		background = new Texture(p);
	}
}
