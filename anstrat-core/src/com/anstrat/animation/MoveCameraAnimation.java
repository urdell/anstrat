package com.anstrat.animation;

import com.anstrat.gui.GEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MoveCameraAnimation extends Animation {
	
	public static final float moveSpeed = 0.5f;
	private Vector3 start, current, end;
	float xoffset, yoffset, amtOffset;
	private GEngine ge;
	
	public MoveCameraAnimation(Vector2 endTile) {
		this(endTile, moveSpeed);
		/*length = moveSpeed;
		lifetimeLeft = length;
		
		ge = GEngine.getInstance();
		start = ge.getPosition();
		end = new Vector3();
		current = new Vector3();
		end.x = endTile.x; end.y = endTile.y; end.z = start.z;
		
		xoffset = end.x - start.x;
		yoffset = end.y - start.y;*/
		
	}
	public MoveCameraAnimation(Vector2 endTile, float moveSpeed) {
		length = moveSpeed;
		lifetimeLeft = length;
		
		ge = GEngine.getInstance();
		start = new Vector3(ge.getPosition());
		end = new Vector3();
		current = new Vector3();
		end.x = endTile.x; end.y = endTile.y; end.z = start.z;
		
		xoffset = end.x - start.x;
		yoffset = end.y - start.y;
	}
	@Override
	public void run(float deltaTime) {
		amtOffset = ((length-lifetimeLeft)/length);
		current.set(start.x + (xoffset*amtOffset), start.y + (yoffset*amtOffset),0);
		ge.setPosition(current);
		ge.cameraController.checkBounds();
	}
}
