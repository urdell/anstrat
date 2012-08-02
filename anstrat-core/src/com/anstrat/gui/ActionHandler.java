package com.anstrat.gui;

import com.anstrat.animation.FullscreenTextAnimation;
import com.anstrat.animation.FloatingTextAnimation;
import com.anstrat.command.ActivateAbilityCommand;
import com.anstrat.command.ActivateDoubleTargetedPlayerAbilityCommand;
import com.anstrat.command.ActivatePlayerAbilityCommand;
import com.anstrat.command.ActivateTargetedAbilityCommand;
import com.anstrat.command.ActivateTargetedPlayerAbilityCommand;
import com.anstrat.command.AttackCommand;
import com.anstrat.command.CaptureCommand;
import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.command.CreateUnitCommand;
import com.anstrat.command.EndTurnCommand;
import com.anstrat.command.MoveCommand;
import com.anstrat.core.GameInstance;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.core.User;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.abilities.TargetedAbility;
import com.anstrat.gameCore.playerAbilities.DoubleTargetedPlayerAbility;
import com.anstrat.gameCore.playerAbilities.PlayerAbility;
import com.anstrat.geography.Pathfinding;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;


/**
 * 
 * Processes high-level user actions
 *
 */
public class ActionHandler {
	
	public boolean showingConfirmDialog = false;
	public GTile confirmTile;
	public Command confirmCommand;

	public void click(GTile gTile, int clickedQuadrant){
		GEngine gEngine = GEngine.getInstance();
		Unit unit = StateUtils.getUnitByTile(gTile.tile.coordinates);
		Command command;
		
		if(showingConfirmDialog){	// if confirm needed, only confirm or cancel.
			if(gTile == confirmTile)
				confirmPress();
			else
				confirmCancelPress();
			return;
		}
		
		switch(gEngine.selectionHandler.selectionType){
		case SelectionHandler.SELECTION_EMPTY:
			gEngine.selectionHandler.gTile = gTile;
			if(unit != null){
				gEngine.selectionHandler.selectUnit(unit);
			}else if( StateUtils.getBuildingByTile(gTile.tile.coordinates) == StateUtils.getCurrentPlayerCastle()){ // clicked own castle
				GameInstance game = State.activeState.gameInstance;
				if(!(game instanceof NetworkGameInstance || game.isAiGame()) ||
						StateUtils.getCurrentPlayerCastle().controllerId==User.globalUserID ||
						State.activeState.players[StateUtils.getCurrentPlayerCastle().controllerId].ai==null)
					Popup.getBuyUnitPopup().show();
			}
			//TODO Might need some more verification than (unit!=null) for that tile contains a unit
			break;
		case SelectionHandler.SELECTION_UNIT:
			Unit selectedUnit = gEngine.selectionHandler.selectedUnit;
			if(unit == null){   //Empty tile clicked
				if(gEngine.actionMap.getActionType(gTile.tile.coordinates) == ActionMap.ACTION_MOVE){
					Command c = new MoveCommand(selectedUnit, gTile.tile.coordinates);
					requestConfirm(gTile, selectedUnit, c, clickedQuadrant);
				}else{
					gEngine.selectionHandler.deselect();
				}
			}
			else{    //unit clicked
				if(unit==gEngine.selectionHandler.selectedUnit){ // current unit
					gEngine.selectionHandler.deselect();
				}
				else if(unit.ownerId == gEngine.selectionHandler.selectedUnit.ownerId && selectedUnit.ownerId!=State.activeState.currentPlayerId){
					
					gEngine.selectionHandler.selectUnit(unit);
					gEngine.selectionHandler.gTile = gTile;
					gEngine.highlighter.highlightTile(unit.tileCoordinate);
				}
				else if(unit.ownerId != State.activeState.currentPlayerId && selectedUnit.ownerId==State.activeState.currentPlayerId){
					Command c = new AttackCommand(gEngine.selectionHandler.selectedUnit, unit);
					if(c.isAllowed())
						requestConfirm(gTile, selectedUnit, c, clickedQuadrant);
					else
						gEngine.animationHandler.runParalell(new FullscreenTextAnimation("Can not attack this"));
				}else{
					gEngine.highlighter.highlightTiles(Pathfinding.getUnitRange(unit));
					gEngine.selectionHandler.selectUnit(unit);
					gEngine.selectionHandler.gTile = gTile;
				}
				
			}
			break;
		case SelectionHandler.SELECTION_BUILDING:
			//Building selectedBuilding = gEngine.selectionHandler.selectedBuilding;
			break;
		case SelectionHandler.SELECTION_SPAWN:
			command = new CreateUnitCommand(State.activeState.getCurrentPlayer(), gTile.tile.coordinates, gEngine.selectionHandler.spawnUnitType);
			if(command.isAllowed())
				requestConfirm(gTile, null, command, clickedQuadrant);
			else{
				gEngine.animationHandler.runParalell(new FullscreenTextAnimation( ((CreateUnitCommand)command).getReason() ));
				deselectPress();
			}
			break;
		case SelectionHandler.SELECTION_TARGETED_ABILITY:
			command = new ActivateTargetedAbilityCommand(gEngine.selectionHandler.selectedUnit, 
													gTile.tile.coordinates, 
													gEngine.selectionHandler.selectedUnit.abilities.indexOf(gEngine.selectionHandler.selectedTargetedAbility));
			if(command.isAllowed())
				requestAbilityConfirm(gTile, gEngine.selectionHandler.selectedUnit, command, gEngine.selectionHandler.selectedTargetedAbility, clickedQuadrant);
			else
				gEngine.selectionHandler.deselect();
			break;
		case SelectionHandler.SELECTION_TARGETED_PLAYER_ABILITY:
			command = new ActivateTargetedPlayerAbilityCommand(State.activeState.getCurrentPlayer(), gTile.tile.coordinates, gEngine.selectionHandler.selectedTargetedPlayerAbility.type);
			if(command.isAllowed())
				requestPlayerAbilityConfirm(gTile, command, gEngine.selectionHandler.selectedTargetedPlayerAbility, clickedQuadrant);
			else
				gEngine.selectionHandler.deselect();
			break;
		case SelectionHandler.SELECTION_DOUBLE_TARGETED_PLAYER_ABILITY:
			DoubleTargetedPlayerAbility temp = gEngine.selectionHandler.selectedDoubleTargetedPlayerAbility;
			if (temp.state == 0) {
				temp.activateFirst(State.activeState.getCurrentPlayer(), gTile.tile.coordinates);
				GEngine.getInstance().selectionHandler.selectPlayerAbility(temp);
			}
			else {
				command = new ActivateDoubleTargetedPlayerAbilityCommand(State.activeState.getCurrentPlayer(), gEngine.selectionHandler.selectedDoubleTargetedPlayerAbility.type, temp.coords, gTile.tile.coordinates);
				if(command.isAllowed())
					requestPlayerAbilityConfirm(gTile, command, gEngine.selectionHandler.selectedDoubleTargetedPlayerAbility, clickedQuadrant);
				else
					gEngine.selectionHandler.deselect();
			}
			break;
		default:
			break;
		
		}

		
		
	}
	
	public void endTurnPress(){
		confirmCancelPress();
		GEngine.getInstance().selectionHandler.deselect();
		Command c = new EndTurnCommand();
		CommandHandler.execute(c);
		GameInstance.saveGameInstances(Gdx.files.local("games.bin"));
	}
	
	public void capturePress(){
		Command c = null;
		
		Player player = State.activeState.getCurrentPlayer();
		Unit selectedUnit = GEngine.getInstance().selectionHandler.selectedUnit;
		if(selectedUnit.ownerId == player.playerId){
			if (selectedUnit != null){
				for (Building building : State.activeState.map.buildingList.values()){
					if (selectedUnit.tileCoordinate == building.tileCoordinate){
						if (building.controllerId != player.playerId && selectedUnit.ownerId == player.playerId){
							c = new CaptureCommand(building, selectedUnit, player);
							
						}
					}
				}
			}
			
			if (c != null){
				requestConfirm(GEngine.getInstance().getMap().getTile(selectedUnit.tileCoordinate), selectedUnit, c, ConfirmDialog.BOTTOM_RIGHT);
			}
		}
	}
	
	public void deselectPress() {
		showingConfirmDialog = false;
		GEngine.getInstance().selectionHandler.deselect();
		
	}
	public void refreshHighlight(Unit unit){
		GEngine gEngine = GEngine.getInstance();
		gEngine.highlighter.highlightTiles(Pathfinding.getUnitRange(unit));
		gEngine.highlighter.showRange(unit.tileCoordinate, unit.getMaxAttackRange());
		GEngine.getInstance().actionMap.prepare(unit);
	}

	public void abilityPress(int i) {
		Unit unit = GEngine.getInstance().selectionHandler.selectedUnit;
		if(unit.ownerId == State.activeState.currentPlayerId){
			Ability ability = unit.getAbilities().get(i);
			if(ability != null){
				if(!(ability instanceof TargetedAbility)){ // not targeted
					Command c = new ActivateAbilityCommand(unit, i);
					requestAbilityConfirm(GEngine.getInstance().getMap().getTile(unit.tileCoordinate), unit, c, ability, ConfirmDialog.BOTTOM_RIGHT);
					//CommandHandler.execute(c);
				}
				
				if(ability instanceof TargetedAbility){
					GEngine.getInstance().selectionHandler.selectAbility(unit, (TargetedAbility)ability);
				}
			}
			GEngine.getInstance().updateUI();
		}
	}
	
	/**
	 * Shows the confirmdialog for a given command.
	 * @param targetTile
	 * @param unit
	 * @param command
	 */
	public void requestConfirm(GTile targetTile, Unit unit, Command command, int clickedQuadrant){
		
		GEngine gEngine = GEngine.getInstance();
		confirmTile = targetTile;
		confirmCommand = command;
		showingConfirmDialog = true;
		int dialogPosition = ConfirmDialog.invertQuadrant(clickedQuadrant);
		if(command instanceof MoveCommand){

			gEngine.confirmDialog = ConfirmDialog.moveConfirm( unit, ((MoveCommand) command).getPath(), dialogPosition );

		}
		else if(command instanceof AttackCommand){
			gEngine.confirmDialog = ConfirmDialog.attackConfirm( unit, StateUtils.getUnitByTile(targetTile.tile.coordinates), dialogPosition );
		}
		else if(command instanceof CaptureCommand){
			gEngine.confirmDialog = ConfirmDialog.captureConfirm( unit, State.activeState.map.getBuildingByTile(targetTile.tile.coordinates), dialogPosition );
		}
		else if(command instanceof CreateUnitCommand){
			gEngine.confirmDialog = ConfirmDialog.buyConfirm( ((CreateUnitCommand)command).getUnitType(), dialogPosition );
		}
	}
	
	/**
	 * Shows the confirmdialog for an ability command (both targeted and non-targeted).
	 * @param targetTile
	 * @param unit
	 * @param ability
	 * @param command
	 */
	public void requestAbilityConfirm(GTile targetTile, Unit unit, Command command, Ability ability, int clickedQuadrant){	
		GEngine gEngine = GEngine.getInstance();
		confirmTile = targetTile;
		confirmCommand = command;
		showingConfirmDialog = true;
		int dialogPosition = ConfirmDialog.invertQuadrant(clickedQuadrant);
		if(command instanceof ActivateAbilityCommand){
			gEngine.confirmDialog = ability.generateConfirmDialog(unit, dialogPosition);

		}
		else if(command instanceof ActivateTargetedAbilityCommand){
			gEngine.confirmDialog = ((TargetedAbility)ability).generateConfirmDialog(unit, targetTile.tile.coordinates, dialogPosition);
		}
	}
	/**
	 * Shows the confirmdialog for a playerAbility command (both targeted and non-targeted).
	 * @param targetTile
	 * @param unit
	 * @param ability
	 * @param command
	 */
	public void requestPlayerAbilityConfirm(GTile targetTile, Command command, PlayerAbility ability, int clickedQuadrant){	
		GEngine gEngine = GEngine.getInstance();
		confirmTile = targetTile;
		confirmCommand = command;
		showingConfirmDialog = true;
		int dialogPosition = ConfirmDialog.invertQuadrant(clickedQuadrant);
		if(command instanceof ActivatePlayerAbilityCommand){
			gEngine.confirmDialog = ability.generateConfirmDialog(dialogPosition);

		}
		else if(command instanceof ActivateTargetedPlayerAbilityCommand){
			gEngine.confirmDialog = ability.generateConfirmDialog(dialogPosition);
		}
		else if(command instanceof ActivateDoubleTargetedPlayerAbilityCommand){
			gEngine.confirmDialog = ability.generateConfirmDialog(dialogPosition);
		}
	}
	
	public void confirmPress(){
		CommandHandler.execute(confirmCommand);
		showingConfirmDialog = false;
		GEngine.getInstance().confirmOverlay.clear();
		
		if(confirmCommand instanceof EndTurnCommand) 	// Check for commands in which selection should be cleared.
		{
			GEngine.getInstance().selectionHandler.deselect();			
		}else if(confirmCommand instanceof CreateUnitCommand){	// select the newly created unit
			Unit newUnit = StateUtils.getUnitByTile(((CreateUnitCommand)confirmCommand).getTarget());
			GEngine.getInstance().selectionHandler.selectUnit(newUnit);
		}
		else{
			Unit selectedUnit = GEngine.getInstance().selectionHandler.selectedUnit;	// Reselect the current unit to make sure things are updated.
			GEngine.getInstance().selectionHandler.selectUnit(selectedUnit);
		}
	}
	public void confirmCancelPress(){
		showingConfirmDialog = false;
		GEngine.getInstance().confirmOverlay.clear();
	}
	
}
