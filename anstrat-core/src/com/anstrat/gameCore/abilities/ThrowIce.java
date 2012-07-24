package com.anstrat.gameCore.abilities;

import com.anstrat.gameCore.Unit;

public class ThrowIce extends TargetedAbility{
	
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 3;
	private static final int RANGE = 1;
	
	public ThrowIce() {
		super("Throw","Throws ice", AP_COST, RANGE);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	

	@Override
	public String getIconName(Unit source) {
		// TODO Auto-generated method stub
		return null;
	}

}
