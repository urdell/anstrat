package com.anstrat.gameCore.playerAbilities;

import com.anstrat.gameCore.Combat;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.badlogic.gdx.Gdx;

public class Confusion extends TargetedPlayerAbility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int damage = 6;
	
	public Confusion(Player player) {
		super(player, PlayerAbilityType.CONFUSION);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		Gdx.app.log("PlayerAbility", "Confusion was cast");
		Combat.battle(target, target);
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				targetUnit.ownerId != player.playerId;
	}
}
