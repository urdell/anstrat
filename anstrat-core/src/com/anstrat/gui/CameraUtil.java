package com.anstrat.gui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraUtil {

	public static void windowToCameraCoordinates(OrthographicCamera camera, Vector2 windowCoordinates, Vector3 out){	
		windowToCameraCoordinates(camera, windowCoordinates.x, windowCoordinates.y, out);
	}
	
	public static void windowToCameraCoordinates(OrthographicCamera camera, float x, float y, Vector3 out){
		out.x = x;
		out.y = y;
		out.z = 0f;
		
		camera.unproject(out);
		out.mul(1 / camera.zoom);
	}
	
	public static void resizeCamera(OrthographicCamera camera, int width, int height){
		float changeX = width - camera.viewportWidth;
		float changeY = height - camera.viewportHeight;
		
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		
		// Make sure the camera's position remains unchanged
		camera.position.x += changeX / 2f;
		camera.position.y += changeY / 2f;
	}
}
