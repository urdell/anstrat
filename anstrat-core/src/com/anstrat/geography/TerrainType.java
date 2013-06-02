package com.anstrat.geography;

public enum TerrainType {
	//"Normal" terrain
	FIELD("Field", 1, "grass"),
	FOREST("Forest", 2, "forest"),
	HILL("Hill", 2, "hill"),
	MOUNTAIN("Mountain", Integer.MAX_VALUE, "mountain"),
	SHALLOW_WATER("Shallow Water", 2, "shallow"),
	DEEP_WATER("Deep Water", Integer.MAX_VALUE, "water"),
	
	//Rocky terrain
	ROCKYGROUND("Rocky Ground", 1, "rockyground"),
	ROCKYFOREST("Rocky Forest", 2, "rockyforest"),
	ROCKYHILL("Rocky Hill", 2, "rockyhill"),
	ROCKYLAVA("Rocky Lava", 2, "rockylava"),
	ROCKYLAVAPOOL("Rocky Lava Pool", Integer.MAX_VALUE, "rockylavapool"),
	
	//Snowy terrain
	SNOW("Snow", 1, "snow"),
	SNOWFOREST("Snowy Forest", 2, "snowforest"),
	SNOWHILL("Snowy Hill", 2, "snowhill"),
	SNOWMOUNTAIN("Snowy Mountain", Integer.MAX_VALUE, "snowmountain"),
	
	//Special
	CRATER("Crater", 2, "crater"),
	VOLCANO("Volcano", Integer.MAX_VALUE, "volcano"),
	
	//Buildings
	CASTLE("Castle", 1, "castle"),
	PORTAL("Portal", 1, "portal-base"),
	VILLAGE("Village", 1, "village");
	//RUNE("Runestone", 1, "runestone-red");
	
	public transient final String name;
	public transient final int penalty;
	public transient final String[] textures;
	
	private TerrainType(String name, int penalty, String ... textures){
		this.name = name;
		this.penalty = penalty;
		this.textures = textures;
	}
}
