package com.anstrat.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * 
 * Constructors of Animation that are called from the commands and gamecore must not have arguments referencing GUI.
 *
 */
public abstract class Animation {
	
	/** Duration until next Animation is allowed to play */
	protected float length;
	
	/** Time until this animation is terminated */
	protected float lifetimeLeft;
	
	/**
	 * Affects everything that is supposed to be affected, such as moving elements.
	 * Will run at least once when lifetimeLeft has reached 0.
	 * @param deltaTime
	 */
	public abstract void run(float deltaTime);
	
	/**
	 * Draws the animation using the game camera. Everything will be drawn using world coordinates.<br>
	 * Animations may be undrawn, and still affect other visible elements
	 * @param batch
	 */
	public void draw(float deltaTime, SpriteBatch batch){
		
	}
	
	/**
	 * Draws the animation a using the uiCamera. Everything will be drawn using screen coordinates.<br>
	 * Animations may be undrawn, and still affect other visible elements
	 * @param batch
	 */
	public void drawFixed(float deltaTime, SpriteBatch batch){
	
	}
	
	/**
	 * Returns whether the animation is in fog of war or not. If not it will not be drawn.
	 */
	public abstract boolean isVisible();
	
	/**
	 * Called once when the animation is removed. Safe for concurrent modification.
	 */
	public void postAnimationAction() { }
}
