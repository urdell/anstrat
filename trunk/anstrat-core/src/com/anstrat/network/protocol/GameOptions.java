package com.anstrat.network.protocol;

import java.io.Serializable;

public class GameOptions implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int MAP_SPECIFIC = 0;
	public static final int MAP_GENERATED = 1;
	public static final int MAP_RANDOM = 2;
	
	public final int god;
	public final int team;
	
	public final boolean fog;
	
	/** One of {@link MAP_SPECIFIC}, {@link MAP_GENERATED} or {@link MAP_RANDOM} */
	public final int mapChoice;
	
	/** The associated map name if map choice is set to {@link SPECIFIC_MAP} */
	public final String mapName;

	public GameOptions(int god, int team, boolean fog, int mapChoice, String mapName) {
		this.god = god;
		this.team = team;
		this.fog = fog;
		this.mapChoice = mapChoice;
		this.mapName = mapName;
	}
}
