package com.anstrat.gui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Handles the bounding box
 * @author Erik
 *
 */
public abstract class GObject {
	
	private BoundingBox boundingBox;
	private Vector3 bbMin, bbMax;
	protected boolean boundingBoxOutdated = true;
	
	public GObject(){
		boundingBox = new BoundingBox();
		bbMin = new Vector3();
		bbMax = new Vector3();
	}
	
	public BoundingBox getBoundingBox(){
		if(boundingBoxOutdated){
			Rectangle r = getBoundingRectangle();
			bbMin.x = r.x;
			bbMin.y = r.y;
			
			bbMax.x = r.x + r.width;
			bbMax.y = r.y + r.height;
			
			boundingBox.set(bbMin, bbMax);
			
			boundingBoxOutdated = false;
		}
		
		return boundingBox;
	}
	
	protected abstract Rectangle getBoundingRectangle();
}
