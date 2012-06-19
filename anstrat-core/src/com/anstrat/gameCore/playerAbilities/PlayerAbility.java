package com.anstrat.gameCore.playerAbilities;

import java.io.Serializable;

import com.anstrat.gameCore.Player;

public abstract class PlayerAbility implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final String iconName;
	public final String description;
	public final int manaCost;
	public final Player player;
	public final PlayerAbilityType type;
	
	public PlayerAbility(String name, String iconName, String description, int manaCost, Player player, PlayerAbilityType type) {
		this.name = name;
		this.iconName = iconName;
		this.description = description;
		this.manaCost = manaCost;
		this.player = player;
		this.type = type;
	}
	
	public void activate(){
		player.mana -= manaCost;
	}
	
	public boolean isAllowed(Player player){
		return player.mana >= manaCost;	// Player must have the required mana
	}
	
	public static PlayerAbility[] getAbilities() {
		return null;
	}
}
