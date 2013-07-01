package com.anstrat.command;

import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.APHeal;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.abilities.ChainingAxe;
import com.anstrat.gameCore.abilities.Heal;
import com.anstrat.gameCore.abilities.Kamikaze;
import com.anstrat.gameCore.abilities.Knockback;
import com.anstrat.gameCore.abilities.LeapAttack;
import com.anstrat.gameCore.abilities.LifeSteal;
import com.anstrat.gameCore.abilities.MagicSpear;
import com.anstrat.gameCore.abilities.Poison;
import com.anstrat.gameCore.abilities.ShadowImage;
import com.anstrat.gameCore.abilities.TargetedAbility;
import com.anstrat.gameCore.abilities.ThrowIce;
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

	public String getReason(TileCoordinate tileCoordinate) {
		State s = State.activeState;
		Unit source = s.unitList.get(sourceUnitId);
		Ability ability = source.abilities.get(abilityId);
		if(source.currentAP < ability.apCost){
			return "Insufficient AP";
		}
		if (ability instanceof APHeal ){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target friendly unit";
			}
			if(source.ownerId != StateUtils.getUnitByTile(target).ownerId){
				return "Must target friendly unit";
			}
			
			if (StateUtils.getUnitByTile(target).getMaxAP() == StateUtils.getUnitByTile(target).currentAP){
				return "Target already at max AP";
			}
		}	
		if (ability instanceof ChainingAxe){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target enemy unit";
			}
			if(source.ownerId == StateUtils.getUnitByTile(target).ownerId){
				return "Must target enemy unit";
			}
		}
			
		if (ability instanceof Heal){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target friendly unit";
			}
			if(source.ownerId != StateUtils.getUnitByTile(target).ownerId){
				return "Must target friendly unit";
			}
			if (StateUtils.getUnitByTile(target).getMaxHP() == StateUtils.getUnitByTile(target).currentHP){
				return "Target already at full health";
			}
		}	
		
		if (ability instanceof Kamikaze){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target enemy unit";
			}
			if(source.ownerId == StateUtils.getUnitByTile(target).ownerId){
				return "Must target enemy unit";
			}
		}
		if (ability instanceof Knockback){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target enemy unit";
			}
			if(source.ownerId == StateUtils.getUnitByTile(target).ownerId){
				return "Must target enemy unit";
			}
		}
		if (ability instanceof LeapAttack){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target enemy unit";
			}
			if(source.ownerId == StateUtils.getUnitByTile(target).ownerId){
				return "Must target enemy unit";
			}
			TileCoordinate tile = Knockback.getKnockBackCoordinate(source, StateUtils.getUnitByTile(target));
			
			if(State.activeState.map.getTile(tile) == null){
				return "Leap tile not available";
			}
			if(StateUtils.getUnitByTile(tile) != null || State.activeState.map.getTile(tile).terrain.penalty == Integer.MAX_VALUE){ 
				return "Leap tile not available";
			}
			
		}
		
		if (ability instanceof LifeSteal){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target enemy unit";
			}
			if(source.ownerId == StateUtils.getUnitByTile(target).ownerId){
				return "Must target enemy unit";
			}
		}
		if (ability instanceof MagicSpear){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target enemy unit";
			}
			if(source.ownerId == StateUtils.getUnitByTile(target).ownerId){
				return "Must target enemy unit";
			}
		}
		if (ability instanceof Poison){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target enemy unit";
			}
			if(source.ownerId == StateUtils.getUnitByTile(target).ownerId){
				return "Must target enemy unit";
			}
		}
		if (ability instanceof ThrowIce){
			if (null == StateUtils.getUnitByTile(target)){
				return "Must target enemy unit";
			}
			if(source.ownerId == StateUtils.getUnitByTile(target).ownerId){
				return "Must target enemy unit";
			}
		}
		return "not possible";
	}

}
