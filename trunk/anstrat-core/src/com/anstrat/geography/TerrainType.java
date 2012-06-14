package com.anstrat.geography;

public enum TerrainType {
	DEEP_WATER("Deep Water", 1),
	//SNOW("Snow", 1),
	FIELD("Field", 1),
	MOUNTAIN("Mountain", 1),
	//VOLCANO("Volcano", 1),
	FOREST("Forest", 1),
	//HILL("Hill", 2),
	SHALLOW_WATER("Shallow Water", 1),
	CASTLE("Castle", 2),
	VILLAGE("Village", 1);
	
	public transient final String name;
	public transient final int defenceBonus;
	
	private TerrainType(String name, int defenceBonus){
		this.name = name;
		this.defenceBonus = defenceBonus;
	}
}
