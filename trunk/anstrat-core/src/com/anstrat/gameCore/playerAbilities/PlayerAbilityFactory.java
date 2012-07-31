package com.anstrat.gameCore.playerAbilities;

import com.anstrat.gameCore.Player;

public class PlayerAbilityFactory {

	public static PlayerAbility createAbility(PlayerAbilityType type, Player player){
		if (type.equals(PlayerAbilityType.THUNDERBOLT))
			return new Thunderbolt(player);
		else if (type.equals(PlayerAbilityType.REMOVE_EFFECTS))
			return new RemoveEffects(player);
		else if (type.equals(PlayerAbilityType.COMETSTRIKE))
			return new CometStrike(player);
		else if (type.equals(PlayerAbilityType.SWAP))
			return new Swap(player);
		else if (type.equals(PlayerAbilityType.ZOMBIFY))
			return new Zombify(player);
		else if (type.equals(PlayerAbilityType.THORS_RAGE))
			return new ThorsRage(player);
		else if (type.equals(PlayerAbilityType.ODINS_BLESSING))
			return new OdinsBlessing(player);
		else if (type.equals(PlayerAbilityType.HELS_CURSE))
			return new HelsCurse(player);
		else if (type.equals(PlayerAbilityType.CONFUSION))
			return new Confusion(player);
		else
			return null;
	}
}
