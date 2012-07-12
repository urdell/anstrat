package com.anstrat.gameCore.playerAbilities;

import com.anstrat.gameCore.Player;

public class PlayerAbilityFactory {

	public static PlayerAbility createAbility(PlayerAbilityType type, Player player){
		if (type.equals(PlayerAbilityType.THUNDERBOLT))
			return new Thunderbolt(player);
		else if (type.equals(PlayerAbilityType.SPEEDBOOST))
			return new Speedboost(player);
		else if (type.equals(PlayerAbilityType.COMETSTRIKE))
			return new CometStrike(player);
		else
			return null;
	}
}
