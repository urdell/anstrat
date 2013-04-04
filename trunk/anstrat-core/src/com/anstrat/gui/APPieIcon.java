package com.anstrat.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class APPieIcon extends Widget {
	private int maxAP, APReg;
	
	public APPieIcon(int maxAP, int APReg) {
		this.maxAP = maxAP;
		this.APReg = APReg;
	}
	
	@Override
	public float getPrefWidth(){
		return Gdx.graphics.getWidth()*0.12f;
	}
	@Override
	public float getPrefHeight(){
		return Gdx.graphics.getWidth()*0.12f;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		validate();
		APPieDisplay.draw(getX(), getY(), getWidth(), APReg, maxAP, APReg, 2, batch, true, 1);
	}
	
}
