package com.anstrat.command;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.APHeal;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.abilities.ChainingAxe;
import com.anstrat.gameCore.abilities.Heal;
import com.anstrat.gameCore.abilities.Kamikaze;
import com.anstrat.gameCore.abilities.Knockback;
import com.anstrat.gameCore.abilities.LeapAttack;
import com.anstrat.gameCore.playerAbilities.OdinsBlessing;
import com.anstrat.gameCore.playerAbilities.PlayerAbility;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityFactory;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;

public class ActivatePlayerAbilityCommand extends Command {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int playerid;
	private PlayerAbilityType type;
	
	public ActivatePlayerAbilityCommand(Player player, PlayerAbilityType type) {
		this.playerid = player.playerId;
		this.type = type;
	}
	
	@Override
	protected void execute() {
		PlayerAbilityFactory.createAbility(type, State.activeState.players[playerid]).activate();
	}
	
	@Override
	public boolean isAllowed(){
		PlayerAbility ability = PlayerAbilityFactory.createAbility(type, State.activeState.players[playerid]);
		
		return super.isAllowed() && ability != null && ability.isAllowed(ability.player);
		
	}
	
	public String getReason() {
		State s = State.activeState;
		PlayerAbilityType ability = type;
		Player player = State.activeState.players[playerid];
		if( player.mana < ability.manaCost){
			return "Insufficient Mana";
		}
		if (ability.equals(PlayerAbilityType.ODINS_BLESSING)){
			// Cannot find any cases where you should not be allowed to cast it
		}
		return "Not possible";
	}

}
