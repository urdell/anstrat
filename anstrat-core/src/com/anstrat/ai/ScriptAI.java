package com.anstrat.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.anstrat.command.ActivateAbilityCommand;
import com.anstrat.command.ActivatePlayerAbilityCommand;
import com.anstrat.command.ActivateTargetedAbilityCommand;
import com.anstrat.command.AttackCommand;
import com.anstrat.command.CaptureCommand;
import com.anstrat.command.Command;
import com.anstrat.command.CreateUnitCommand;
import com.anstrat.command.EndTurnCommand;
import com.anstrat.command.MoveCommand;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
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
	 * 1. Using Divine Intervention
	 * 2. Buy units for the gold at hand.
	 * 3. Activate abilities if profitable
	 * 4. Attack enemy units in range, applying its attack algorithm to determine in which order to attack.
	 * 5. Walk for the closest building not already in possession or occupied by one of its own units,
	 * start with a unit which can reach a building prioritize unit closest to a building
	 * 6. Walk for closest enemy unit, will only make the movement if it can make an attack this turn as well
	 * 7. No enemy units in play nor any building not owned or occupied, 
	 * walk towards enemy's mainbase, although always saves at least 3 ap
	 * 8. End turn
	 */
	
	private Player godlikeAI;
	private ActionMap actionMap = new ActionMap();
	
	public ScriptAI(Player player){
		this.godlikeAI = player;
	}
	
	@Override
	public Command generateNextCommand() {
		
		/*Divine Intervention
		 * 
		 */
		
		Command DivineIntervention = new ActivatePlayerAbilityCommand(this.godlikeAI,PlayerAbilityType.ODINS_BLESSING);
		if(DivineIntervention.isAllowed()){
			return DivineIntervention;
		}
		
		/* Buy algorithm
		 * 
		 */
		Command createCommand = null;
		if(getEnemyUnits().size() < 3) {
			int i =0;
			try {
				 i = Random.class.newInstance().nextInt(6);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (i ==0){
				if(godlikeAI.gold >= UnitType.SWORD.cost){
					createCommand = generateCreateCommand(UnitType.SWORD);
				}
			}
			if (i==1){
				if(godlikeAI.gold >= UnitType.BERSERKER.cost){
					createCommand = generateCreateCommand(UnitType.BERSERKER);
				}
			}
			if (i==2){
				if(godlikeAI.gold >= UnitType.SHAMAN.cost){
					createCommand = generateCreateCommand(UnitType.SHAMAN);
				}
			}
			if (i==3){
				if(godlikeAI.gold >= UnitType.HAWK.cost){
					createCommand = generateCreateCommand(UnitType.HAWK);
				}
			}
			if (i==4){
				if(godlikeAI.gold >= UnitType.AXE_THROWER.cost){
					createCommand = generateCreateCommand(UnitType.AXE_THROWER);
				}
			}
			if (i==5){
				if(godlikeAI.gold >= UnitType.WOLF.cost){
					createCommand = generateCreateCommand(UnitType.WOLF);
				}
			}
			if(createCommand != null && createCommand.isAllowed())
				return createCommand;
			
		}
		
		if(getEnemyUnits().size() >= 3) {
			if(godlikeAI.gold >= UnitType.BERSERKER.cost){
				if(getMyUnits().size() < 2){
					createCommand = generateCreateCommand(UnitType.BERSERKER);
					if(createCommand != null && createCommand.isAllowed())
						return createCommand;
				}
			}
				
			if (godlikeAI.gold >= UnitType.SWORD.cost){
				if (getMyUnits().size() <= 3 || getMyUnits().size() >= 6 && getMyUnits().size() < 8){
					createCommand = generateCreateCommand(UnitType.SWORD);
				}
				else if(getMyUnits().size() > 7 || getMyUnits().size() < 3){
					createCommand = generateCreateCommand(UnitType.HAWK);
				}
				else {
					createCommand = generateCreateCommand(UnitType.AXE_THROWER);
				}
			}
			if (godlikeAI.gold >= UnitType.AXE_THROWER.cost)
				if (getMyUnits().size() > 1 && getMyUnits().size() < 5)
					createCommand = generateCreateCommand(UnitType.AXE_THROWER);
			if(createCommand != null && createCommand.isAllowed())
				return createCommand;
		}
		
		/* Abilities Algoritm
		 * Should only activate its ability if it has a good target or profitable
		 */
		List<Unit> attackingOrder = sortInAttackingOrder(getMyUnits());
		
		
			for(Unit myUnit : attackingOrder){
				
				if(myUnit.currentAP >= myUnit.getAPCostToActivateAbility()){
					
				/* Berserk
				 * Will not attempt his ability in this version
				 */
						if(myUnit.getUnitType().equals(UnitType.BERSERKER)){
							
						}
				/* Axe-thrower
				 * Attempts chain axe if at least two enemies stands adjacent
				 */
						if(myUnit.getUnitType().equals(UnitType.AXE_THROWER)){
								int unitRange = myUnit.getMaxAttackRange();
								
									for(Tile tile : getTilesPossibleForAttacks(unitRange)){
										if(tile.coordinates.equals(myUnit.tileCoordinate)){
											List<Unit> orderToAttack = sortInOrderToAttack(getEnemyUnits(),myUnit);
											for(Unit enemyUnit : orderToAttack){ 
												if (Pathfinding.getDistance(myUnit.tileCoordinate,enemyUnit.tileCoordinate) <= unitRange){
													for(Tile tile42 : getAdjacentTiles(enemyUnit)){
														Unit unit42 = StateUtils.getUnitByTile(tile42.coordinates);
														if(unit42 != null){
															if(unit42.ownerId == enemyUnit.ownerId){
																Command targetedAbilityCommand = new ActivateTargetedAbilityCommand(myUnit, enemyUnit.tileCoordinate,0);
																if(targetedAbilityCommand.isAllowed()){
																	return targetedAbilityCommand;
																} 
															}
														}
													}
												}
											}
										}
									}	
								
							}
						
				/* Shaman 
				 * Only heals units currently in range lacking at least 10 hp
				 */
						if(myUnit.getUnitType().equals(UnitType.SHAMAN)){
							for(Tile tile : getTilesPossibleToHeal(myUnit)){
								Unit unit = StateUtils.getUnitByTile(tile.coordinates);
								if(unit != null){
									if(unit.ownerId == myUnit.ownerId && (unit.getMaxHP()-10)>= unit.currentHP ){
										Command targetedAbilityCommand = new ActivateTargetedAbilityCommand(myUnit, unit.tileCoordinate,0);
										if(targetedAbilityCommand.isAllowed()){
											return targetedAbilityCommand;
										}
									}
								}
							}
						}
				/* Swordsman
				 *  Uses his ability if any enemy unit is adjacent 
				 *  Could be potientially include if any ranged is in attack range
				 */
						if(myUnit.getUnitType().equals(UnitType.SWORD)){
							for(Tile tile : getAdjacentTiles(myUnit)){
								Unit enemyUnit = StateUtils.getUnitByTile(tile.coordinates);
								if(enemyUnit != null){
									if (enemyUnit.ownerId != myUnit.ownerId){
										Command abilityCommand = new ActivateAbilityCommand(myUnit, 0);
										if(abilityCommand.isAllowed()){
											return abilityCommand;
										}
									}
								}
							}	
						}
				/* Hawk
				 * AI will not attempt this in this version
				 */
						if(myUnit.getUnitType().equals(UnitType.HAWK)){
							
						}
				/* Wolf
				 * Attempt to use his ability if he lacks at least 5 hp
				 */
						if(myUnit.getUnitType().equals(UnitType.WOLF)){
							if (myUnit.currentHP<=(myUnit.getMaxHP()-5)){
								for(Tile tile : getTilesPossibleForAttacks(myUnit.getMaxAttackRange())){
									if(tile.coordinates.equals(myUnit.tileCoordinate)){
										List<Unit> orderToAttack = sortInOrderToAttack(getEnemyUnits(),myUnit);
										for(Unit enemyUnit : orderToAttack){ 
											if (Pathfinding.getDistance(myUnit.tileCoordinate,enemyUnit.tileCoordinate) <= myUnit.getMaxAttackRange()){
												Command targetedAbilityCommand = new ActivateTargetedAbilityCommand(myUnit, enemyUnit.tileCoordinate,0);
												if(targetedAbilityCommand.isAllowed()){
													return targetedAbilityCommand;
												}
											}
										}
									}
								}
							}
						}
				}
			}
		
		/* Attack Algoritm
		 *  
		 */
		
		
		for(Unit myUnit : attackingOrder){
			int unitRange = myUnit.getMaxAttackRange();
			if(myUnit.currentAP >= myUnit.getAPCostAttack()){
				for(Tile tile : getTilesPossibleForAttacks(unitRange)){
					if(tile.coordinates.equals(myUnit.tileCoordinate)){
						List<Unit> orderToAttack = sortInOrderToAttack(getEnemyUnits(),myUnit);
						for(Unit enemyUnit : orderToAttack){ 
							if (Pathfinding.getDistance(myUnit.tileCoordinate,enemyUnit.tileCoordinate) <= unitRange){
							Command attackCommand = generateAttackCommand(myUnit, enemyUnit);
							if(attackCommand.isAllowed())
								return attackCommand;
							}
						}
					}
				}	
			}
		}
		
		/* Walk for closest building algorithm
		 * 
		 */
		int pathCost = Integer.MAX_VALUE;
		Path chosenPath = null;
		Path p = null;
		
		Path p1;
		int longestPathValue = 0;
		TileCoordinate lastTileInPath = null;
		Command moveforNearestBuildingCommand = null;
		
		
		for (Unit myUnit : getMyUnits()){
			if (myUnit.currentAP >= 2){
				for (Building b : getBuildingsNotOwnedByMeAndNotYetOccupied()){ // Could change to enemybuildings if you want the AI to not pursuit neutral buildings
					p = Pathfinding.getUnitPath(myUnit, b.tileCoordinate);
					if (p.path != null){
						if(pathCost > p.getPathCost(myUnit.getUnitType())){
						pathCost = p.getPathCost(myUnit.getUnitType());
						chosenPath = p;
						//TODO if same pathcost prioritize enemybuilding?
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
			
			moveforNearestBuildingCommand = generateMoveCommand(myUnit, lastTileInPath);
			if(moveforNearestBuildingCommand.isAllowed())
				return moveforNearestBuildingCommand;
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
						moveCommand = generateMoveCommand(myUnit, t);
						if(moveCommand.isAllowed()){
							return moveCommand;		
						}
					}
				}
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
					
					Command moveforEnemyBaseCommand = generateMoveCommand(myUnit, tileInPathToAimFor);
					if(moveforEnemyBaseCommand.isAllowed())
						return moveforEnemyBaseCommand;
				}
			}
		}
		
		// Here I, godlikeAI, will be conquering the world leaving it to you to catch up		
		return generateEndTurnCommand();
	}



	/**
	 * Gives you all the units belonging to you
	 * @return All units belonging to the current player in an ArrayList
	 */
	private List<Unit> getMyUnits (){
		List<Unit> myUnits = new ArrayList<Unit>();
		
		for(Unit u : State.activeState.unitList.values()){
			if (u.ownerId == godlikeAI.playerId)
				myUnits.add(u);
		}
		return myUnits;
	}
	/**
	 * Gives you all the units belonging to your enemy
	 * @return All units belonging to the other player in an ArrayList
	 */
	private List<Unit> getEnemyUnits(){
	List<Unit> enemyUnits = new ArrayList<Unit>();
		
		for(Unit u : State.activeState.unitList.values()){
			if (u.ownerId != godlikeAI.playerId)
				enemyUnits.add(u);
		}
		return enemyUnits;
	}
	
	/**
	 * Gives you all buildings not belonging to you
	 *@return All buildings not own in an ArrayList 
	 */
	private List<Building> getBuildingsNotOwnedByMe(){
		List<Building> buildingsNotOwnedByMe = new ArrayList<Building>();
		for(Building b : State.activeState.map.buildingList.values()){
			if (b.controllerId != godlikeAI.playerId)
				buildingsNotOwnedByMe.add(b);
		}
		return buildingsNotOwnedByMe;
	}
	
	/**
	 * @return All buildings belonging to the enemy in an ArrayList
	 */
	private List<Building> getEnemyBuildings(){
		List<Building> enemyBuildings = new ArrayList<Building>();
		for(Building b : State.activeState.map.buildingList.values()){
			if (b.controllerId == (godlikeAI.playerId+1)%2)
				enemyBuildings.add(b);
		}
		return enemyBuildings;
	}
	
	/**
	 * Gives you all buildings which is not yours and you don't have a unit on
	 * @return ArrayList with buildings not owning nor occupying
	 */
	private List<Building> getBuildingsNotOwnedByMeAndNotYetOccupied(){
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
	private List<Tile> getAdjacentEnemyTiles(){
		List<Tile> nearbyTiles = new ArrayList<Tile>();
		for(Unit enemyUnit : getEnemyUnits()){  
			nearbyTiles.addAll(State.activeState.map.getNeighbors(enemyUnit.tileCoordinate));
		}
		return nearbyTiles;
	}
	
	/**
	 * @ArrayList of tiles which is adjacent to specified unit
	 */
	private List<Tile> getAdjacentTiles(Unit unit){
		List<Tile> nearbyTiles = new ArrayList<Tile>();  
			nearbyTiles.addAll(State.activeState.map.getNeighbors(unit.tileCoordinate));
		return nearbyTiles;
	}
	
	/**
	 * Gives you all the tiles which is possible for a specified unit to execute an attack from
	 * @param the unit's range
	 * @return ArrayList of tiles from which is a unit can make an attack
	 */
	
	private List<Tile> getTilesPossibleForAttacks (int range){
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
	
	private List<Tile> getTilesPossibleToHeal (Unit unit){
		List<Tile> TilesPossibleToRangedHeals = new ArrayList<Tile>();
		List<Tile> temp = new ArrayList<Tile>();
		TilesPossibleToRangedHeals.add(State.activeState.map.getTile(unit.tileCoordinate));
		TilesPossibleToRangedHeals.addAll(getAdjacentTiles(unit));
		
		for(int i=1; i < unit.getMaxAttackRange(); i++){
			for (Tile t : TilesPossibleToRangedHeals){
				temp.addAll(State.activeState.map.getNeighbors(t.coordinates));
			}
			TilesPossibleToRangedHeals.addAll(temp);
			temp.clear();
		}
		
		return TilesPossibleToRangedHeals;
		
	}
	
	/**
	 * Sort my units in the order they should attack
	 * @param List of my units
	 * @return A sorted List of my units in the order to attack
	 */
	
	private List<Unit> sortInAttackingOrder(List<Unit> myUnits ){
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
	private List<Unit> sortInOrderToAttack(List<Unit> enemyUnits, Unit myUnit){
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
	private List<TileCoordinate> sortInCostOrder(List<TileCoordinate> chosenCoordinates){
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
	
	private Command generateEndTurnCommand(){
		return new EndTurnCommand(godlikeAI.playerId);
	}
	private Command generateMoveCommand(Unit unit, TileCoordinate tile){
		return new MoveCommand(godlikeAI.playerId, unit, tile);
	}
	
	private Command generateAttackCommand(Unit attacker, Unit defender){
		return new AttackCommand(godlikeAI.playerId, attacker, defender);
	}
	
	private Command generateCaptureCommand(Building building, Unit unit){
		return new CaptureCommand(godlikeAI.playerId, building, unit);
	}
	
	
	private Command generateCreateCommand(UnitType type){
		TileCoordinate castleCoordinate = null;
		if(StateUtils.getCurrentPlayerCastle() == null)
			return new CreateUnitCommand(godlikeAI.playerId, castleCoordinate, type); // no position -> invalid command.
		castleCoordinate = StateUtils.getCurrentPlayerCastle().tileCoordinate;
		
		// First check if castle position is free
		// TODO: Check terrain and buildings too?
		if(StateUtils.getUnitByTile(castleCoordinate) == null){
			CreateUnitCommand createUnit = new CreateUnitCommand(godlikeAI.playerId, castleCoordinate, type);
			if(createUnit.isAllowed())
				return createUnit;
		}
		
		for(Tile t : State.activeState.map.getNeighbors(castleCoordinate)){
			if(State.activeState.map.isAdjacent(t.coordinates, castleCoordinate))
				
			// Check if something's in the way
			// TODO: Check terrain and buildings too?
			if(StateUtils.getUnitByTile(t.coordinates) == null){
				CreateUnitCommand createUnitC = new CreateUnitCommand(godlikeAI.playerId, t.coordinates, type);
				if(createUnitC.isAllowed()){
					
					System.out.println(t.coordinates);
					return createUnitC;
				}
			}
		}
		
		throw new RuntimeException("AI: Failed to generate a create unit command, could not find a free tile.");
	}
}


		