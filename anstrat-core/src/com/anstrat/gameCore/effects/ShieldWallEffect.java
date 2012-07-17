package com.anstrat.gameCore.effects;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;

public class ShieldWallEffect extends Effect implements AffectsDefense, TriggerOnDefend, TriggerOnTurnEnd{

	private static final long serialVersionUID = 1L;

	private static final int DEFENSE_CAP = 4;
	
	private int defenseIncrease = 4;
	private boolean isActive;
	
	public ShieldWallEffect(){
		this.name = "Shield Wall";
		this.iconName = "shield";
		this.description = "Blocks the first attack each turn greatly reducing damage taken.";
	}
	
	@Override
	public void triggerOnDefend(Unit defender, Unit attacker) {
		
		if(!isActive){
			defenseIncrease = 0;
		}
		else{
			// TODO: Breaks the rule that the model shouldn't know about the gui, but I see no other
			// way with the current architecture...
			GEngine.getInstance().getUnit(defender).playCustom(Assets.getAnimation("sword-shield"), true);
		}
		isActive = false;
	}

	@Override
	public int rangedDefIncrease(Unit unit) {
		return defenseIncrease;
	}

	@Override
	public int bluntDefIncrease(Unit unit) {
		return defenseIncrease;
	}

	@Override
	public int cutDefIncrease(Unit unit) {
		return defenseIncrease;
	}

	@Override
	public void triggerOnTurnEnd(Unit u) {
		defenseIncrease = DEFENSE_CAP;
		isActive = true;
	}

	public boolean isActive(){
		return isActive;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ShieldWallEffect)
		{
			return ((ShieldWallEffect) o).name.equals(name);
		}
		return false;
	}
}
