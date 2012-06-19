package com.anstrat.gameCore.playerAbilities;

import com.anstrat.gameCore.Player;

public class HerpDiDerp extends PlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HerpDiDerp(String name, String iconName, String description,
			int manaCost, Player player) {
		super(name, iconName, description, manaCost, player, PlayerAbilityType.HERP_DI_DERP);
	}

	public HerpDiDerp(Player player) {
		this("herp","derp","description mothafuckaaaaa!!!h", 5, player);
	}
	
	

}
