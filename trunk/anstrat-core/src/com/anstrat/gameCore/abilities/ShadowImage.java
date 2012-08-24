package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.LifeStealAnimation;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.DamageRow;
import com.anstrat.gui.confirmDialog.HealRow;
import com.anstrat.gui.confirmDialog.TextRow;

public class ShadowImage extends TargetedAbility {
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 4;
	private static final int RANGE = 1;
	
	public ShadowImage(){
		super("ShadowImage","Creates an shadow image that makes a lifesteal attack",AP_COST, RANGE);
		iconName = "lifesteal-button";
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
		
		targetUnit.currentHP -= source.getAttack()-1;
		source.currentHP += source.getAttack()-1;
		
		if(source.currentHP > source.getMaxHP()) source.currentHP = source.getMaxHP();
		
		Animation animation = new LifeStealAnimation(source,targetUnit);
		GEngine.getInstance().animationHandler.enqueue(animation);
		targetUnit.resolveDeath();
		if(!targetUnit.isAlive){
			Animation deathAnimation = new DeathAnimation(targetUnit,source.tileCoordinate);
			GEngine.getInstance().animationHandler.enqueue(deathAnimation);
		}
	}

	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(source.getAttack()-1, source.getAttack()-1);
		int HPAfterAttack = source.currentHP+source.getAttack()-1;
		if(source.currentHP+source.getAttack() > source.getMaxHP() ){
			HPAfterAttack = source.getMaxHP();
		}
		ConfirmRow healRow = new HealRow(source.currentHP, HPAfterAttack);
		return ConfirmDialog.abilityConfirm(position, nameRow, apRow, damageRow,healRow);
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
