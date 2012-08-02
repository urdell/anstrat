
package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.HealAnimation;
import com.anstrat.animation.UpdateBarAnimation;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;

public class MagicSpear extends TargetedAbility {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 3;
	private static final int RANGE = 2;

	
	public MagicSpear(){
		super("Magic Spear","Uses a magic spear to attack from range",AP_COST, RANGE);
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
		
		targetUnit.currentHP -= source.getAttack();
		
		Animation animation = new HealAnimation(source, StateUtils.getUnitByTile(source.tileCoordinate));
		GEngine.getInstance().animationHandler.enqueue(animation);
		animation = new UpdateBarAnimation(targetUnit);
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
