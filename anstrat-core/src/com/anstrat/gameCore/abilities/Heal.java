package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.HealAnimation;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.HealRow;
import com.anstrat.gui.confirmDialog.TextRow;

public class Heal extends TargetedAbility {
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 3;
	private static final int HEAL_AMOUNT = 5;
	private static final int RANGE = 2;
	
	public Heal(){
		super("Heal", "Heal target unit " + HEAL_AMOUNT + " hp.", AP_COST, RANGE);
		iconName = "heal-button";
	}

	@Override
	public boolean isAllowed(Unit source, TileCoordinate coordinates) {
		Unit targetUnit = StateUtils.getUnitByTile(coordinates);
		
		return super.isAllowed(source, coordinates)
				&& targetUnit != null
				&& targetUnit.ownerId == source.ownerId				// Target must be friendly
				&& targetUnit.currentHP < targetUnit.getMaxHP();	// Not at max hp already
	}

	@Override
	public void activate(Unit source, TileCoordinate coordinate) {
		super.activate(source, coordinate);
		
		Unit targetUnit = StateUtils.getUnitByTile(coordinate);
		targetUnit.currentHP += HEAL_AMOUNT;
		
		if(targetUnit.currentHP > targetUnit.getMaxHP()) targetUnit.currentHP = targetUnit.getMaxHP();
		
		Animation animation = new HealAnimation(source, StateUtils.getUnitByTile(coordinate), HEAL_AMOUNT);
		GEngine.getInstance().animationHandler.enqueue(animation);
	}
	
	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		Unit targetUnit = StateUtils.getUnitByTile(target);
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		int finalHealth = targetUnit.currentHP+HEAL_AMOUNT;
		if(finalHealth > targetUnit.getUnitType().maxHP)
			finalHealth = targetUnit.getUnitType().maxHP;
		ConfirmRow healRow = new HealRow(StateUtils.getUnitByTile(target).currentHP, finalHealth);
		return ConfirmDialog.abilityConfirm(position, nameRow, apRow, healRow);
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
