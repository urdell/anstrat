package com.anstrat.command;

import com.anstrat.gameCore.Combat;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;

public class AttackCommand extends Command{
	
	private static final long serialVersionUID = 1L;
	private int attackerId, defenderId;
	
	public AttackCommand(Unit attacker, Unit defender){
		attackerId = attacker.id;
		defenderId = defender.id;
	}
	
	public AttackCommand(int playerIndex, Unit attacker, Unit defender){
		super(playerIndex);
		attackerId = attacker.id;
		defenderId = defender.id;
	}
	
	@Override
	protected void execute(){
		State s = State.activeState;
		Unit attacker = s.unitList.get(attackerId);
		Unit defender = s.unitList.get(defenderId);
		Combat.battle(attacker, defender);
		
	}
	
	@Override
	public boolean isAllowed(){
		State s = State.activeState;
		Unit attacker = s.unitList.get(attackerId);
		Unit defender = s.unitList.get(defenderId);
		
		return super.isAllowed() &&
				attacker != null &&
				defender != null &&
				attacker.ownerId != defender.ownerId &&
				Fog.isVisible(defender.tileCoordinate, attacker.ownerId) &&
				Combat.canAttack(attacker, defender);
	}
	
	@Override
	public int hashCode(){
		return super.hashCode() + attackerId * 13 + defenderId * 23;
	}
}
