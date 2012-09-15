package com.anstrat.popup;

import com.badlogic.gdx.InputMultiplexer;

/**
 * TODO: Remove inputs in a safe way?
 * @author kalle
 *
 */
public class PopupInputMultiplexer extends InputMultiplexer {
	
	private boolean overridesInput;
	
	public void setOverridesInput(boolean flag){
		this.overridesInput = flag;
	}

	@Override
	public boolean keyDown(int keycode) {
		super.keyDown(keycode);
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		super.keyUp(keycode);
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		super.keyTyped(character);
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		super.touchDown(x, y, pointer, button);
		return overridesInput;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		super.touchUp(x, y, pointer, button);
		return overridesInput;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		super.touchDragged(x, y, pointer);
		return overridesInput;
	}

	/*@Override	//TODO UIFIX
	public boolean touchMoved(int x, int y) {
		super.touchMoved(x, y);
		return overridesInput;
	}*/

	@Override
	public boolean scrolled(int amount) {
		super.scrolled(amount);
		return overridesInput;
	}
	
}
