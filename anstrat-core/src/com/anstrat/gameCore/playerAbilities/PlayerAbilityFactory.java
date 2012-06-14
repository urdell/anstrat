package com.anstrat.gameCore.playerAbilities;

import com.anstrat.gameCore.Player;

public class PlayerAbilityFactory {
	public static final int NOTHING = 0;
	public static final int SMITE = 1;

	public static PlayerAbility createAbility(int abilityId, Player player){
		switch(abilityId){
			case SMITE: return new Smite(player);
			default: return null;
		}
	}
}
