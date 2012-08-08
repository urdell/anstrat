package com.anstrat.command;

import com.anstrat.animation.CaptureAnimation;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GameUI;

public class CaptureCommand extends Command{
	
	private static final long serialVersionUID = 1L;
	
	private static final int CAPTURE_COST = 4;
	
	private int buildingID;	// the building being captured
	private int playerID;	
	private int unitID;		// the unit capturing the building
	
	public CaptureCommand(Building building, Unit unit, Player player){
		buildingID = building.id;
		unitID = unit.id;
		playerID = player.playerId;
	}
	
	@Override
	protected void execute(){
		Unit unit = State.activeState.unitList.get(unitID);
		Building captureBuilding = State.activeState.map.buildingList.get(buildingID);
		captureBuilding.capturePointsRemaining -= 1;
		

		unit.currentAP -= CAPTURE_COST;

		GEngine.getInstance().animationHandler.enqueue(new CaptureAnimation(unit, captureBuilding));
		
		if (captureBuilding.capturePointsRemaining <= 0){
			captureBuilding.controllerId = playerID;
			captureBuilding.capturePointsRemaining = captureBuilding.captureCost;
			
			if(captureBuilding.type == Building.TYPE_CASTLE){
				GameUI.showVictoryPopup(State.activeState.getCurrentPlayer().getDisplayName());
				
				// Send a "hidden" EndTurnCommand
				CommandHandler.execute(new EndTurnCommand());
				
				// Remove this game
				Main.getInstance().games.endGame(GameInstance.activeGame);
			}
		}
	}
	
	@Override
	public boolean isAllowed(){
		Unit unit = State.activeState.unitList.get(unitID);
		Building captureBuilding = State.activeState.map.buildingList.get(buildingID);
		
		return super.isAllowed() &&
					unit != null &&
					unit.currentAP >= CAPTURE_COST && //Changed for only allowing cap at required AP
					unit.getUnitType() != UnitType.HAWK &&
					unit.getUnitType() != UnitType.WOLF &&
					unit.tileCoordinate == captureBuilding.tileCoordinate &&
					unit.ownerId == playerID &&
					captureBuilding.controllerId != playerID;
	}
	
	@Override
	public int hashCode(){
		return super.hashCode() + buildingID * 23 + playerID * 7 + unitID * 31;
	}
}

