package com.anstrat.core;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;

/**
 * Controls the camera implementing basic functions such as panning, flinging, zooming.
 * @author eriter
 *
 */
public class CameraController extends InputAdapter implements GestureListener {

	private float velX, velY;		// Flinging speed
	private boolean isFlinging;
	
	private float initialScale;
	private boolean isZooming;
	
	private OrthographicCamera camera;
	
	// Bounds restriction
	private float width;
	private float height;
	private float offsetTop, offsetBottom, offsetLeft, offsetRight;
	private float minWidth, minHeight, maxWidth = Float.MAX_VALUE, maxHeight = Float.MAX_VALUE;
	
	/**
	 * Constructs a camera controller that translates input to specific camera
	 * operations such as panning, flinging and zooming.
	 * @param camera the camera to control
	 */
	public CameraController(OrthographicCamera camera){
		this.camera = camera;
	}
	
	/**
	 * Sets the bounds of the camera, zero values of both width and height means there<br>
	 * are no bounds.
	 * @param width the width of the world
	 * @param height the height of the world
	 */
	public void setBounds(float width, float height){
		if(width < 0 || height < 0) throw new IllegalArgumentException("Bounds must be positive!");
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Sets the zoom limits of this camera controller.
	 * A zero value removes means there is no limit.
	 * @param minWidth the minimum world width allowed to be displayed by the viewport.
	 * @param minHeight the minimum world height allowed to be displayed by the viewport.
	 * @param maxWidth the maximum world height allowed to be displayed by the viewport.
	 * @param maxWidth the maximum world width allowed to be displayed by the viewport.
	 */
	public void setZoomLimits(float minWidth, float minHeight, float maxWidth, float maxHeight){
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}
	
	/**
	 * Sets the offset in ui size, for example if there's an ui object
	 * always covering a side of the viewport.
	 * @param offsetTop
	 * @param offsetBottom
	 * @param offsetLeft
	 * @param offsetRight
	 */
	public void setOffsets(float offsetTop, float offsetBottom, float offsetLeft, float offsetRight){
		this.offsetTop = offsetTop;
		this.offsetBottom = offsetBottom;
		this.offsetLeft = offsetLeft;
		this.offsetRight = offsetRight;
	}
	
	public void update(float delta) {
		//camera.position.add(5, 10, 0); //fast auto camera movement
		if (isFlinging) {
			// If true, (x,y) = 0,0 is top-left, otherwise bottom-left
			boolean yDown = camera.up.y < 0;
			
			velX *= 0.97f;
			velY *= 0.97f;
			camera.position.add(-velX * delta, (yDown ? -1 : 1) * velY * delta, 0);
			
			if (Math.abs(velX) < 0.01f) velX = 0;
			if (Math.abs(velY) < 0.01f) velY = 0;
			if(velX == 0 && velY == 0) isFlinging = false;
			
			int result = checkBounds();
			
			/*if(result != 0){		// reduce speed at collision
				// Collision, reduce speed by a lot
				velX *= 0.2;
				velY *= 0.2;
			}*/
			
			if((result & (COLLISION_LEFT | COLLISION_RIGHT)) != 0){
				//velX = -velX;  
				velX = 0;// no bounce
			}
			
			if((result & (COLLISION_BOTTOM | COLLISION_TOP)) != 0){
				//velY = -velY;	 
				velY = 0;// no bounce
			}
		}
	}
	
	@Override
	public boolean touchDown(int x, int y, int pointer) {
		isFlinging = false;
		isZooming = false;
		
		return false;
	}

	@Override
	public boolean fling (float velocityX, float velocityY) {
		isFlinging = true;
		velX = camera.zoom * velocityX * 0.5f;
		velY = camera.zoom * velocityY * 0.5f;
		
		return false;
	}
	
	@Override
	public boolean pan(int x, int y, int deltaX, int deltaY) {
		boolean yDown = camera.up.y < 0;
		camera.position.add(-deltaX * camera.zoom * 0.5f, (yDown ? -1 : 1) * deltaY * camera.zoom * 0.5f, 0);
		
		// Make sure camera stays within map bounds
		checkBounds();
		
		return false;
	}
	
	public void resize(){
		checkBounds();
	}

	private static final int COLLISION_TOP = 1;		// ..0001
	private static final int COLLISION_BOTTOM = 2;	// ..0010
	private static final int COLLISION_LEFT = 4;	// ..0100
	private static final int COLLISION_RIGHT = 8;	// ..1000
	
	/**
	 * Checks if the camera has gone out of bounds and if so corrects it.
	 * Returns and int with it's first or second bit set to indicate where it collided.
	 */
	public int checkBounds(){
		if(width == 0 && height == 0) return 0;		// No bounds
		
		int result = 0;
		
		// When zooming out the viewport will be larger (in world units)
		float viewportWidth = camera.viewportWidth * camera.zoom;
		float viewportHeight = camera.viewportHeight * camera.zoom;
		
		Vector2 offset = new Vector2(
				((viewportWidth) - camera.viewportWidth) / 2, 
				((viewportHeight) - camera.viewportHeight) / 2);
		
		// The top-left and bottom-right corners of the camera
		Vector2 cameraTL = new Vector2(camera.position.x - camera.viewportWidth / 2f, camera.position.y - camera.viewportHeight / 2f).sub(offset);
		Vector2 cameraBR = cameraTL.cpy().add(viewportWidth, viewportHeight);
		
		float offsetLeftW = offsetLeft * camera.zoom;
		float offsetRightW = offsetRight * camera.zoom;
		float offsetTopW = offsetTop * camera.zoom;
		float offsetBottomW = offsetBottom * camera.zoom;
		
		// If viewport is larger than width, keep the camera edges outside
		// If viewport is smaller than width, keep the camera edges inside
		boolean viewportWidthLargerThanMap = viewportWidth > width + offsetLeftW + offsetRightW;
		boolean viewportHeightLargerThanMap = viewportHeight > height + offsetTopW + offsetBottomW;
		
		// Handle x
		if(this.width > 0){
			if(viewportWidthLargerThanMap && cameraTL.x > -offsetLeftW || !viewportWidthLargerThanMap && cameraTL.x < -offsetLeftW){
				cameraTL.x = -offsetLeftW;
				result |= COLLISION_LEFT;
			}
			else if(viewportWidthLargerThanMap && cameraBR.x < width + offsetRightW || !viewportWidthLargerThanMap && cameraBR.x > width + offsetRightW){
				cameraTL.x = width - viewportWidth + offsetRightW;
				result |= COLLISION_RIGHT;
			}
		}
		
		// Handle y
		if(this.height > 0){
			if(viewportHeightLargerThanMap && cameraTL.y > -offsetTopW || !viewportHeightLargerThanMap && cameraTL.y < -offsetTopW){
				cameraTL.y = -offsetTopW;
				result |= COLLISION_TOP;
			}
			else if(viewportHeightLargerThanMap && cameraBR.y < height + offsetBottomW || !viewportHeightLargerThanMap && cameraBR.y > height + offsetBottomW){
				cameraTL.y = height - viewportHeight + offsetBottomW;
				result |= COLLISION_BOTTOM;
			}
		}

		// Convert back to centric coordinates
		cameraTL.add(camera.viewportWidth / 2f, camera.viewportHeight / 2f).add(offset);
		camera.position.x = cameraTL.x;
		camera.position.y = cameraTL.y;
		
		return result;
	}
	
	@Override
	public boolean zoom(float originalDistance, float currentDistance) {
		
		if(!isZooming){
			initialScale = camera.zoom;
			isZooming = true;
		}
		
		float ratio = originalDistance / currentDistance;
		float zoom = initialScale * ratio;
		
		setZoom(zoom);
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		setZoom(camera.zoom + (amount > 0 ? 1 : -1) * 0.1f);
		return false;
	}
	
	private void setZoom(float zoom) {
		
		// Limit maximum zoom
		float maxZoomWidth = minWidth / camera.viewportWidth;
		float maxZoomHeight = minHeight / camera.viewportHeight;
		float maxZoom = Math.max(maxZoomWidth, maxZoomHeight);
		
		if(maxZoom != 0 && zoom > maxZoom) zoom = maxZoom;
		
		// Limit minimum zoom
		float minZoomWidth = maxWidth / camera.viewportWidth;
		float minZoomHeight = maxHeight / camera.viewportHeight;
		float minZoom = Math.max(minZoomWidth, minZoomHeight);
		
		if(minZoom != 0 && zoom < minZoom) zoom = minZoom;
		
		camera.zoom = zoom;
		checkBounds();
	}
	
	// Unused events
	
	@Override
	public boolean pinch(Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
		return false;
	}

	@Override
	public boolean longPress(int x, int y) {
		return false;
	}
	
	@Override
	public boolean tap(int x, int y, int count) {
		return false;
	}
}
