package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.AttackAnimation;
import com.anstrat.animation.PoisonAnimation;
import com.anstrat.core.Assets;
import com.anstrat.gameCore.Combat;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.PoisonEffect;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.DamageRow;
import com.anstrat.gui.confirmDialog.TextRow;
import com.anstrat.gui.confirmDialog.XxxPicRow;

public class Poison extends TargetedAbility{
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 3;
	private static final int RANGE = 2;
	private static final int nrOfRounds = 3;
	private static final float DAMAGEMULTIPLIER = 0.7f;
	
	public Poison() {
		super("Poison", "Fires a poisonous arrow that reduces the targets damage for the next round", AP_COST, RANGE);
		iconName = "poison-button";
	}

	public boolean isAllowed(Unit source, TileCoordinate coordinates) {
		Unit targetUnit = StateUtils.getUnitByTile(coordinates);
		
		return super.isAllowed(source, coordinates) 
				&& targetUnit != null
				&& targetUnit.ownerId != source.ownerId;
	}
	
	
	@Override
	public void activate(Unit source, TileCoordinate coordinate) {
		super.activate(source, coordinate);
		
		Unit targetUnit = StateUtils.getUnitByTile(coordinate);
		
		int minDamage = Combat.minDamage(source, targetUnit, DAMAGEMULTIPLIER);
		int maxDamage = Combat.maxDamage(source, targetUnit, DAMAGEMULTIPLIER);
		int damage = State.activeState.random.nextInt( maxDamage-minDamage+1 ) + minDamage; // +1 because random is exclusive
		targetUnit.currentHP -= damage;
		targetUnit.resolveDeath();
		PoisonEffect poisonEffect = new PoisonEffect(nrOfRounds);
		targetUnit.effects.add(poisonEffect);
		
		CombatLog cl = new CombatLog();
		cl.attacker = source;
		cl.defender = targetUnit;
		cl.newAttackerAP = source.currentAP;
		cl.newDefenderHP = targetUnit.currentHP;
		cl.attackDamage = damage;
		Animation attackanimation = new AttackAnimation(cl);
		GEngine ge = GEngine.getInstance();
		ge.animationHandler.enqueue(attackanimation);
		if(targetUnit.currentHP > 0){
			Animation poisonAnimation = new PoisonAnimation(targetUnit);
			ge.animationHandler.runParalell(poisonAnimation);
		}
	}

	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(
				Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER), 
				Combat.maxDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER));
		return ConfirmDialog.abilityConfirm(position, "confirm-poison", damageRow, ConfirmDialog.apcost, apRow);
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
