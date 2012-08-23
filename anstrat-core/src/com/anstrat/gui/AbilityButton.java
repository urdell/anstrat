package com.anstrat.gui;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.DamageModification;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.abilities.Ability;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;


/**
 * 
 * @author Anton
 * Designed to dynamically change its displayed ability without need for creating new instances.
 */
public class AbilityButton extends Table{
	TextureRegion image;
	Unit unit = null;
	Ability ability = null;
	
	public AbilityButton(Unit unit, Ability ability){
		setAbility(unit, ability);
		touchable = true;
	}
	public AbilityButton(){
		image = Assets.getTextureRegion("cancel");
		touchable = true;
	}
	
	public void setAbility(Unit unit, Ability ability){
		image = Assets.getTextureRegion(unit.getUnitType().portrait);
		this.unit = unit;
		this.ability = ability;
	}
	
	@Override
	public float getPrefWidth(){
		return Gdx.graphics.getWidth()*0.30f; // Oversized to fill up all it's allowed space
	}
	@Override
	public float getPrefHeight(){
		return Gdx.graphics.getWidth()*0.30f;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		validate();
		batch.setColor(Color.WHITE);
		if( GEngine.getInstance().selectionHandler.selectionType == SelectionHandler.SELECTION_TARGETED_ABILITY ){ // TODO more checks for this specific ability.
			batch.setColor(Color.YELLOW);
		}
		if(!ability.isAllowed(unit)){
			batch.setColor(Color.GRAY);
		}
		
		batch.draw(image, x, y, width, height);

		if(unit != null && ability != null){
			FancyNumbers.drawNumber(ability.apCost, x, y+height/50, height/3, false, batch);
		
		}
		batch.setColor(Color.WHITE);
	}

}