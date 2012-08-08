package com.anstrat.command;

import com.anstrat.gameCore.CreationHandler;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.Map;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.TerrainType;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;

/**
 * The command that is used when a unit is created
 * @author Ekis
 *
 */
public class CreateUnitCommand extends Command {
	
	private static final long serialVersionUID = 1L;
	private UnitType unitType;
	private TileCoordinate tileCoord;
	
	/**
	 * Constructor
	 * @param tile the Tile that the Unit is created on
	 * @param unitType The type of the unit to be created
	 * @param playerId The player in control of the unit
	 */
	public CreateUnitCommand(Player player, TileCoordinate tileCoord, UnitType unitType) {
		this.tileCoord = tileCoord;
		this.unitType = unitType;
	}
	
	@Override
	protected void execute() {
		CreationHandler.createUnit(tileCoord, unitType, playerID);
		GEngine.getInstance().selectionHandler.deselect();
		Fog.recalculateFog(playerID, State.activeState.map);
	}

	@Override
	public boolean isAllowed() {
		TileCoordinate castleCoordinate = StateUtils.getCurrentPlayerCastle().tileCoordinate;
		TerrainType tileTerrain = State.activeState.map.getTile(tileCoord).terrain;
		
		return super.isAllowed()  
				&& tileCoord != null
				&& StateUtils.getUnitByTile(tileCoord) == null							// Tile is empty
				&& unitType.getTerrainPenalty(tileTerrain) != Integer.MAX_VALUE			// Unit can enter the chosen tile's terrain
				&& State.activeState.players[playerID].gold >= unitType.cost			// Player can afford unit
				&& Pathfinding.getDistance(castleCoordinate, tileCoord) <= 1;			// Within range of castle
	}
	
	@Override
	public String toString(){
		return String.format("%s(type=%s, position=%s, playerID=%d)", this.getClass().getSimpleName(), unitType, tileCoord, playerID);
	}
	
	public String getReason() {
		if (tileCoord == null) 
			return "Cannot recruit";
		else if (StateUtils.getUnitByTile(tileCoord) != null)
			return "Already occupied";
		else if (unitType.getTerrainPenalty(State.activeState.map.getTile(tileCoord).terrain) == Integer.MAX_VALUE)
			return "Impassable terrain";
		else if (State.activeState.players[playerID].gold < unitType.cost)
			return "Not enough gold";
		else if (Pathfinding.getDistance(StateUtils.getCurrentPlayerCastle().tileCoordinate, tileCoord) > 1)
			return "Too far away";
		
		return "";
	}
	public UnitType getUnitType(){
		return unitType;
	}
	public TileCoordinate getTarget(){
		return tileCoord;
	}
	
	@Override
	public int hashCode(){
		return super.hashCode() + unitType.hashCode() * 37 * tileCoord.hashCode() + playerID * 29;
	}
}
