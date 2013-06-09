package com.anstrat.util;

/**
 * Custom dimension class, java.awt package is not available on Android. 
 * @author eriter
 *
 */
public class Dimension {
	public final int height, width;
	
	public Dimension(int width, int height){
		this.width = width;
		this.height = height;
	}
}
