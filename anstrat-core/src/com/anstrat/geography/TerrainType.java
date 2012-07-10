package com.anstrat.geography;

public enum TerrainType {
	//"Normal" terrain
	FIELD("Field", 1, "grass"),
	FOREST("Forest", 1, "forest"),
	HILL("Hill", 2, "hill"),
	MOUNTAIN("Mountain", 1, "mountain"),
	SHALLOW_WATER("Shallow Water", 1, "shallow-0001","shallow-0002"),
	DEEP_WATER("Deep Water", 1, "water-0001","water-0002"),
	
	//Rocky terrain
	ROCKYGROUND("Rocky Ground", 1, "rockyground"),
	ROCKYFOREST("Rocky Forest", 1, "rockyforest"),
	ROCKYHILL("Rocky Hill", 2, "rockyhill"),
	VOLCANO("Volcano", 1, "volcano"),
	ROCKYLAVA("Rocky Lava", 1, "rockylava"),
	ROCKYLAVAPOOL("Rocky Lava Pool", 1, "rockylavapool"),
	
	//Snowy terrain
	SNOW("Snow", 1, "snow"),
	SNOWFOREST("Snowy Forest", 1, "snowforest"),
	SNOWHILL("Snowy Hill", 2, "snowhill"),
	SNOWMOUNTAIN("Snowy Mountain", 1, "snowmountain"),
	
	//Buildings
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
