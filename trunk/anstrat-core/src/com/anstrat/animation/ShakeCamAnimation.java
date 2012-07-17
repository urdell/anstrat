package com.anstrat.animation;

import java.util.Arrays;

import com.anstrat.gui.GEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class ShakeCamAnimation extends Animation {

	public static final float moveSpeed = 0.5f;
	private Vector3 pos;
	private Vector2 current;
	float xoffset, yoffset, amtOffset;
	private boolean[] done = null;
	private int i;
	
	/**
	 * shakes the cam for lenght time
	 * @param position
	 * @param size 
	 * @param length
	 */
	public ShakeCamAnimation(Vector2 position, float xsize, float ysize, float length, int shakes) {
		this.length = length;
		lifetimeLeft = length;
		done = new boolean[shakes];
		Arrays.fill(done, false);
		pos = new Vector3();
		current = new Vector2();
		pos.set(position.x, position.y, 0);
		current.set(pos.x, pos.y);
		xoffset = xsize;
		yoffset = ysize;
		
		i = done.length;
	}
	@Override
	public void run(float deltaTime) {
		if (lifetimeLeft <= 0) {
			Animation animation = new MoveCameraAnimation(current.set(pos.x, pos.y), 0.05f);
			GEngine.getInstance().animationHandler.runParalell(animation);
		}
		else {
			if(i > done.length*lifetimeLeft/length)
				i--;
			if (i >= 0 && i < done.length) {
				if(done[i] == false) {
					double rand = Math.random();
					if (rand <= 0.20) {
						current.set(pos.x, pos.y);
					}
					else if (rand <= 0.30) {
						current.set(pos.x-xoffset, pos.y-yoffset);
					}
					else if(rand <= 0.40) {
						current.set(pos.x-xoffset, pos.y);
					}
					else if(rand <= 0.5) {
						current.set(pos.x, pos.y-yoffset);
					}
					else if (rand < 0.6){
						current.set(pos.x, pos.y+yoffset);
					}
					else if (rand < 0.7) {
						current.set(pos.x+xoffset, pos.y);
					}
					else if (rand < 0.8) {
						current.set(pos.x + xoffset, pos.y + yoffset);
					}
					else if (rand < 0.9) {
						current.set(pos.x +xoffset, pos.y - yoffset);
					}
					else {
						current.set(pos.x - xoffset, pos.y + yoffset);
					}
					done[i] = true;
					Animation animation = new MoveCameraAnimation(current, 0.05f);
					GEngine.getInstance().animationHandler.runParalell(animation);
				}
			}
		}
	}
}
