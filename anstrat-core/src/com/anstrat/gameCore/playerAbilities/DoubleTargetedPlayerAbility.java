package com.anstrat.gameCore.playerAbilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.geography.Map;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;

public class DoubleTargetedPlayerAbility extends PlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TileCoordinate coords;
	public int state = 0;
	
	public DoubleTargetedPlayerAbility(Player player,
			PlayerAbilityType type) {
		super(player, type);
	}
	
	public List<TileCoordinate> getValidTiles(Player player){
		Map map = State.activeState.map;
		List<TileCoordinate> validList = new ArrayList<TileCoordinate>();
		for(Tile[] row : map.tiles){
			for(Tile target : row){
				if (state == 0) {
					if(isAllowed(player, target.coordinates))
						validList.add(target.coordinates);
				}
				else {
					if (isAllowed(player, coords, target.coordinates))
						validList.add(target.coordinates);
				}
			}
		}
		return validList;
	}
	
	public boolean isAllowed(Player player, TileCoordinate tc){
		return super.isAllowed(player);
	}
	
	public boolean isAllowed(Player player, TileCoordinate tc1, TileCoordinate tc2){
		return super.isAllowed(player);
	}
	
	public void activate(Player player, TileCoordinate tc1, TileCoordinate tc2){
		super.activate();
	}
	
	public void activateFirst(Player player, TileCoordinate tc){
		state = 1;
		coords = tc;
	}
}
