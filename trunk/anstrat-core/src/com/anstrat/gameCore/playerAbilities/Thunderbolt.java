package com.anstrat.gameCore.playerAbilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.ThunderboltAnimation;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TerrainType;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Thunderbolt extends TargetedPlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int damage = 8;
	
	public Thunderbolt(Player player) {
		super(player, PlayerAbilityType.THUNDERBOLT);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit target = StateUtils.getUnitByTile(tile);
		target.currentHP -= damage;
		if(target.currentHP <= 0){
			GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(target, GEngine.getInstance().getUnit(target).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
			State.activeState.unitList.remove(target.id);
		}
		Gdx.app.log("PlayerAbility", "Thunderbolt was cast");
		List<Unit> list = this.getAffectedUnits(tile);
		for(Unit unit : list) {
			unit.currentHP -= damage;
			if(unit.currentHP <= 0){
				GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(unit, GEngine.getInstance().getUnit(target).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
				State.activeState.unitList.remove(target.id);
			}
		}
		Animation animation = new ThunderboltAnimation(target, list, damage);
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		Unit targetUnit = StateUtils.getUnitByTile(target);
		return super.isAllowed(player) && 
				targetUnit != null &&
				targetUnit.ownerId != player.playerId;
	}
	
	private List<Unit> getAffectedUnits(TileCoordinate tc) {
		List<Unit> res = new ArrayList<Unit>();
		if (State.activeState.map.getTile(tc).terrain.equals(TerrainType.SHALLOW_WATER)) {
			List<Tile> temp = State.activeState.map.getNeighbors(tc);
			for(int i = 0; i < temp.size(); i++) {
				Tile tile = temp.get(i);
				if (tile.terrain.equals(TerrainType.SHALLOW_WATER)) {
					if (StateUtils.getUnitByTile(tile.coordinates) != null)
						res.add(StateUtils.getUnitByTile(tile.coordinates));
					for(Tile t : State.activeState.map.getNeighbors(tile)) {
						if (!temp.contains(t) && t.terrain.equals(TerrainType.SHALLOW_WATER)) {
							temp.add(t);
						}
					}
				}
			}
		}
		
		return res;
	}
}

	