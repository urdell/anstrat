package com.anstrat.command;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
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

}
