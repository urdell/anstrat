package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.AttackAnimation;
import com.anstrat.animation.DeathAnimation;
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

	public Kamikaze(){
		super("Kamikaze","Making a suicideÁttack dealing additional damage",AP_COST, RANGE);
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
		
		int roll = State.activeState.random.nextInt(6)+1;
		
		targetUnit.currentHP -= source.getAttack()+roll;
		source.currentHP = 0;
		
		targetUnit.resolveDeath();
		source.resolveDeath();
		
		
		CombatLog cl = new CombatLog();
		cl.attacker = source;
		cl.defender = targetUnit;
		cl.newAttackerAP = source.currentAP;
		cl.newDefenderHP = targetUnit.currentHP;
		cl.attackDamage = source.getAttack()+3+roll;
		Animation animation = new AttackAnimation(cl);
		GEngine.getInstance().animationHandler.enqueue(animation);
		
		Animation sourceDeathAnimation = new DeathAnimation(source, coordinate);
		GEngine.getInstance().animationHandler.enqueue(sourceDeathAnimation);
		
	}
	
	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(source.getAttack()+4, source.getAttack()+9);
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
