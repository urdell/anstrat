package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.GlassCannonAnimation;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.GlassCannonEffect;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class GlassCannon extends TargetedPlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GlassCannon(Player player) {
		super(player, PlayerAbilityType.GLASS_CANNON);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		target.effects.add(new GlassCannonEffect());
		Animation animation = new GlassCannonAnimation(target);
		GEngine.getInstance().animationHandler.enqueue(animation);
		Gdx.app.log("PlayerAbility", "Glass Cannon was cast");
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				Fog.isVisible(target, player.playerId);
	}

}
