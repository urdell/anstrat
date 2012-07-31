package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public class PoisonEffect extends Effect implements DamageModifier,TriggerOnTurnEnd {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int nrOfRoundsTotal;
	private int nrOfRoundsSoFar = 0;
	
	public PoisonEffect(int nrOfRounds){
		this.name = "Poison";
		this.iconName = "sword";
		this.nrOfRoundsTotal = nrOfRounds;
	}
	
	@Override
	public float damageModification(Unit unit) { 
		return -0.5f;
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
}

