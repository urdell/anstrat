package com.anstrat.command;

import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.abilities.TargetedAbility;
import com.anstrat.geography.TileCoordinate;

/**
 * 
 * 
 *
 */
public class ActivateTargetedAbilityCommand extends Command {

	private static final long serialVersionUID = -7801276709452841347L;
	private int sourceUnitId;
	private int abilityId;
	private TileCoordinate target;
	
	public ActivateTargetedAbilityCommand(Unit source, TileCoordinate target, int abilityId){
		sourceUnitId = source.id;
		this.abilityId = abilityId;
		this.target = target;
	}
	
	@Override
	protected void execute() {
		
		State s = State.activeState;
		Unit source = s.unitList.get(sourceUnitId);
		Ability ability = source.abilities.get(abilityId);
		if(ability instanceof TargetedAbility){
			((TargetedAbility)ability).activate(source, target);
		}		
	}
	
	@Override
	public boolean isAllowed(){
		State s = State.activeState;
		Unit source = s.unitList.get(sourceUnitId);
		Ability ability = source.abilities.get(abilityId);
		
		return super.isAllowed()
				&& ability != null
				&& ability instanceof TargetedAbility
				&& ((TargetedAbility)ability).isAllowed(source, target);
	}
}
