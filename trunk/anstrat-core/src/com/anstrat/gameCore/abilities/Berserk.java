package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.BerserkAnimation;
import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.BerserkEffect;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.gameCore.effects.EffectFactory;
import com.anstrat.gui.GEngine;

public class Berserk extends Ability{

	private static final int HP_COST = 3;
	private static final int AP_COST = 5;
	
	private static final long serialVersionUID = 1L;
	
	public Berserk(){
		super("Berserk", "Sacrifice health to increase attack", AP_COST);
	}

	private boolean isAlreadyActivated(Unit unit){
		for(Effect effect : unit.effects) 
			if(effect instanceof BerserkEffect) return true;
		
		return false;
	}
	
	@Override
	public boolean isAllowed(Unit source) {
		// Only one berserk effect allowed to be active at the same time and unit should not be able to commit suicide
		return super.isAllowed(source)  && source.currentHP >= HP_COST && !isAlreadyActivated(source);
	}

	@Override
	public void activate(Unit source) {
		super.activate(source);
		
		GEngine engine = GEngine.getInstance();
		
		engine.getUnit(source).playCustom(Assets.getAnimation("berserker-berserk"), false);
		source.currentHP -= HP_COST;
		source.effects.add(EffectFactory.createEffect(EffectFactory.BERSERK));
		
		// Double animation speed
		engine.getUnit(source).animationSpeed = 2f;
		
		Animation animation = new BerserkAnimation(source);
		engine.animationHandler.enqueue(animation);
	}

	@Override
	public String getIconName(Unit source) {
		if(isAlreadyActivated(source)) return "berserk-button-active";
		if(!isAllowed(source)) return "berserk-button-gray";
		
		return "berserk-button";
	}
}
