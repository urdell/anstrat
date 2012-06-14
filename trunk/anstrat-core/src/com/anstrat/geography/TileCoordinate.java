package com.anstrat.geography;

import java.io.Serializable;

public class TileCoordinate implements Serializable {

	/**
	 * This should only be changed if the class is changed in such a way that
	 * it is incompatible with the previous version. 
	 */
	private static final long serialVersionUID = 8824003858898205043L;
	
	public int x,y;
	
	public TileCoordinate(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public TileCoordinate(TileCoordinate c){
		this.x = c.x;
		this.y = c.y;
	}
	
	public TileCoordinate(){
	}
	
	@Override
	public String toString(){
		return String.format("%s(%d, %d)", this.getClass().getSimpleName(), x, y);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		// instanceof also checks for null
		if(!(obj instanceof TileCoordinate)){
			return false;
		}
		
		TileCoordinate other = (TileCoordinate) obj;
		return other.x == this.x && other.y == this.y;
	}
	
	@Override
	public int hashCode(){
		return this.x * 13 + this.y * 37;
	}
}
