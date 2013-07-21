package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.KamikazeAnimation;
import com.anstrat.gameCore.Combat;
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

public class Kamikaze extends TargetedAbility {
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 3;
	private static final int RANGE = 1;
	private static final float DAMAGEMULTIPLIER = 2.0f;

	public Kamikaze(){
		super("Kamikaze", "An attack that deals extra damage but also kills the hawk unit.", AP_COST, RANGE);
		iconName = "kamikaze-button";
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
		source.currentHP = 0;	//Kamikaze kills itself
		
		targetUnit.resolveDeath();
		source.resolveDeath();
		
		CombatLog cl = new CombatLog();
		cl.attacker = source;
		cl.defender = targetUnit;
		cl.newAttackerAP = source.currentAP;
		cl.newDefenderHP = targetUnit.currentHP;
		cl.attackDamage = damage;
		//Animation animation = new AttackAnimation(cl);
		KamikazeAnimation animation = new KamikazeAnimation(source, targetUnit, damage);
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(
				Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER), 
				Combat.maxDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER));
		return ConfirmDialog.abilityConfirm(position, "confirm-kamikaze", damageRow, ConfirmDialog.apcost, apRow);
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
