package com.anstrat.ai;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.command.AttackCommand;
import com.anstrat.command.CaptureCommand;
import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.command.EndTurnCommand;
import com.anstrat.command.MoveCommand;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.Path;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.ActionMap;
/**
 * A first version of the ScriptAI, will refer to itself as godlikePlayer
 * @author Tomas
 *
 */
public class ScriptAI implements IArtificialIntelligence {
	
	/*
	 * The AI will attempt to do different kind of actions in the following way.
	 * 1. Buy units for the gold at hand.
	 * 2. Attack enemy units in range, applying its attack algorithm to determine in which order to attack.
	 * 3. Capture Buildings not owning, occupied by one of its own units.
	 * 4. Walk for closest enemy unit, will only make the movement if it can make an attack this turn as well
	 * 5. Walk for the closest building not already in possession, the AI need to have at least 2 AP to consider this move
	 * 6. No enemy units in play nor any building not owned or occupied, 
	 * walk towards enemy's mainbase, although always saves at least 3 ap
	 * 7. End turn
	 */
	
	Player godlikePlayer;
	int godlikePlayerId;
	ActionMap actionMap = new ActionMap();
	
	@Override
	public Command generateNextCommand() {
		State state = State.activeState;
		
		godlikePlayer = state.getCurrentPlayer();
		godlikePlayerId = godlikePlayer.playerId;
		
		//Buy algorithm
		Command createCommand = null;
		if(godlikePlayer.gold >= UnitType.BERSERKER.cost){
			if(getMyUnits().size() < 2){
				createCommand = CommandHandler.generateCreateCommand(UnitType.BERSERKER);
				if(createCommand != null && createCommand.isAllowed())
					return createCommand;
			}
		}
			
		if (godlikePlayer.gold >= UnitType.SWORD.cost){
			if (getMyUnits().size() <= 3 || getMyUnits().size() >= 6 && getMyUnits().size() < 8){
				createCommand = CommandHandler.generateCreateCommand(UnitType.SWORD);
			}
			else if(getMyUnits().size() > 7){
				createCommand = CommandHandler.generateCreateCommand(UnitType.HAWK);
			}
			else {
				createCommand = CommandHandler.generateCreateCommand(UnitType.AXE_THROWER);
			}
		}
		if (godlikePlayer.gold >= UnitType.AXE_THROWER.cost)
			if (getMyUnits().size() > 1 && getMyUnits().size() < 5)
				createCommand = CommandHandler.generateCreateCommand(UnitType.AXE_THROWER);
		if(createCommand != null && createCommand.isAllowed())
			return createCommand;
		
		
		// Attack Algoritm
		List<Unit> attackingOrder = sortInAttackingOrder(getMyUnits());
		
		
		for(Unit myUnit : attackingOrder){
			int unitRange = myUnit.getMaxAttackRange();
			if(myUnit.currentAP >= 2){
				for(Tile tile : getTilesPossibleForAttacks(unitRange)){
					if(tile.coordinates.equals(myUnit.tileCoordinate)){
						List<Unit> orderToAttack = sortInOrderToAttack(getEnemyUnits(),myUnit);
						for(Unit enemyUnit : orderToAttack){ 
							if (Pathfinding.getDistance(myUnit.tileCoordinate,enemyUnit.tileCoordinate) <= unitRange){
							Command attackCommand = new AttackCommand(myUnit,enemyUnit);
							if(attackCommand.isAllowed())
								return attackCommand;
							}
						}
					}
				}	
			}
		}
		
		// Capture Algorithm
		for(Unit myUnit : getMyUnits()){
			for (Building buildingsNotOwnedByMe : getBuildingsNotOwnedByMe()){
				CaptureCommand captureCommand;
				if(myUnit.tileCoordinate == buildingsNotOwnedByMe.tileCoordinate){
					captureCommand = new CaptureCommand(buildingsNotOwnedByMe,myUnit,state.getCurrentPlayer());
					if(captureCommand.isAllowed())
						return captureCommand;
				}
			}
		}
		
		
		// Walk to enemy unit algorithm
		List<TileCoordinate> chosenCoordinates = new ArrayList<TileCoordinate>();
		Command moveCommand;
		
		for(Unit myUnit : getMyUnits()){
			int unitRange = myUnit.getMaxAttackRange();
			actionMap.prepare(myUnit);
				for(Tile t : getTilesPossibleForAttacks(unitRange)){  //find the nearest tile, with regard to the unit considered by AIKnowledge
					if(actionMap.getActionType(t.coordinates) != ActionMap.ACTION_NULL &&
							actionMap.getCost(t.coordinates) < (myUnit.currentAP-2)){
						chosenCoordinates.add(t.coordinates);
					}
				}
				List<TileCoordinate> walkOrder = sortInCostOrder(chosenCoordinates);
				if (walkOrder != null){
					for (TileCoordinate t : walkOrder){
						moveCommand = new MoveCommand(myUnit, t);
						if(moveCommand.isAllowed()){
							return moveCommand;		
						}
					}
				}
		}
		
		
		// Walk for closest building algorithm
		int pathCost = Integer.MAX_VALUE;
		Path chosenPath = null;
		Path p = null;
		
		Path p1;
		int longestPathValue = 0;
		TileCoordinate lastTileInPath = null;
		MoveCommand moveforNearestBuildingCommand = null;
		
		
		for (Unit myUnit : getMyUnits()){
			if (myUnit.currentAP >= 2 && myUnit.getUnitType() != UnitType.HAWK && UnitType.WOLF != myUnit.getUnitType()){
				for (Building b : getBuildingsNotOwnedByMeAndNotYetOccupied()){ // Could change to enemybuildings if you want the AI to not pursuit neutral buildings
					p = Pathfinding.getUnitPath(myUnit, b.tileCoordinate);
					if (p.path != null){
						if(pathCost > p.getPathCost(myUnit.getUnitType())){
						pathCost = p.getPathCost(myUnit.getUnitType());
						chosenPath = p;
						}	
					}
				}
		
				// walk as long as possible on chosen path
				if(chosenPath != null){
					for (TileCoordinate t : chosenPath.path){
						p1 = Pathfinding.getUnitPath(myUnit,t);
						if (p1.path != null){
							int p1Cost = p1.getPathCost(myUnit.getUnitType());
							
							if(myUnit.currentAP >= p1Cost && p1Cost > longestPathValue && StateUtils.getUnitByTile(t) ==null ){
								longestPathValue = p1Cost;
								lastTileInPath = t;
							}
						}
					}
				}
			}
			
			moveforNearestBuildingCommand = new MoveCommand(myUnit, lastTileInPath);
			if(moveforNearestBuildingCommand.isAllowed())
				return moveforNearestBuildingCommand;
		}
		
		
		//No building is not yet taken or occupied, all units should start to make it for the enemy mainbase
		TileCoordinate tileInPathToAimFor = null;
		
		Building b =null;
		for (Unit myUnit : getMyUnits()){
			if (myUnit.currentAP >= 4){
				for (Building building : getEnemyBuildings()){
					if (Building.TYPE_CASTLE == building.type){
						b = building;
					}
				}
				p = Pathfinding.getUnitPath(myUnit, b.tileCoordinate);
				if(p!=null && p.path!=null){
					for (TileCoordinate t : p.path){
						p1 = Pathfinding.getUnitPath(myUnit,t);
						if (p1 != null){
							int p1Cost = p1.getPathCost(myUnit.getUnitType());
							
							if(myUnit.currentAP-2 >= p1Cost && p1Cost > longestPathValue && StateUtils.getUnitByTile(t) ==null ){
								longestPathValue = p1Cost;
								tileInPathToAimFor = t;
							}
						}
					}
					MoveCommand moveforEnemyBaseCommand = new MoveCommand(myUnit, tileInPathToAimFor);
					if(moveforEnemyBaseCommand.isAllowed())
						return moveforEnemyBaseCommand;
				}
			}
		}
		
		// Here I, godlikeAI, will be conquering the world leaving it to you to catch up		
		return new EndTurnCommand();
	}



	/**
	 * Gives you all the units belonging to you
	 * @return All units belonging to the current player in an ArrayList
	 */
	public List<Unit> getMyUnits (){
		List<Unit> myUnits = new ArrayList<Unit>();
		
		for(Unit u : State.activeState.unitList.values()){
			if (u.ownerId == godlikePlayerId)
				myUnits.add(u);
		}
		return myUnits;
	}
	/**
	 * Gives you all the units belonging to your enemy
	 * @return All units belonging to the other player in an ArrayList
	 */
	public List<Unit> getEnemyUnits(){
	List<Unit> enemyUnits = new ArrayList<Unit>();
		
		for(Unit u : State.activeState.unitList.values()){
			if (u.ownerId != godlikePlayerId)
				enemyUnits.add(u);
		}
		return enemyUnits;
	}
	
	/**
	 * Gives you all buildings not belonging to you
	 *@return All buildings i don't own in an ArrayList 
	 */
	public List<Building> getBuildingsNotOwnedByMe(){
		List<Building> buildingsNotOwnedByMe = new ArrayList<Building>();
		for(Building b : State.activeState.map.buildingList.values()){
			if (b.controllerId != godlikePlayerId)
				buildingsNotOwnedByMe.add(b);
		}
		return buildingsNotOwnedByMe;
	}
	
	/**
	 * @return All buildings belonging to the enemy in an ArrayList
	 */
	public List<Building> getEnemyBuildings(){
		List<Building> enemyBuildings = new ArrayList<Building>();
		for(Building b : State.activeState.map.buildingList.values()){
			if (b.controllerId == (godlikePlayerId+1)%2)
				enemyBuildings.add(b);
		}
		return enemyBuildings;
	}
	
	/**
	 * Gives you all buildings which is not yours and you don't have a unit on
	 * @return ArrayList with buildings not owning nor occupying
	 */
	public List<Building> getBuildingsNotOwnedByMeAndNotYetOccupied(){
		List<Building> buildingsNotOwnedByMe = getBuildingsNotOwnedByMe();
		
		for (Building b : getBuildingsNotOwnedByMe()){
			for (Unit u : getMyUnits()){
				if (u.tileCoordinate == b.tileCoordinate){
					buildingsNotOwnedByMe.remove(b);
				}
			}
			
		}
		return buildingsNotOwnedByMe;
		
	}
	
	/**
	 * @ArrayList of tiles which is adjacent to a enemyUnit
	 */
	public List<Tile> getAdjacentEnemyTiles(){
		List<Tile> nearbyTiles = new ArrayList<Tile>();
		for(Unit enemyUnit : getEnemyUnits()){  
			nearbyTiles.addAll(State.activeState.map.getNeighbors(enemyUnit.tileCoordinate));
		}
		return nearbyTiles;
	}
	
	/**
	 * Gives you all the tiles which is possible for a specified unit to execute an attack from
	 * @param the unit's range
	 * @return ArrayList of tiles from which is a unit can make an attack
	 */
	
	public List<Tile> getTilesPossibleForAttacks (int range){
		List<Tile> TilesPossibleForRangedAttacks = new ArrayList<Tile>();
		List<Tile> temp = new ArrayList<Tile>();
		TilesPossibleForRangedAttacks.addAll(getAdjacentEnemyTiles());
		
		for(int i=1; i < range; i++){
			for (Tile t : TilesPossibleForRangedAttacks){
				temp.addAll(State.activeState.map.getNeighbors(t.coordinates));
			}
			TilesPossibleForRangedAttacks.addAll(temp);
			temp.clear();
		}
		
		return TilesPossibleForRangedAttacks;
		
	}
	
	/**
	 * Sort my units in the order they should attack
	 * @param List of my units
	 * @return A sorted List of my units in the order to attack
	 */
	
	public List<Unit> sortInAttackingOrder(List<Unit> myUnits ){
		List<Unit> attackOrder = new ArrayList<Unit>();
		// should sort on AP, lowest first, works but maybe a testcase :( buhu
		for (Unit u: myUnits){
			int attackorderSize = attackOrder.size();
			for (int i=0; i <= attackorderSize; i++){
				if(attackOrder.size() == i){
					attackOrder.add(u);
				}
				else if(u.currentAP < attackOrder.get(i).currentAP ){
					attackOrder.add(i,u);
				}		
			}
		}	
		
		return attackOrder;
	}
	
	/**
	 * Take a list of the enemy units and sorts them in the order to attack them, different for every unit
	 * @param enemyUnits
	 * @return A list of enemy units in the order to attack them
	 */
	public List<Unit> sortInOrderToAttack(List<Unit> enemyUnits, Unit myUnit){
		/*
		 * If both at a distance requiring no movement, hit the one with most HP first given (HP <= ATT)
				Hit the one with (HP <= ATT) before sort the rest on lowest HP first
		 */
		List<Unit> attackOrder = new ArrayList<Unit>();
		
		for (Unit u: enemyUnits){
			int attackorderSize = attackOrder.size();
			for (int i=0; i <= attackorderSize; i++){
				if(attackOrder.size() == i){
					attackOrder.add(u);
				}
				
				else if(u.currentHP > attackOrder.get(i).currentHP && myUnit.getAttack() >= u.currentHP ){
					attackOrder.add(i,u);
				}
				else if(attackOrder.get(i).currentHP > myUnit.getAttack() && myUnit.getAttack() >= u.currentHP){
					attackOrder.add(i,u);
				}
				else if(attackOrder.get(i).currentHP > myUnit.getAttack() && myUnit.getAttack() < u.currentHP && u.currentHP < attackOrder.get(i).currentHP){
					attackOrder.add(i,u);
				}
			}
		}	
		
		return attackOrder;
	}
	
	
	/**
	 * Sorts the list of tiles to get in range to attack the enemy unit so that the cheapest cost in AP is first
	 * @param A list of tiles possible to attack an enemy unit
	 * @return List of TileCordinatess sorted on the most preferable Tile first in order to reach enemy 
	 */
	public List<TileCoordinate> sortInCostOrder(List<TileCoordinate> chosenCoordinates){
		if (chosenCoordinates == null || chosenCoordinates.size() == 0)
			return null;
		List<TileCoordinate> walkOrder = new ArrayList<TileCoordinate>();
	
		for (TileCoordinate t: chosenCoordinates){
			int walkOrderSize = walkOrder.size();
			for (int i=0; i <= walkOrderSize; i++){
				if(walkOrder.size() == i){
					walkOrder.add(t);
				}
				else if(actionMap.getCost(t) < actionMap.getCost(walkOrder.get(i)) ){
					walkOrder.add(i,t);
				}		
			}
		}	
		return walkOrder;
	}
	
	
}


		