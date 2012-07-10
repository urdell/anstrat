package com.anstrat.geography;

public enum TerrainType {
	DEEP_WATER("Deep Water", 1, "water-0001","water-0002"),
	//SNOW("Snow", 1, "snow"),
	FIELD("Field", 1, "grass"),
	MOUNTAIN("Mountain", 1, "mountain"),
	//VOLCANO("Volcano", 1, "volcano-0001","volcano-0002"),
	FOREST("Forest", 1, "forest"),
	//HILL("Hill", 2, "hill"),
	SHALLOW_WATER("Shallow Water", 1, "shallow-0001","shallow-0002"),
	CASTLE("Castle", 2, "castle"),
	VILLAGE("Village", 1, "village");
	
	public transient final String name;
	public transient final int defenceBonus;
	public transient final String[] textures;
	
	private TerrainType(String name, int defenceBonus, String ... textures){
		this.name = name;
		this.defenceBonus = defenceBonus;
		this.textures = textures;
	}
}
