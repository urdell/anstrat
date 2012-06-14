package com.anstrat.guiComponent;

import com.anstrat.core.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * A fade in transition effect. Removes itself after fading in.
 * @author Kalle
 */
public class TransitionEffect extends Actor {
	private static float speed = 3f;
	private Sprite overlay;
	
	public TransitionEffect(){
		overlay = new Sprite(Assets.WHITE);
		overlay.setColor(0f, 0f, 0f, 1f);
		overlay.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		float alpha = overlay.getColor().a - Gdx.graphics.getDeltaTime()*speed;
		if(alpha < 0){
			this.stage.removeActor(this);
			return;
		}
		overlay.setColor(0f, 0f, 0f, alpha);
		overlay.draw(batch);
	}

	@Override
	public Actor hit(float x, float y) {
		return null;
	}
}
