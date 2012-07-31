package com.anstrat.gameCore.playerAbilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.OdinsBlessingAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class OdinsBlessing extends PlayerAbility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int apRestored = 3;
	
	public OdinsBlessing(Player player) {
		super(player, PlayerAbilityType.ODINS_BLESSING);
	}
	
	@Override
	public void activate(){
		super.activate();
		Unit[] targets = StateUtils.getPlayersUnits(player);
		for(Unit unit : targets) {
			unit.currentAP = Math.min(unit.getMaxAP(), unit.currentAP+apRestored);
		}
		Animation animation = new OdinsBlessingAnimation(targets);
		GEngine.getInstance().animationHandler.enqueue(animation);
		Gdx.app.log("PlayerAbility", "Odin's blessing was cast");
	}
}
