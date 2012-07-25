package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.HealAnimation;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;

public class APHeal extends TargetedAbility{
	
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 3;
	private static final int AP_HEAL = 2;
	private static final int RANGE = 2;
	
	public APHeal(){
		super("AP-Regenaration", "Restore "  + AP_HEAL + " of target units AP.", AP_COST, RANGE);
	}

	@Override
	public boolean isAllowed(Unit source, TileCoordinate coordinates) {
		Unit targetUnit = StateUtils.getUnitByTile(coordinates);
		
		return super.isAllowed(source, coordinates)
				&& targetUnit != null
				&& targetUnit.ownerId == source.ownerId				// Target must be friendly
				&& targetUnit.currentAP < targetUnit.getMaxAP();	// Not at max hp already
	}

	@Override
	public void activate(Unit source, TileCoordinate coordinate) {
		super.activate(source, coordinate);
		
		Unit targetUnit = StateUtils.getUnitByTile(coordinate);
		targetUnit.currentAP += AP_HEAL;
		
		if(targetUnit.currentAP > targetUnit.getMaxAP()) targetUnit.currentAP = targetUnit.getMaxAP();
		
		Animation animation = new HealAnimation(source, StateUtils.getUnitByTile(coordinate));
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
