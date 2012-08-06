package com.anstrat.animation;

import com.anstrat.gui.GEngine;

/**
 * Animation that will zoom in on given coordinates
 * @author Ekis
 *
 */
public class ZoomCameraAnimation extends Animation {
	public static final float moveSpeed = 0.5f;
	private float start, current, end;
	float amtOffset, offset;
	private GEngine ge;
	
	/**
	 * 
	 * @param endTile coordinates that this animation will zoom in on
	 */
	public ZoomCameraAnimation(float endZoom, float length) {
		this.length = length;
		lifetimeLeft = length;
		
		ge = GEngine.getInstance();
		start = ge.camera.zoom;
		end = endZoom;
		current = start;
		
		offset = end - start;
		
	}
	
	@Override
	public void run(float deltaTime) {
		amtOffset = (length-lifetimeLeft)/length;
		current = start + (offset*amtOffset);
		ge.camera.zoom = current;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return true;
	}
}
