package com.anstrat.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class UI extends Stage{

	protected boolean visible = true;
	
	public UI(SpriteBatch batch, OrthographicCamera camera) {
		super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
		super.setCamera(camera);
	}
	
	public void resize(float width, float height){
		super.setViewport(width, height, true);
	}
	
	@Override
	public void draw(){
		//super.act(Gdx.graphics.getDeltaTime());
		if(visible) super.draw();
	}
	
	/**
	 * TODO: Go through all elements and check automatically
	 * @param x the x window coordinate
	 * @param y the y window coordinate
	 * @param count
	 * @return If it hit any interface element
	 */
	abstract public boolean tap(float x, float y, int count, int button);
	
	public boolean isVisible(){
		return visible;
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
}
