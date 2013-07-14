package com.anstrat.gameCore.abilities;

import com.anstrat.animation.ShieldWallAnimation;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.effects.ShieldWallEffect;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.HealRow;
import com.anstrat.gui.confirmDialog.TextRow;

public class ShieldWall extends Ability {

	private static final long serialVersionUID = 7057699430536432784L;
	/**
	 * Halves damage when activated
	 * Does not affect abilities at the moment
	 */
	private static final int nrOfRounds = 1;
	
	
	public ShieldWall() {
		super("Shield Wall", "Halves all incoming damage for the duration of the turn",2);
		iconName = "shield-button";
	}

	public boolean isAllowed(Unit source){
		return super.isAllowed(source);
	}
	
	public void activate(Unit source){
		super.activate(source);
		ShieldWallEffect shieldWallEffect = new ShieldWallEffect(nrOfRounds);
		source.effects.add(shieldWallEffect);
		
		GEngine.getInstance().animationHandler.enqueue(new ShieldWallAnimation(source));
	}
	
	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow healRow = new HealRow(1, 1/2);
		return ConfirmDialog.abilityConfirm(position, nameRow, healRow, apRow);
	}
	
	
	@Override
	public String getIconName(Unit source) {
		if(!isAllowed(source)) return "heal-button-gray";
		if(GEngine.getInstance().selectionHandler.selectionType == SelectionHandler.SELECTION_TARGETED_ABILITY){
			return "heal-button-active";
		}
		return "shield-button";
	}

}
