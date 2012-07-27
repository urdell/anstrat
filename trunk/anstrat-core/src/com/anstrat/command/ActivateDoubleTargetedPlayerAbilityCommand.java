package com.anstrat.command;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.playerAbilities.DoubleTargetedPlayerAbility;
import com.anstrat.gameCore.playerAbilities.PlayerAbility;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityFactory;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.gameCore.playerAbilities.TargetedPlayerAbility;
import com.anstrat.geography.TileCoordinate;

public class ActivateDoubleTargetedPlayerAbilityCommand extends Command {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PlayerAbilityType type;
	private int playerid;
	private TileCoordinate target1;
	private TileCoordinate target2;
	
	public ActivateDoubleTargetedPlayerAbilityCommand(Player player,  PlayerAbilityType type, TileCoordinate target1, TileCoordinate target2){
		this.type = type;
		this.playerid = player.playerId;
		this.target1 = target1;
		this.target2 = target2;
	}
	
	@Override
	protected void execute() {
		
		PlayerAbility ability = PlayerAbilityFactory.createAbility(type, State.activeState.players[playerid]);
		if(ability instanceof DoubleTargetedPlayerAbility){
			DoubleTargetedPlayerAbility temp = (DoubleTargetedPlayerAbility)ability;
			temp.activate(ability.player, target1, target2);
		}		
	}
	
	@Override
	public boolean isAllowed(){
		PlayerAbility ability = PlayerAbilityFactory.createAbility(type, State.activeState.players[playerid]);
		
		return super.isAllowed()
				&& ability != null
				&& ability instanceof DoubleTargetedPlayerAbility
				&& ((DoubleTargetedPlayerAbility)ability).isAllowed(ability.player, target1, target2);
	}
}
