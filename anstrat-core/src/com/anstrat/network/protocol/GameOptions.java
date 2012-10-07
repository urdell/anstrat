package com.anstrat.network.protocol;

import java.io.Serializable;

import com.anstrat.geography.Map;

public class GameOptions implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int MAP_SPECIFIC = 0;
	public static final int MAP_GENERATED = 1;
	public static final int MAP_RANDOM = 2;
	public static final int MAP_CUSTOM = 3;
	
	/** Only used for custom maps **/
	public final Map map;
	
	public final int god;
	public final int team;
	
	public final boolean fog;
	
	/** One of {@link MAP_SPECIFIC}, {@link MAP_GENERATED} or {@link MAP_RANDOM} */
	public final int mapChoice;
	
	/** The associated map name if map choice is set to {@link SPECIFIC_MAP} */
	public final String mapName;

	/**
	 * 
	 * @param god
	 * @param team
	 * @param fog
	 * @param mapChoice
	 * @param mapName
	 * @param map may be null
	 */
	public GameOptions(int god, int team, boolean fog, int mapChoice, String mapName, Map map) {
		this.god = god;
		this.team = team;
		this.fog = fog;
		this.mapChoice = mapChoice;
		this.mapName = mapName;
		this.map = map;
	}
}
