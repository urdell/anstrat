package com.anstrat.geography;

public enum TerrainType {
	//"Normal" terrain
	FIELD("Field", 1, 1, "grass"),
	FOREST("Forest", 2, 1, "forest"),
	HILL("Hill", 2, 2, "hill"),
	MOUNTAIN("Mountain", Integer.MAX_VALUE, 1, "mountain"),
	SHALLOW_WATER("Shallow Water", 2, 1, "shallow-0001","shallow-0002"),
	DEEP_WATER("Deep Water", 1, Integer.MAX_VALUE, "water-0001","water-0002"),
	
	//Rocky terrain
	ROCKYGROUND("Rocky Ground", 1, 1, "rockyground"),
	ROCKYFOREST("Rocky Forest", 2, 1, "rockyforest"),
	ROCKYHILL("Rocky Hill", 2, 2, "rockyhill"),
	VOLCANO("Volcano", Integer.MAX_VALUE, 1, "volcano"),
	ROCKYLAVA("Rocky Lava", 2, 1, "rockylava"),
	ROCKYLAVAPOOL("Rocky Lava Pool", Integer.MAX_VALUE, 1, "rockylavapool"),
	
	//Snowy terrain
	SNOW("Snow", 1, 1, "snow"),
	SNOWFOREST("Snowy Forest", 2, 1, "snowforest"),
	SNOWHILL("Snowy Hill", 2, 2, "snowhill"),
	SNOWMOUNTAIN("Snowy Mountain", Integer.MAX_VALUE, 1, "snowmountain"),
	
	//Buildings
	CASTLE("Castle", 1, 2, "castle"),
	VILLAGE("Village", 1, 1, "village");
	
	public transient final String name;
	public transient final int defenceBonus;
	public transient final int penalty;
	public transient final String[] textures;
	
	private TerrainType(String name, int penalty, int defenceBonus, String ... textures){
		this.name = name;
		this.penalty = penalty;
		this.defenceBonus = defenceBonus;
		this.textures = textures;
	}
}
