package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.RemoveEffectsAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class RemoveEffects extends TargetedPlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RemoveEffects(Player player) {
		super(player, PlayerAbilityType.REMOVE_EFFECTS);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		for(Effect e : target.effects) {
			target.effects.remove(e);
		}
		Animation animation = new RemoveEffectsAnimation(target);
		GEngine.getInstance().animationHandler.enqueue(animation);
		Gdx.app.log("PlayerAbility", "Remove effects was cast");
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				targetUnit.ownerId == player.playerId;
	}

}
