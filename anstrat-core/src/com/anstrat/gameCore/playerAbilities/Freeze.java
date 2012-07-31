package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.FreezeAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Freeze extends TargetedPlayerAbility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int damage = 4;
	
	public Freeze(Player player) {
		super(player, PlayerAbilityType.COMETSTRIKE);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit centerTarget = StateUtils.getUnitByTile(tile);
		Animation animation = new FreezeAnimation(tile, damage);
		
		if (centerTarget != null) {
			centerTarget.currentHP -= damage;
			if (centerTarget.currentHP <= 0) {
				GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(centerTarget, GEngine.getInstance().getUnit(centerTarget).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
				State.activeState.unitList.remove(centerTarget.id);
			}
		}
		GEngine.getInstance().animationHandler.enqueue(animation);
		Gdx.app.log("PlayerAbility", "Freeze was cast");
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		return super.isAllowed(player);
	}
}
