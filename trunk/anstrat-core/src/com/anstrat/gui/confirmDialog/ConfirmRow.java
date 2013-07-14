package com.anstrat.gui.confirmDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ConfirmRow {

	public static final float ROW_HEIGHT = (Gdx.graphics.getHeight()*0.04f);
	public static final float ROW_WIDTH = ROW_HEIGHT*4;
	
	
	
	public abstract void draw(float x, float y, SpriteBatch batch);
}
