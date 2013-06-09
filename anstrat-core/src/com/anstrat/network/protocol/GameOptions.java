package com.anstrat.network.protocol;

import java.io.Serializable;
import java.util.Random;

import com.anstrat.geography.Map;
import com.anstrat.util.Dimension;

public class GameOptions implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	public static enum MapType {
		GENERATED_SIZE_SMALL("Small"),
		GENERATED_SIZE_MEDIUM("Medium"),
		GENERATED_SIZE_LARGE("Large"),
		GENERATED_SIZE_RANDOM("Random"),
		SPECIFIC("Specific");
		
		private String description;
		
		private MapType(String description){
			this.description = description;
		}
		
		@Override
		public String toString() {
			return this.description;
		}

		/**
		 * Returns (or randomizes) the map size for the given MapType.
		 */
		public static Dimension getMapSize(MapType t, Random random){
			switch(t){
				case GENERATED_SIZE_LARGE: {
					return new Dimension(16,16);
				}
				case GENERATED_SIZE_MEDIUM: {
					return new Dimension(12,12);
				}
				case GENERATED_SIZE_SMALL: {
					return new Dimension(8,8);
				}
				case GENERATED_SIZE_RANDOM: {
				// TODO: Randomize! Make sure to keep a good width/height ratio
					return new Dimension(16,16);
				}
				default: {
					throw new IllegalArgumentException("Can't get size of map type: " + t);
				}
			}
		}
	}
	
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
		
		if(map != null){
			map.fogEnabled = fog;
		}
		
		if(map == null && mapType == MapType.SPECIFIC){
			throw new IllegalArgumentException("Have to specify map if MapType is set to SPECIFIC!");
		}
		else if(map != null && mapType != MapType.SPECIFIC){
			throw new IllegalArgumentException("Can't specify map when MapType is set to GENERATED!");
		}
	}
}
