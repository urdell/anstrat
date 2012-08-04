package com.anstrat.gameCore.abilities;

import java.util.Random;

import com.anstrat.animation.Animation;
import com.anstrat.animation.AttackAnimation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.HealAnimation;
import com.anstrat.animation.MoveAnimation;
import com.anstrat.gameCore.CombatLog;
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
import com.anstrat.gui.confirmDialog.TextRow;
import com.badlogic.gdx.math.Vector2;

public class LeapAttack extends TargetedAbility{
		/**
		 * TODO Copy Paste from Kamikaze right now
		 */
		private static final long serialVersionUID = 1L;
		private static final int AP_COST = 3;
		private static final int RANGE = 1;

		
		public LeapAttack(){
			super("Leap Attack","Making a leap-attack, jumping over the enemy and finishes with a dashing blow for extra damage",AP_COST, RANGE);
		}
		

		public boolean isAllowed(Unit source, TileCoordinate coordinates) {
			Unit targetUnit = StateUtils.getUnitByTile(coordinates);
			if(targetUnit == null)
				return false;
			boolean jumpingTarget = false;
			TileCoordinate tile = Knockback.getKnockBackCoordinate(source, targetUnit);
			if(StateUtils.getUnitByTile(tile) == null){
				if(State.activeState.map.getTile(tile).terrain.penalty != Integer.MAX_VALUE){ 
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
			
			int roll = State.activeState.random.nextInt(6)+1;
			int damage = source.getAttack()+6+roll;
			targetUnit.currentHP -= damage;
			targetUnit.resolveDeath();
			source.tileCoordinate = Knockback.getKnockBackCoordinate(source, targetUnit);
			Animation moveAnimation = new MoveAnimation(source, jumpingFrom, source.tileCoordinate);
			GEngine.getInstance().animationHandler.enqueue(moveAnimation);
			
			CombatLog cl = new CombatLog();
			cl.attacker = source;
			cl.defender = targetUnit;
			cl.newAttackerAP = source.currentAP;
			cl.newDefenderHP = targetUnit.currentHP;
			cl.attackDamage = damage;
			Animation animation = new AttackAnimation(cl);
			GEngine.getInstance().animationHandler.enqueue(animation);
			if(!targetUnit.isAlive){
				Animation deathAnimation = new DeathAnimation(targetUnit,source.tileCoordinate);
				GEngine.getInstance().animationHandler.enqueue(deathAnimation);
			}
		}
		
		@Override
		public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
			ConfirmRow nameRow = new TextRow(name);
			ConfirmRow apRow = new APRow(source, apCost);
			ConfirmRow damageRow = new DamageRow(source.getAttack()+7, source.getAttack()+12);
			return ConfirmDialog.abilityConfirm(position, nameRow, apRow, damageRow);
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
