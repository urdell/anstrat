package com.anstrat.popup;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class PopupGestureHandler implements GestureListener {
	
	private boolean overridesInput;
	
	@Override
	public boolean tap(int x, int y, int count) {
		return overridesInput;
	}

	public void setOverridesInput(boolean flag){
		this.overridesInput = flag;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer) {
		return overridesInput;
	}

	@Override
	public boolean longPress(int x, int y) {
		return overridesInput;
	}

	@Override
	public boolean fling(float velocityX, float velocityY) {
		return overridesInput;
	}

	@Override
	public boolean pan(int x, int y, int deltaX, int deltaY) {
		return overridesInput;
	}

	@Override
	public boolean zoom(float originalDistance, float currentDistance) {
		return overridesInput;
	}

	@Override
	public boolean pinch(Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
		return overridesInput;
	}
}
