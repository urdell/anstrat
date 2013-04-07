package com.anstrat.network.protocol;

import java.io.Serializable;

import com.anstrat.geography.Map;

public class GameOptions implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	public enum MapType { SPECIFIC, GENERATED_SIZE_SMALL, GENERATED_SIZE_MEDIUM,
						  GENERATED_SIZE_LARGE, GENERATED_SIZE_RANDOM };
	
	/** The map to use if MapType is set to SPECIFIC **/
	public final Map map;
	public final MapType mapType;
	public final int god;
	public final int team;
	public final boolean fog;

	/** Map may only be null if map type is set to generated. **/
	public GameOptions(Map map, MapType mapType, int god, int team, boolean fog) {
		this.map = map;
		this.mapType = mapType;
		this.god = god;
		this.team = team;
		this.fog = fog;
		
		if(map == null && mapType == MapType.SPECIFIC){
			throw new IllegalArgumentException("Have to specify map if MapType is set to SPECIFIC!");
		}
		else if(map != null && mapType != MapType.SPECIFIC){
			throw new IllegalArgumentException("Can't specify map when MapType is set to GENERATED!");
		}
	}
}
