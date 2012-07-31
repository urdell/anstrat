package com.anstrat.gameCore.abilities;

public class AbilityFactory {
	
	public static final int NOTHING = 0;
	public static final int KAMIKAZE = 1;
	public static final int HEAL = 2;
	public static final int SHADOWIMAGE = 3;
	public static final int MAGIC_SPEAR = 4;
	public static final int LEAP_ATTACK = 5;
	public static final int SHIELD_WALL = 6;
	public static final int CHAINING_AXE = 7;
	public static final int AP_HEAL = 8;
	public static final int THROW_ICE = 9;
	public static final int KNOCKBACK = 10;
	public static final int DARK_RITUAL = 11;
	public static final int POISON = 12;

	public static Ability createAbility(int abilityId){
		switch(abilityId){
			case KAMIKAZE: return new Kamikaze();
			case HEAL: return new Heal();
			case SHADOWIMAGE: return new ShadowImage();
			case MAGIC_SPEAR: return new MagicSpear();
			case LEAP_ATTACK: return new LeapAttack();
			case SHIELD_WALL: return new ShieldWall();
			case CHAINING_AXE: return new ChainingAxe();
			case AP_HEAL: 		return new APHeal();
			case THROW_ICE: return new ThrowIce();
			case KNOCKBACK: return new Knockback();
			case DARK_RITUAL: return new DarkRitual();
			case POISON: return new Poison();
			default: return null;
		}
	}
}
