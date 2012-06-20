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

public class Thunderbolt extends TargetedPlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int damage = 5;
	
	public Thunderbolt(Player player) {
		super("Thunderbolt", "sword", "Deals damage to one target enemy unit", 5, player, PlayerAbilityType.THUNDERBOLT);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		System.out.println("thunderbolt activate!");
		Unit target = StateUtils.getUnitByTile(tile);
		target.currentHP -= damage;
		if(target.currentHP <= 0){
			GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(target));
			State.activeState.unitList.remove(target.id);
		}
		Gdx.app.log("PlayerAbility", "Thunderbolt was cast");
		Animation animation = new ThunderboltAnimation(target);
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		System.out.println("thunderbolt isallowed!!");
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				targetUnit.ownerId != player.userID;
	}
}
