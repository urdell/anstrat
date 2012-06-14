package com.anstrat.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class UI extends Stage{

	private boolean visible = true;
	
	public UI(SpriteBatch batch, OrthographicCamera camera) {
		super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, batch);
		super.setCamera(camera);
	}
	
	public void resize(int width, int height){
		super.setViewport(width, height, true);
	}
	
	@Override
	public void draw(){
		//super.act(Gdx.graphics.getDeltaTime());
		if(visible) super.draw();
	}
	
	public void setBounds(Actor actor, float x, float y, float width, float height){
		actor. x = x;
		actor.y = y;
		actor.width = width;
		actor.height = height;
	}
	
	/**
	 * TODO: Go through all elements and check automatically
	 * @param x the x window coordinate
	 * @param y the y window coordinate
	 * @param count
	 * @return If it hit any interface element
	 */
	abstract public boolean tap(int x, int y, int count);
	
	public boolean isVisible(){
		return visible;
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
}
