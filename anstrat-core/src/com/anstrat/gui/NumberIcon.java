package com.anstrat.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class NumberIcon extends Widget{
	
	private int number;
	private float size;
	private Color color;
	
	public NumberIcon(int number, float size, Color color) {
		this.number = number;
		this.size = size;
		this.color = color;
	}
	
	@Override
	public float getPrefWidth(){
		return size;
	}
	@Override
	public float getPrefHeight(){
		return size;
	}
	
	public void setNumberAndSize(int number, float size) {
		this.number = number;
		this.size = size;
	}
	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		validate();
		FancyNumbers.drawNumber(number, getX(), getY(), size, false, color, batch);
	}
	
}
