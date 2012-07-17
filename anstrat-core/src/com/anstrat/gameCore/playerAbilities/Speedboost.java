package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.SpeedBoostAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class Speedboost extends TargetedPlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Speedboost(String name,  Player player) {
		super(name, player, PlayerAbilityType.SPEEDBOOST);
	}

	public Speedboost(Player player) {
		this("speed", player);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		target.freeMoves += 2;
		//TODO make freemoves usable.
		target.currentAP = target.getMaxAP();
		Animation animation = new SpeedBoostAnimation();
		GEngine.getInstance().animationHandler.enqueue(animation);
		Gdx.app.log("PlayerAbility", "Speedboost was cast");
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		System.out.println("speedboost isallowed!!");
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				targetUnit.ownerId == player.playerId;
	}

}
