package com.anstrat.command;

import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.Ability;

public class ActivateAbilityCommand extends Command {

	private static final long serialVersionUID = -7801276709452841347L;
	private int sourceUnitId;
	private int abilityId;
	
	public ActivateAbilityCommand(Unit source, int abilityId){
		sourceUnitId = source.id;
		this.abilityId = abilityId;
	}
	
	@Override
	protected void execute() {
		State s = State.activeState;
		Unit source = s.unitList.get(sourceUnitId);
		Ability ability = source.abilities.get(abilityId);
		ability.activate(source);	
	}
	
	@Override
	public boolean isAllowed(){
		State s = State.activeState;
		Unit source = s.unitList.get(sourceUnitId);
		Ability ability = source.abilities.get(abilityId);
		
		return super.isAllowed() && ability != null && ability.isAllowed(source);
		
	}

}
