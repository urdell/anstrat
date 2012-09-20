package com.anstrat.gameCore.effects;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;

public class ShieldWallEffect extends Effect implements  DamageTakenModifier, TriggerOnTurnEnd{
	private static final long serialVersionUID = 1L;

	
	private float damageReduction = 1/2;
	private int nrOfRoundsTotal;
	private int nrOfRoundsSoFar = 1;
	private boolean isActive;
	
	public ShieldWallEffect( int nrOfRounds){
		this.name = "Shield Wall";
		this.iconName = "shield";
		this.description = "Take half damage for the duration of this turn";
		this.nrOfRoundsTotal = nrOfRounds;
	}


	@Override
	public void triggerOnTurnEnd(Unit u) {
		if (nrOfRoundsTotal <= nrOfRoundsSoFar ){
			sheduledRemove = true;
		}
		else{
			nrOfRoundsSoFar++;
		}
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

	@Override
	public float damageTakenModification(Unit unit) {
		return 0.5f;
	}
}
