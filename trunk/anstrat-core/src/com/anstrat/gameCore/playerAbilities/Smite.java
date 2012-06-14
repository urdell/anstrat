package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.HealAnimation;
import com.anstrat.animation.SmiteAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.gameCore.effects.TriggerOnKill;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class Smite extends PlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int damage = 5;
	
	public Smite(Player player) {
		super("Smite", "sword", "Deals damage to one target enemy unit", 5, player);
	}
	
	public void activate(Unit target){
		super.activate();
		target.currentHP -= damage;
		if(target.currentHP <= 0){
			// GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(defender));
			State.activeState.unitList.remove(target.id);
		}
		Gdx.app.log("PlayerAbility", "Smite was cast");
		Animation animation = new SmiteAnimation();
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	public Boolean isAllowed(Unit target) {
		return super.isAllowed(player) && target.ownerId != player.userID;
	}
}
