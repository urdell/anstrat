package com.anstrat.gameCore;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.FloatingTextAnimation;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.gameCore.effects.TriggerOnTurnEnd;
import com.anstrat.gameCore.effects.TriggerOnTurnStart;
import com.anstrat.geography.Map;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * 
 * @author Anton
 *
 * A complete gamestate for a single instance of a game.
 *
 */
public class State implements Serializable{
	
	private static final long serialVersionUID = 2L;
	
	public static State activeState;
	
	public final Map map;
	public HashMap<Integer,Unit> unitList;
	public int nextUnitId;
	public int turnNr = 1;
	public Random random;
	
	public final Player[] players;
	
	/** Index in players */
	public int currentPlayerId = 0;
	
	public State(Map map, Player[] players){
		this.map = map;
		this.players = players;
		this.random = new Random();
		this.unitList = new HashMap<Integer, Unit>();
	}
	
	public State(Map map, Player[] players, long randomSeed){
		this(map, players);
		this.random.setSeed(randomSeed);
	}
	
	// TODO: validation delegation
	public void addUnit(TileCoordinate tilecoordinate, Unit unit){
		unitList.put(unit.id, unit);
		unit.tileCoordinate = map.tiles[tilecoordinate.x][tilecoordinate.y].coordinates;
	}
	
	private int[] tempIncome = new int[2];
	
	public void endTurn(){
		
		currentPlayerId = (currentPlayerId + 1) % players.length;
		Player player = State.activeState.getCurrentPlayer();
		
		List<Unit> unitsToBeRemoved = new ArrayList<Unit>();
		for(Unit u : unitList.values()){
			
			// Update AP for all units
			u.attacksThisTurn = 0;
			if(u.ownerId == currentPlayerId){
				u.currentAP += u.getAPReg();
				if (u.currentAP > u.getMaxAP()) u.currentAP = u.getMaxAP();	// Limit to max AP
				
				List<Effect> effectsToBeRemoved = new ArrayList<Effect>();
				for(Effect effect : u.effects){
					if(effect instanceof TriggerOnTurnEnd){
						((TriggerOnTurnEnd) effect).triggerOnTurnEnd(u);
					}
					if(effect.sheduledRemove)
						effectsToBeRemoved.add(effect);
				}
				u.effects.removeAll(effectsToBeRemoved);
				
				for(Effect effect : u.effects){
					if(effect instanceof TriggerOnTurnStart){
						((TriggerOnTurnStart) effect).triggerOnTurnStart(u);
					}
				}
			}
			
			
			// Regenerate HP for all units currently located in a friendly building
			for (Building building : State.activeState.map.buildingList.values()){
				if(u.tileCoordinate == building.tileCoordinate && u.ownerId == building.controllerId && u.ownerId == currentPlayerId){
					int HPMissing = u.getMaxHP() - u.currentHP;
					int HPReg = Math.min(u.getHPReg(), HPMissing);
					u.currentHP += HPReg;
					
					// Show animation
					if(HPReg > 0) GEngine.getInstance().animationHandler.enqueue(new FloatingTextAnimation(u.tileCoordinate, String.valueOf(HPReg), Color.GREEN));
				}
			}
			if(u.currentHP <= 0)
				unitsToBeRemoved.add(u);
			
		}
		for(Unit u : unitsToBeRemoved){
			u.resolveDeath();
			GEngine.getInstance().animationHandler.enqueue(new DeathAnimation(u, GEngine.getInstance().getUnit(u).isFacingRight()?new Vector2(-1f,0f):new Vector2(1f,0f)));
		}
		//Regenerate capture-points if no unit is occupying village
		
		for (Building building : State.activeState.map.buildingList.values()){
			boolean buildingNotOccupiedByEnemyUnit = true;
			if(building.controllerId == currentPlayerId){
				for(Unit u : unitList.values()){
					if(u.ownerId != currentPlayerId && u.tileCoordinate == building.tileCoordinate){
						buildingNotOccupiedByEnemyUnit = false;
					}
				}
				if(buildingNotOccupiedByEnemyUnit){
					int capturePointMissing = (building.captureCost - building.capturePointsRemaining);
					int capturePointReg = Math.min(building.capturePointReg, capturePointMissing);
					building.capturePointsRemaining += capturePointReg;
					
				}
			}
		}
		
		
		// Update gold and mana income
		getIncome(currentPlayerId, tempIncome);
		player.gold += tempIncome[0];
		player.mana += tempIncome[1];
		
		// Update turn information
		turnNr++;
	}
	
	/**
	 * Writes the given player's income to the given array.
	 * Array must be of length 2 or greater.
	 * Index 0: gold income
	 * Index 1: mana income
	 * @param player
	 */
	public void getIncome(int playerID, int[] out){
		int gold = 0, mana = 0;
		
		for (Building building : State.activeState.map.buildingList.values()){
			if (building.controllerId == playerID){
				gold += building.goldIncome;
				mana += building.manaIncome;
			}
		}
		
		out[0] = gold;
		out[1] = mana;
	}
	
	public Player getCurrentPlayer(){
		return players[currentPlayerId];
	}
	
	public boolean isUserCurrentPlayer(){
		return GameInstance.activeGame.getUserPlayer().playerId == currentPlayerId;
	}
}
