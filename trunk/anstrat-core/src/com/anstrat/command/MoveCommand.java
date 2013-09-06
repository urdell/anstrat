package com.anstrat.command;

import com.anstrat.animation.MoveAnimation;
import com.anstrat.animation.UberTextAnimation;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Path;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;

public class MoveCommand extends Command {

	private static final long serialVersionUID = 1L;
	private TileCoordinate endTile;
	private int unitId;
	
	/**
	 * The constructor
	 * @param unit Unit to move
	 * @param endTile Tile coordinates to move unit to
	 */
	public MoveCommand(Unit unit, TileCoordinate endTile)
	{
		this.unitId = unit.id;
		this.endTile = endTile;
	}
	
	public MoveCommand(int playerIndex, Unit unit, TileCoordinate endTile)
	{
		super(playerIndex);
		this.unitId = unit.id;
		this.endTile = endTile;
	}
	
	@Override
	protected void execute() {
		State state = State.activeState;
		Unit unit = state.unitList.get(unitId);
		//GEngine.getInstance().selectionHandler.deselect();
		
		Path path = Pathfinding.getUnitPath(unit, endTile);
		TileCoordinate previousTile = unit.tileCoordinate;
		
		for(int i=0;i<path.path.size();i++){
			TileCoordinate tc = path.path.get(i);
			Building buildbob = StateUtils.getBuildingByTile(tc);
			int curid = State.activeState.currentPlayerId;
			boolean wasNeutral = false;
			
			if(	buildbob!=null && 
				buildbob.type != Building.TYPE_CASTLE && 
				buildbob.controllerId != curid){
				if (buildbob.controllerId == -1) {
					wasNeutral = true;
				}
				buildbob.controllerId = curid;
				UberTextAnimation utah;
				
				if (GameInstance.activeGame.isUserCurrentPlayer()) {
					utah = new UberTextAnimation(tc, "captured-player");
					GEngine.getInstance().animationHandler.runParalell(utah);
				}
				else if (!wasNeutral){
					utah = new UberTextAnimation(tc, "captured-enemy");
					GEngine.getInstance().animationHandler.runParalell(utah);
				}
				
			}
			
			MoveAnimation animation = new MoveAnimation(unit, previousTile, tc);
			
			if(i == 0) animation.setIsFirst();
			if(i == path.path.size() - 1) { 
				animation.setIsLast();
				if(buildbob!=null && buildbob.type == Building.TYPE_CASTLE && buildbob.controllerId != curid){
					UberTextAnimation utah = new UberTextAnimation(tc, "capturing-base-player");
					GEngine.getInstance().animationHandler.enqueue(utah);
					System.out.println(State.activeState.baseCaps==null);
					State.activeState.baseCaps[curid] = State.activeState.turnNr;
				}
			}
			
			GEngine.getInstance().animationHandler.enqueue(animation);
			previousTile = tc;
		}
		
		//GEngine.getInstance().highlighter.highlightTiles(path.path);
		unit.currentAP -= path.getPathCost(unit.getUnitType());
		unit.tileCoordinate = state.map.tiles[endTile.x][endTile.y].coordinates;
		
		Fog.recalculateFog(playerID, State.activeState);
		
		//StateVerifier.verifyState(State.activeState);
		//GEngine.getInstance().actionMap.prepare(unit);
		//GEngine.getInstance().syncToState();
	}
	
	/**
	 * Runs the pathfinding algorithm, might be optimized not to.
	 * @return
	 */
	public int getAPCost(){
		Unit unit = State.activeState.unitList.get(unitId);
		Path path = Pathfinding.getUnitPath(unit, endTile);
		return path.getPathCost(unit.getUnitType());
	}
	
	public Path getPath(){
		Unit unit = State.activeState.unitList.get(unitId);
		
		return Pathfinding.getUnitPath(unit, endTile);
	}

	@Override
	public boolean isAllowed() {
		
		State state = State.activeState;
		Unit unit = state.unitList.get(unitId);
		
		boolean result = true;
		
		/*
		return (super.isAllowed() &&
				unit != null && 
				!unit.tileCoordinate.equals(endTile) &&
				unit.ownerId == state.currentPlayerId &&
				Pathfinding.getUnitRange(unit).contains(endTile) &&
				StateUtils.getUnitByTile(state.map.getTile(endTile).coordinates) == null) &&
				Fog.isVisible(endTile, unit.ownerId);
		*/
		
		System.out.println("UNIT HAS "+unit==null?-1:unit.currentAP+" AP");
		
		result = result && super.isAllowed();
		if(!result){
			System.out.println("************** CASE 1 ****************");
			return false;
		}
		result = result && unit != null;
		if(!result){
			System.out.println("************** CASE 22 ****************");
			return false;
		}
		result = result && !unit.tileCoordinate.equals(endTile);
		if(!result){
			System.out.println("************** CASE 333 ****************");
			return false;
		}
		result = result && unit.ownerId == state.currentPlayerId;
		if(!result){
			System.out.println("************** CASE 4444 ****************");
			return false;
		}
		result = result && Pathfinding.getUnitRange(unit).contains(endTile);
		if(!result){
			System.out.println("************** CASE 55555 ****************");
			return false;
		}
		result = result && StateUtils.getUnitByTile(state.map.getTile(endTile).coordinates) == null;
		if(!result){
			System.out.println("************** CASE 666666 ****************");
			return false;
		}
		result = result && Fog.isVisible(endTile, unit.ownerId);
		if(!result){
			System.out.println("************** CASE 7777777 ****************");
			return false;
		}
		
		return result;
	}

}