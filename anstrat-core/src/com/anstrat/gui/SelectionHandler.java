package com.anstrat.gui;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.MoveCameraAnimation;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.abilities.TargetedAbility;
import com.anstrat.geography.TileCoordinate;

public class SelectionHandler {
	public static final int SELECTION_UNIT = 1;
	public static final int SELECTION_EMPTY = 2;
	public static final int SELECTION_SPAWN = 3;
	/** Used for targeted abilities */
	public static final int SELECTION_TARGETED_ABILITY = 5;
	public static final int SELECTION_BUILDING = 6;
	
	public GTile gTile;
	public Unit selectedUnit = null;
	public UnitType spawnUnitType = null;
	public Building selectedBuilding = null;
	public TargetedAbility selectedTargetedAbility = null;
	public int selectionType = SELECTION_EMPTY;
	
	
	public void selectUnit(Unit unit){
		if(unit != null){
			selectedUnit = unit;
			selectionType = SELECTION_UNIT;
			GEngine.getInstance().userInterface.showUnit(unit);
			GEngine.getInstance().highlighter.showRange(unit.tileCoordinate, unit.getMaxAttackRange());
			
			if(unit.ownerId == State.activeState.currentPlayerId)
				GEngine.getInstance().actionMap.prepare(unit);
		}
	}
	public void selectAbility(Unit source, TargetedAbility ability){
		selectedUnit = source;
		selectedTargetedAbility = ability;
		selectionType = SELECTION_TARGETED_ABILITY;
		
		List<TileCoordinate> highlights = ability.getValidTiles(source);
		if(highlights.isEmpty()){
			deselect();
			return;
		}
		GEngine.getInstance().highlighter.highlightTiles(highlights);
		GEngine.getInstance().highlighter.setOutline(highlights, Highlighter.BORDER_ABILITY);
		
		
	}
	public void selectBuilding(Building building){
		if(building != null){
			selectedBuilding = building;
			selectionType = SELECTION_BUILDING;
		}
	}
	public void selectSpawn(UnitType unitType){
		spawnUnitType = unitType;
		selectionType = SELECTION_SPAWN;
		
		List<TileCoordinate> adjacent = (List<TileCoordinate>) StateUtils.getAdjacentTiles(StateUtils.getCurrentPlayerCastle().tileCoordinate);
		adjacent.add(StateUtils.getCurrentPlayerCastle().tileCoordinate);
		// Remove invalid tiles.
		List<TileCoordinate> highlights = new ArrayList<TileCoordinate>();
		for(TileCoordinate tc : adjacent){
			if(unitType.getTerrainPenalty(GEngine.getInstance().map.getTile(tc).tile.terrain)!=Integer.MAX_VALUE
					&& StateUtils.getUnitByTile(tc)==null)
				highlights.add(tc);
		}
		
		GEngine.getInstance().highlighter.highlightTiles(highlights);
		GEngine.getInstance().highlighter.setOutline(highlights, Highlighter.BORDER_SPAWN);
		
		Animation animation = new MoveCameraAnimation(GEngine.getInstance().map.getTile(StateUtils.getCurrentPlayerCastle().tileCoordinate).getCenter());
		GEngine.getInstance().animationHandler.runParalell(animation);
	}
	public void deselect(){
		GEngine gEngine = GEngine.getInstance();
		gEngine.highlighter.clearHighlights();
		selectionType = SELECTION_EMPTY;
		gEngine.actionMap.clear();
		gEngine.userInterface.showUnit(null);
	}
	
	
}
