package com.anstrat.guiComponent;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class ColorTable extends Table {
	private Texture background;
	private Color backgroundColor;
	private Pixmap pix;
	float xOff = 0, yOff = 0, wOff = 0, hOff = 0;
	
	public ColorTable(Color color){
		super(Assets.SKIN);
		pix = new Pixmap(8, 8, Format.RGB565);
		this.setColor(color);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(background, getX()+xOff, getY()+yOff, getWidth()-wOff, getHeight()-hOff);
		super.draw(batch, parentAlpha);
	}
	
	@Override
	public void setBackground(Drawable background) {
		if(background==null)
			return;
		
		xOff = background.getLeftWidth()/2;
		yOff = background.getBottomHeight()/2;
		wOff = xOff + background.getRightWidth()/2;
		hOff = yOff + background.getTopHeight()/2;
		
		super.setBackground(background);
	}

	public void setColor(Color color){
		backgroundColor = color;
		pix.setColor(color);
		pix.fill();
		background = new Texture(pix);
	}
	
	public final Color getBackgroundColor(){
		return backgroundColor;
	}
}
