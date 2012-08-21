package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.ThorsRageAnimation;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.ThorsRageEffect;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class ThorsRage extends TargetedPlayerAbility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ThorsRage(Player player) {
		super(player, PlayerAbilityType.THORS_RAGE);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		Gdx.app.log("PlayerAbility", "Thor's rage was cast");
		ThorsRageEffect effect = new ThorsRageEffect();
		target.effects.add(effect);
		
		Animation animation = new ThorsRageAnimation(target);
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				targetUnit.ownerId == player.playerId &&
				Fog.isVisible(target, player.playerId);
	}
}
