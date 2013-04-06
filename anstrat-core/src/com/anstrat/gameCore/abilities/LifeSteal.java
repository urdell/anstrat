package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.LifeStealAnimation;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;

public class LifeSteal extends TargetedAbility {
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 4;
	private static final int RANGE = 1;
	private static final float DAMAGEMULTIPLIER = 1.0f;
	
	public LifeSteal(){
		super("LifeSteal","Gains the HP his target unit suffers",AP_COST, RANGE);
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
		
		Animation animation = new LifeStealAnimation(source,targetUnit,source.getAttack()-1);
		GEngine.getInstance().animationHandler.enqueue(animation);
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

	
