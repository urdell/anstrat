package com.anstrat.gameCore;

public class DamageModification {
	
	private static final double[][] modifiers = {
		/*0 Berserker*/		{1.0, 1.0, 1.0, 1.0, 1.2, 1.0,   1.0, 1.2, 1.0, 1.0, 1.0, 1.0},
		/*1 Shaman*/		{1.0, 1.0, 1.0, 1.5, 1.0, 1.0,   1.0, 1.0, 1.0, 1.0, 1.0, 1.5},
		/*2 Axe Thrower*/	{1.0, 1.0, 1.0, 1.0, 1.0, 1.2,   1.2, 1.0, 1.0, 1.0, 1.0, 1.0},
		/*3 Swordsman*/		{1.3, 1.0, 1.0, 1.0, 1.0, 1.0,   1.0, 1.0, 1.0, 1.0, 1.3, 1.0},
		/*4 Wolf*/			{1.0, 1.0, 1.3, 1.0, 1.0, 1.0,   1.0, 1.0, 1.3, 1.0, 1.0, 1.0},
		/*5 Hawk*/			{1.0, 1.3, 1.0, 1.0, 1.0, 1.0,   1.0, 1.0, 1.0, 1.3, 1.0, 1.0},
		
		/*6 Valkyrie*/		{1.0, 1.0, 1.2, 1.0, 1.0, 1.0,   1.0, 1.0, 1.2, 1.0, 1.0, 1.0},
		/*7 Fallen Warrior*/{1.0, 1.2, 1.0, 1.0, 1.0, 1.0,   1.0, 1.0, 1.0, 1.2, 1.0, 1.0},
		/*8 Dark Elf*/		{1.0, 1.0, 1.0, 1.0, 1.0, 1.3,   1.3, 1.0, 1.0, 1.0, 1.0, 1.0},
		/*9 Goblin Shaman*/	{1.0, 1.0, 1.0, 1.5, 1.0, 1.0,   1.0, 1.0, 1.0, 1.0, 1.0, 1.5},
		/*10 Jotun*/		{1.0, 1.0, 1.0, 1.0, 1.2, 1.0,   1.0, 1.2, 1.0, 1.0, 1.0, 1.0},
		/*11 Troll*/		{1.2, 1.0, 1.0, 1.0, 1.0, 1.0,   1.0, 1.0, 1.0, 1.0, 1.2, 1.0}
	};
	
	private static int getInternalUnitNumber(UnitType type){
		switch(type){
		case BERSERKER:
			return 0;
		case SHAMAN:
			return 1;
		case AXE_THROWER:
			return 2;
		case SWORD:
			return 3;
		case WOLF:
			return 4;
		case HAWK:
			return 5;
		case VALKYRIE:
			return 6;
		case FALLEN_WARRIOR:
			return 7;
		case DARK_ELF:
			return 8;
		case GOBLIN_SHAMAN:
			return 9;
		case JOTUN:
			return 10;
		case TROLL:
			return 11;
		}
		return 0;
	}
	
	
	public static double getAttackModifier(UnitType attacker, UnitType defender) {
		int aNr = getInternalUnitNumber(attacker);
		int dNr = getInternalUnitNumber(defender);
		return modifiers[aNr][dNr];
	}
	public static int getAttackModifierAsPercent(UnitType attacker, UnitType defender) {
		return (int)(100*getAttackModifier(attacker, defender));
	}

}
