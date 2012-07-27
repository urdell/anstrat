package com.anstrat.animation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.anstrat.gui.GEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AnimationHandler {
	
	private List<Animation> runningAnimations = new ArrayList<Animation>();
	private List<Animation> plannedRemovals = new ArrayList<Animation>();
	private Queue<Animation> pendingAnimations = new LinkedList<Animation>();
	private Queue<Animation> paralellPendingAnimations = new LinkedList<Animation>();
	
	private float timeToNext;
	
	
	
	
	/**
	 * Affects everything that is supposed to be affected, such as moving elements
	 * @param deltaTime
	 */
	public void runAll(float deltaTime){
		runningAnimations.addAll(paralellPendingAnimations);
		paralellPendingAnimations.clear();
		plannedRemovals.clear();
		for(Animation a : runningAnimations){
			a.lifetimeLeft -= deltaTime;
			a.run(deltaTime);
			
			if(a.lifetimeLeft <=0){
				plannedRemovals.add(a);
			}
					
		}
		for(Animation removeMe : plannedRemovals){
			runningAnimations.remove(removeMe);
			//TODO Works but possibly needs tossing around later (can only be robust enough here atm)
			if(removeMe instanceof DeathAnimation)
				((DeathAnimation) removeMe).removeGUnit();
		}
		timeToNext -= deltaTime;
		tryAddNextPending();
		//loop through animations
		//add pending animations
	}
	
	public void drawAll(float deltaTime, SpriteBatch batch){
		batch.setProjectionMatrix(GEngine.getInstance().camera.combined);
		batch.begin();
		
		for(Animation a : runningAnimations){
			a.draw(deltaTime, batch);
		}	
		
		batch.end();
	}
	
	public void drawAllFixed(float deltaTime, SpriteBatch batch){
		batch.setProjectionMatrix(GEngine.getInstance().uiCamera.combined);
		batch.begin();
		
		for(Animation a : runningAnimations){
			a.drawFixed(deltaTime, batch);
		}
		
		batch.end();
	}
	
	/**
	 * If allowed, add the next pending animation to running ones
	 * @return successful
	 */
	private boolean tryAddNextPending(){
		if(timeToNext <= 0 && !pendingAnimations.isEmpty()){
			addNextPending();
			//System.out.println("Pending animation added to running ones!");
			return true;
			
		}
		return false;
	}
	

	private void addNextPending(){
		Animation a = pendingAnimations.poll();
		timeToNext = a.length;
		runningAnimations.add(a);
	}
	
	public void enqueue(Animation animation){
		
		pendingAnimations.add(animation);
		tryAddNextPending();
	}
	
	/**
	 * Instantly terminates all animations.
	 * no guarantee to end state of animations
	 */
	public void clearAnimations(){
		runningAnimations.clear();
		pendingAnimations.clear();
		paralellPendingAnimations.clear();
		timeToNext=0;
	}
	
	/**
	 * Will add the animation to running ones next frame.
	 * Safe against concurrency
	 * @param animation
	 */
	
	public void runParalell(Animation animation){
		paralellPendingAnimations.add(animation);
	}

}
