package com.anstrat.core;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.input.GestureDetector.GestureListener;

/**
 * A custom gesture detector that wraps around a {@link GestureDetector} correcting the long
 * press behavior
 * @author Erik
 *
 */
public class CustomGestureDetector extends GestureAdapter implements InputProcessor {

	private float longPressDuration;
	private float timePassed;
	private boolean touchDown;
	private int x,y;
	private GestureListener[] listeners;
	private GestureDetector detector;
	
	public CustomGestureDetector(GestureListener... listeners){
		this(1f, listeners);
	}
	
	/**
	 * @param longPressDuration the delay before a long press is registered, in seconds
	 */
	public CustomGestureDetector(float longPressDuration, GestureListener... listeners){
		this.longPressDuration = longPressDuration;
		this.listeners = listeners;
		
		// Add us as first listener, and also add the other listeners
		GestureMultiplexer multiplexer = new GestureMultiplexer();
		for(GestureListener listener : listeners){
			multiplexer.addProcessor(listener);
		}
		
		multiplexer.addProcessor(0, this);
		this.detector = new GestureDetector(multiplexer);
	}
	
	public void update(float delta){
		if(!touchDown) return;
		
		timePassed += delta;
		
		if(timePassed > longPressDuration){
			touchDown = false;
			
			for(GestureListener listener : listeners){
				if(listener.longPress(this.x, this.y)){
					break;
				}
			}
		}
	}
	
	@Override
	public boolean longPress(int x, int y){
		// Suppress the original long press event, and fire it to our listeners manually
		return true;
	}
	
	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		detector.touchDown(x, y, pointer, button);
		
		this.x = x;
		this.y = y;
		touchDown = true;
		timePassed = 0f;
		
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		detector.touchUp(x, y, pointer, button);
		
		touchDown = false;
		
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		detector.touchDragged(x, y, pointer);
		
		touchDown = false;
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return detector.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return detector.keyTyped(character);
	}

	@Override
	public boolean touchMoved(int x, int y) {
		return detector.touchMoved(x, y);
	}

	@Override
	public boolean scrolled(int amount) {
		return detector.scrolled(amount);
	}

	@Override
	public boolean keyDown(int keycode) {
		return detector.keyDown(keycode);
	}
}
