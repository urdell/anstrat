package com.anstrat.gameCore.playerAbilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anstrat.animation.Animation;
import com.anstrat.animation.CometStrikeAnimation;
import com.anstrat.animation.HuginAndMuninAnimation;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public class HuginAndMunin extends TargetedPlayerAbility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HuginAndMunin(Player player) {
		super(player, PlayerAbilityType.HUGIN_AND_MUNIN);
	}
	
	@Override
	public void activate(Player player, TileCoordinate tile){
		super.activate();
		
		List<Tile> tiles = State.activeState.map.getNeighbors(tile);
		if (State.activeState.map.getTile(tile).visible[GameInstance.activeGame.getUserPlayer().playerId] < 3)
			State.activeState.map.getTile(tile).visible[GameInstance.activeGame.getUserPlayer().playerId] = 3;
		for(Tile t : tiles) {
			if (t.visible[GameInstance.activeGame.getUserPlayer().playerId] < 2) {
				t.visible[GameInstance.activeGame.getUserPlayer().playerId] = 2;
			}
		}
		Fog.recalculateFog(GameInstance.activeGame.getUserPlayer().playerId, State.activeState);
		Animation animation = new HuginAndMuninAnimation(tile);
		
		
		GEngine.getInstance().animationHandler.enqueue(animation);
		
		
		Gdx.app.log("PlayerAbility", "Hugin and Munin was cast");
	}
	
	@Override
	public boolean isAllowed(Player player, TileCoordinate target) {
		return super.isAllowed(player);
	}
}
