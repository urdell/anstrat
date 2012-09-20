package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.HuginAndMuninAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.HuginAndMuninEffect;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class HuginAndMunin extends TargetedPlayerAbility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HuginAndMunin(Player player) {
		super(player, PlayerAbilityType.HUGIN_AND_MUNIN);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		
		Unit target = StateUtils.getUnitByTile(tile);
		target.effects.add(new HuginAndMuninEffect());

		Animation animation = new HuginAndMuninAnimation(tile);
		GEngine.getInstance().animationHandler.enqueue(animation);
		Gdx.app.log("PlayerAbility", "Hugin and Munin was cast");
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Unit unit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) &&
				unit != null &&
				unit.ownerId != player.playerId;
	}
}
