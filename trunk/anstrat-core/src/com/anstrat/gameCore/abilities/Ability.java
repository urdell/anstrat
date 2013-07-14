package com.anstrat.gameCore.abilities;

import java.io.Serializable;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.TextRow;

public abstract class Ability implements Serializable {
	
	private static final long serialVersionUID = 3L;
	
	public final String name;
	public String iconName = "empty-button";
	public final String description;
	public final int apCost;
	
	public Ability(String name, String description, int apCost){
		this.name = name;
		this.description = description;
		this.apCost = apCost;
	}
	
	public void activate(Unit source){
		source.currentAP -= apCost;
	}
	
	public boolean isAllowed(Unit source){
		return source.currentAP >= apCost;	// Unit must have the required ap
	}
	
	public ConfirmDialog generateConfirmDialog(Unit source, int position){
		ConfirmRow apRow = new APRow(source, apCost);
		return ConfirmDialog.abilityConfirm(position, "confirm-boom", ConfirmDialog.apcost, apRow);
	}
	
	public abstract String getIconName(Unit source);
}
