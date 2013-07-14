package com.anstrat.gameCore.playerAbilities;

import java.io.Serializable;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Player;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.CostRow;
import com.anstrat.gui.confirmDialog.TextRow;

public abstract class PlayerAbility implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final Player player;
	public final PlayerAbilityType type;
	
	public PlayerAbility(Player player, PlayerAbilityType type) {
		
		this.player = player;
		this.type = type;
	}
	
	public void activate(){
		player.mana -= type.manaCost;
		GEngine.getInstance().updateUI();
	}
	
	public boolean isAllowed(Player player){
		return player.mana >= type.manaCost;	// Player must have the required mana
	}
	
	public static PlayerAbility[] getAbilities() {
		return null;
	}
	
	public ConfirmDialog generateConfirmDialog(String abilName, int position){
		ConfirmRow costRow = new CostRow(player.mana, type.manaCost, false);
		return ConfirmDialog.abilityConfirm(position, abilName, costRow);
	}
}
