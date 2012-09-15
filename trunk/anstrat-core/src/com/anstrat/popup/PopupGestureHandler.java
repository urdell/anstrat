package com.anstrat.popup;

import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

public class PopupGestureHandler implements GestureListener {
	
	private boolean overridesInput;

	public void setOverridesInput(boolean flag){
		this.overridesInput = flag;
	}

	@Override
	public boolean zoom(float originalDistance, float currentDistance) {
		return overridesInput;
	}

	@Override
	public boolean pinch(Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
		return overridesInput;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return overridesInput;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return overridesInput;
	}

	@Override
	public boolean longPress(float x, float y) {
		return overridesInput;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return overridesInput;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return overridesInput;
	}
}
