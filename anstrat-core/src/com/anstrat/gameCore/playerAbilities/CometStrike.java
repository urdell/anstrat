package com.anstrat.gameCore.playerAbilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anstrat.animation.Animation;
import com.anstrat.animation.CometStrikeAnimation;
import com.anstrat.animation.FloatingTextAnimation;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class CometStrike extends TargetedPlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int damage = 5;
	
	public CometStrike(Player player) {
		super(player, PlayerAbilityType.COMETSTRIKE);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Map<Unit, Integer> map = new HashMap<Unit, Integer>();
		Unit centerTarget = StateUtils.getUnitByTile(tile);
		if(centerTarget != null){
			int damage = centerTarget.currentHP;
			centerTarget.currentHP = 0;
			map.put(centerTarget, damage);
		}
		
		List<Tile> list = State.activeState.map.getNeighbors(tile);
		for (Tile t: list) {
			Unit unit = StateUtils.getUnitByTile(t.coordinates);
			if (unit != null) {
				unit.currentHP -= damage;
				map.put(unit, damage);
			}
		}
		Animation animation = new CometStrikeAnimation(tile, map);
		
		
		GEngine.getInstance().animationHandler.enqueue(animation);
		
		if (centerTarget != null) {
			State.activeState.unitList.remove(centerTarget.id);
		}
		for (Tile t: list) {
			Unit unit = StateUtils.getUnitByTile(t.coordinates);
			if (unit != null && unit.currentHP <= 0) {
				State.activeState.unitList.remove(unit.id);
			}
		}
		
		Gdx.app.log("PlayerAbility", "Comet Strike was cast");
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Boolean tileNotCity = true;
		for (Building building : State.activeState.map.buildingList.values()){
			if(target == building.tileCoordinate){
				tileNotCity = false;
			}
		}
		return super.isAllowed(player) && tileNotCity;
	}
}
