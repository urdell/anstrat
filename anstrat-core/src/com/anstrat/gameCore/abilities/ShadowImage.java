package com.anstrat.gameCore.abilities;

import com.anstrat.animation.Animation;
import com.anstrat.animation.DeathAnimation;
import com.anstrat.animation.LifeStealAnimation;
import com.anstrat.gameCore.Combat;
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
import com.anstrat.gui.confirmDialog.HealRow;
import com.anstrat.gui.confirmDialog.TextRow;

@Deprecated
public class ShadowImage extends TargetedAbility {
	private static final long serialVersionUID = 1L;
	private static final int AP_COST = 4;
	private static final int RANGE = 1;
	private static final float DAMAGEMULTIPLIER = 1.0f;
	
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
		
		int minDamage = Combat.minDamage(source, targetUnit, DAMAGEMULTIPLIER);
		int maxDamage = Combat.maxDamage(source, targetUnit, DAMAGEMULTIPLIER);
		int damage = State.activeState.random.nextInt( maxDamage-minDamage+1 ) + minDamage; // +1 because random is exclusive
		
		targetUnit.currentHP -= damage;
		source.currentHP += damage;
		
		if(source.currentHP > source.getMaxHP()) source.currentHP = source.getMaxHP();
		
		Animation animation = new LifeStealAnimation(source,targetUnit,damage);
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
		ConfirmRow damageRow = new DamageRow(
				Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER), 
				Combat.maxDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER));
		int HPAfterAttack = source.currentHP+Combat.minDamage(source, StateUtils.getUnitByTile(target), DAMAGEMULTIPLIER);
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
