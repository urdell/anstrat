package com.anstrat.gameCore.effects;

import java.io.Serializable;

public abstract class Effect implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String name;
	public String iconName;
	public boolean sheduledRemove = false;
	public String description = "";
	
}
