package com.anstrat.gameCore.effects;

public abstract class EffectFactory {
	
	public static final int NOTHING = 0;
	public static final int AP_DRAIN = 1;
	public static final int SHIELD_WALL = 2;
	public static final int BERSERK = 3;
	
	public static Effect createEffect(int effectId){
		
		switch(effectId){
			case AP_DRAIN: return new APDrainEffect();
			case SHIELD_WALL: return new ShieldWallEffect();
			case BERSERK: return new BerserkEffect();
			default: return null;
		}
	}
}
