package com.anstrat.gameCore.abilities;

public class AbilityFactory {
	
	public static final int NOTHING = 0;
	public static final int BERSERK = 1;
	public static final int HEAL = 2;

	public static Ability createAbility(int abilityId){
		switch(abilityId){
			case BERSERK: return new Berserk();
			case HEAL: return new Heal();
			default: return null;
		}
	}
}
