package com.anstrat.gameCore.abilities;

import java.io.Serializable;

import com.anstrat.gameCore.Unit;

public abstract class Ability implements Serializable {
	
	private static final long serialVersionUID = 3L;
	
	public final String name;
	public final String description;
	private final int apCost;
	
	public Ability(String name, String description, int apCost){
		this.name = name;
		this.description = description;
		this.apCost = apCost;
	}
	
	public void activate(Unit source){
		source.currentAP -= apCost;
	}
	
	public boolean isAllowed(Unit source){
		return source.currentAP >= apCost;	// Unit must have the required ap
	}
	
	public abstract String getIconName(Unit source);
}
