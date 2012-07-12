package com.anstrat.gameCore.playerAbilities;

import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.CometStrikeAnimation;
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
import com.anstrat.gui.GTile;
import com.badlogic.gdx.Gdx;

public class CometStrike extends TargetedPlayerAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int damage = 5;
	
	public CometStrike(Player player) {
		super("comet", "comet", "Deals damage", 40, player, PlayerAbilityType.COMETSTRIKE);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		Unit centerTarget = StateUtils.getUnitByTile(tile);
		if(centerTarget != null){
			centerTarget.currentHP = 0;
			GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(centerTarget));
			State.activeState.unitList.remove(centerTarget.id);
		}
		if (State.activeState.map.getTile(tile).terrain.equals(TerrainType.MOUNTAIN)) {
			GTile gTile = GEngine.getInstance().map.getTile(tile);
			gTile.tile.terrain = TerrainType.FIELD;
			gTile.tile.coordinates = gTile.tile.coordinates; 
			gTile.setTexture(TerrainType.FIELD);
		}
		List<Tile> list = State.activeState.map.getNeighbors(tile);
		for (Tile t: list) {
			Unit unit = StateUtils.getUnitByTile(t.coordinates);
			if (unit != null) {
				unit.currentHP -= damage;
				if(unit.currentHP <= 0){
					GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(centerTarget));
					State.activeState.unitList.remove(centerTarget.id);
				}
				GEngine.getInstance().getUnit(unit).updateHealthbar();
			}
		}
		Animation animation = new CometStrikeAnimation();
		GEngine.getInstance().animationHandler.enqueue(animation);
		
		Gdx.app.log("PlayerAbility", "Comet Strike was cast");
		//Animation animation = new ThunderboltAnimation(target);
		//GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		return super.isAllowed(player);
	}
}
