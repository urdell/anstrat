package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.HelsCurseAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.HelsCurseEffect;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class HelsCurse extends TargetedPlayerAbility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int damage = 3;
	
	public HelsCurse(Player player) {
		super(player, PlayerAbilityType.HELS_CURSE);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		Gdx.app.log("PlayerAbility", "Hel's Curse was cast");
		HelsCurseEffect effect = new HelsCurseEffect(damage);
		target.effects.add(effect);
		Animation animation = new HelsCurseAnimation(target);
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				targetUnit.ownerId != player.playerId;
	}
}
