package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.ThunderboltAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Zombify extends TargetedPlayerAbility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Zombify(Player player) {
		super(player, PlayerAbilityType.ZOMBIFY);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		target.currentHP -= damage;
		if(target.currentHP <= 0){
			GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(target, GEngine.getInstance().getUnit(target).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
			State.activeState.unitList.remove(target.id);
		}
		Gdx.app.log("PlayerAbility", "Thunderbolt was cast");
		Animation animation = new ThunderboltAnimation(target);
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
