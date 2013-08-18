package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.LeapAttackAnimation;
import com.anstrat.animation.UberTextAnimation;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Combat;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.DamageRow;

public class LeapAttack extends TargetedAbility{
		/**
		 * TODO Copy Paste from Kamikaze right now
		 */
		private static final long serialVersionUID = 1L;
		private static final int AP_COST = 3;
		private static final int RANGE = 1;
		private static final float DAMAGEMULTIPLIER = 1.5f;

		public LeapAttack(){
			super("Berserker Rush", "A rush attack that has the berserker run through an enemy and stop behind it.", AP_COST, RANGE);
			iconName = "leap-button";
		}
		
		public boolean isAllowed(Unit source, TileCoordinate coordinates) {
			Unit targetUnit = StateUtils.getUnitByTile(coordinates);
			if(targetUnit == null)
				return false;
			boolean jumpingTarget = false;
			TileCoordinate tile = Knockback.getKnockBackCoordinate(source, targetUnit);
			if(StateUtils.getUnitByTile(tile) == null){
				if(State.activeState.map.getTile(tile) != null && State.activeState.map.getTile(tile).terrain.penalty != Integer.MAX_VALUE){ 
					jumpingTarget = true;
				}
			}		
			
			
			return super.isAllowed(source, coordinates) 
					&& targetUnit != null
					&& targetUnit.ownerId != source.ownerId
					&& jumpingTarget;
		}

		@Override
		public void activate(Unit source, TileCoordinate coordinate) {
			super.activate(source, coordinate);
			TileCoordinate jumpingFrom = source.tileCoordinate;
			Unit targetUnit = StateUtils.getUnitByTile(coordinate);
			
			int minDamage = Combat.minDamage(source, targetUnit, DAMAGEMULTIPLIER);
			int maxDamage = Combat.maxDamage(source, targetUnit, DAMAGEMULTIPLIER);
			int damage = State.activeState.random.nextInt( maxDamage-minDamage+1 ) + minDamage; // +1 because random is exclusive
			targetUnit.currentHP -= damage;
			targetUnit.resolveDeath();
			TileCoordinate tc = Knockback.getKnockBackCoordinate(source, targetUnit);
			source.tileCoordinate = tc;
			Animation leapAnimation = new LeapAttackAnimation(source, targetUnit, damage, jumpingFrom, source.tileCoordinate);
			GEngine.getInstance().animationHandler.enqueue(leapAnimation);
			GEngine.getInstance().getUnit(targetUnit).updateHealthbar();
			
			Building buildbob = StateUtils.getBuildingByTile(tc);
			int curid = State.activeState.currentPlayerId;
			if(	buildbob!=null && buildbob.controllerId != curid) {
				if(buildbob.type == Building.TYPE_GREENVILLAGE){
					buildbob.controllerId = curid;
					UberTextAnimation utah = new UberTextAnimation(tc, "captured-player");
					GEngine.getInstance().animationHandler.runParalell(utah);
				}
				else if(buildbob.type == Building.TYPE_ROCKVILLAGE){
					buildbob.controllerId = curid;
					UberTextAnimation utah = new UberTextAnimation(tc, "captured-player");
					GEngine.getInstance().animationHandler.runParalell(utah);
				}
				else if(buildbob.type == Building.TYPE_SNOWVILLAGE){
					buildbob.controllerId = curid;
					UberTextAnimation utah = new UberTextAnimation(tc, "captured-player");
					GEngine.getInstance().animationHandler.runParalell(utah);
				}
				else if(buildbob.type == Building.TYPE_CASTLE){
					UberTextAnimation utah = new UberTextAnimation(tc, "capturing-base-player");
					GEngine.getInstance().animationHandler.runParalell(utah);
					State.activeState.baseCaps[curid] = State.activeState.turnNr;
				}
			}
			
			Fog.recalculateFog(source.ownerId, State.activeState);
			
			CombatLog cl = new CombatLog();
			cl.attacker = source;
			cl.defender = targetUnit;
			cl.newAttackerAP = source.currentAP;
			cl.newDefenderHP = targetUnit.currentHP;
			cl.attackDamage = damage;
			
		}
		
		@Override
		public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
			ConfirmRow apRow = new APRow(source, apCost);
			ConfirmRow damageRow = new DamageRow(
					Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER), 
					Combat.maxDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER));
			return ConfirmDialog.abilityConfirm(position, "confirm-rush", damageRow, ConfirmDialog.apcost, apRow);
		}
		
		@Override
		public String getIconName(Unit source) {
			if(!isAllowed(source)) return "heal-button-gray";
			if(GEngine.getInstance().selectionHandler.selectionType == SelectionHandler.SELECTION_TARGETED_ABILITY){
				return "heal-button-active";
			}
			return "heal-button";
		}

	}
