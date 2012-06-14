package com.anstrat.command;

import com.anstrat.animation.CaptureAnimation;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GameUI;

public class CaptureCommand extends Command{
	
	private static final long serialVersionUID = 1L;
	
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
		
		int tempCaptureCost = captureBuilding.captureCostRemaining;
		captureBuilding.captureCostRemaining -= unit.currentAP;
		
		if (unit.currentAP > tempCaptureCost){
			//GEngine.getInstance().animationHandler.enqueue(new FloatingTextAnimation(unit.tileCoordinate, " " + tempCaptureCost, Color.RED));
			unit.currentAP -= tempCaptureCost;
		}
		else{
			//GEngine.getInstance().animationHandler.enqueue(new FloatingTextAnimation(unit.tileCoordinate, " " + unit.currentAP, Color.RED));
			unit.currentAP = 0;
		}
		GEngine.getInstance().animationHandler.enqueue(new CaptureAnimation(unit, captureBuilding));
		
		if (captureBuilding.captureCostRemaining <= 0){
			captureBuilding.controllerId = playerID;
			captureBuilding.captureCostRemaining = captureBuilding.captureCost;
			
			if(captureBuilding.type == Building.TYPE_CASTLE){
				GameUI.showVictoryPopup(State.activeState.getCurrentPlayer().displayedName);
				
				// Send a "hidden" EndTurnCommand
				CommandHandler.execute(new EndTurnCommand());
				
				// Remove this game
				State.activeState.gameInstance.remove();
			}
		}
	}
	
	@Override
	public boolean isAllowed(){
		Unit unit = State.activeState.unitList.get(unitID);
		Building captureBuilding = State.activeState.map.buildingList.get(buildingID);
		
		return super.isAllowed() &&
					unit.currentAP >= 4 && //Changed for only allowing cap at required AP
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

