package com.anstrat.gameCore.playerAbilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.geography.Map;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;

public abstract class TargetedPlayerAbility extends PlayerAbility {
	public TargetedPlayerAbility(Player player, PlayerAbilityType type) {
		super(player, type);
	}

private static final long serialVersionUID = 1L;
	

	public List<TileCoordinate> getValidTiles(Player player){
		Map map = State.activeState.map;
		List<TileCoordinate> validList = new ArrayList<TileCoordinate>();
		for(Tile[] row : map.tiles){
			for(Tile target : row){
				if(isAllowed(player, target.coordinates))
					validList.add(target.coordinates);
			}
		}
		return validList;
	}
	
	public boolean isAllowed(Player player, TileCoordinate tc){
		return isAllowed(player);
	}
	
	public void activate(Player player, TileCoordinate tc){
		super.activate();
	}
}
