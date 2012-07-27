package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.TeleportAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;

public class Teleport extends DoubleTargetedPlayerAbility {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Teleport(Player player) {
		super("comet", player, PlayerAbilityType.TELEPORT);
	}
	
	@Override
	public void activateFirst(Player player, TileCoordinate tc){
		super.activateFirst(player, tc);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tc1, TileCoordinate tc2){
		super.activate(player, tc1, tc2);
		Unit unit = StateUtils.getUnitByTile(tc1);
		Animation animation = new TeleportAnimation(unit, StateUtils.getUnitByTile(tc2));
		StateUtils.getUnitByTile(tc2).tileCoordinate = tc1;
		unit.tileCoordinate = tc2;
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate tc){
		Unit unit = StateUtils.getUnitByTile(tc);
		if(unit == null || player.playerId != unit.ownerId)
			return false;
		else 
			return true;
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate tc1, TileCoordinate tc2){
		Unit u1 = StateUtils.getUnitByTile(tc1);
		Unit u2 = StateUtils.getUnitByTile(tc2);
		if (u1 != null && u2 != null && !u1.equals(u2) && u1.ownerId == player.playerId && u2.ownerId == player.playerId) {
			return true;
		}
		else 
			return false;
	}

}
