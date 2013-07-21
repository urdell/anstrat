package com.anstrat.gameCore.abilities;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.animation.Animation;
import com.anstrat.animation.RitualisticVortex;
import com.anstrat.gameCore.Combat;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.confirmDialog.APRow;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmRow;
import com.anstrat.gui.confirmDialog.DamageRow;
import com.anstrat.gui.confirmDialog.HealRow;
import com.anstrat.gui.confirmDialog.TextRow;

public class DarkRitual extends Ability{

	private static final int AP_DRAIN_AMOUNT = 2;
	
	public DarkRitual() {
		super("AP Boom", "Drains AP from nearby enemy units but also kills the \"fallen\".", 0);
		iconName = "suicide-button";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isAllowed(Unit source) {
		return super.isAllowed(source);
	}
	
	@Override
	public void activate(Unit source) {
		super.activate(source);

		source.currentHP = 0;
		
		List<Tile> adjacentTiles = new ArrayList<Tile>();
		adjacentTiles = State.activeState.map.getNeighbors(source.tileCoordinate);
		
		List<Unit> units = new ArrayList<Unit>();
		
		for (Tile adjacentTile : adjacentTiles){
			Unit unit = StateUtils.getUnitByTile(adjacentTile.coordinates);
			if (unit != null){
				if (unit.ownerId != source.ownerId){
					units.add(unit);
					unit.currentAP -= AP_DRAIN_AMOUNT;
					if (unit.currentAP < 0){
						unit.currentAP = 0;
					}
				}
			}
		}
		source.resolveDeath();
		Animation animation = new RitualisticVortex(source, units);
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
	
	/*
	@Override
	public ConfirmDialog generateConfirmDialog(Unit source, TileCoordinate target, int position){
		ConfirmRow nameRow = new TextRow(name);
		ConfirmRow apRow = new APRow(source, apCost);
		ConfirmRow damageRow = new DamageRow(
				Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER), 
				Combat.maxDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER));
		int HPAfterAttack = source.currentHP+Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER);
		if(source.currentHP+source.getAttack() > source.getMaxHP() ){
			HPAfterAttack = source.getMaxHP();
		}
		ConfirmRow healRow = new HealRow(source.currentHP, HPAfterAttack);
		return ConfirmDialog.abilityConfirm(position, "confirm-lifesteal", nameRow, apRow, damageRow,healRow);
	}
	*/
}


