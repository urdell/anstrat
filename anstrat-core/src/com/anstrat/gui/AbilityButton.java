package com.anstrat.gui;

import com.anstrat.command.ActivateAbilityCommand;
import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.abilities.TargetedAbility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


/**
 * 
 * @author Anton
 * Designed to dynamically change its displayed ability without need for creating new instances.
 */
public class AbilityButton extends Table{
	
	TextureRegion image;
	Unit unit = null;
	Ability ability = null;
	boolean isAllowed = false; // Stores this boolean for performance
	
	public AbilityButton(Unit unit, Ability ability){
		setAbility(unit, ability);
		setTouchable(Touchable.enabled);
	}
	public AbilityButton(){
		image = Assets.getTextureRegion("cancel");
		setTouchable(Touchable.enabled);
	}
	
	public void setAbility(Unit unit, Ability ability){
		image = Assets.getTextureRegion(ability.iconName);
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
	
	public void updateIsAllowed(){
		if(unit.ownerId != GameInstance.activeGame.getUserPlayer().playerId){
			isAllowed = false;
		}
		else if(ability instanceof TargetedAbility){
			isAllowed = !((TargetedAbility) ability).getValidTiles(unit).isEmpty();
		} 
		else{
			isAllowed = ability.isAllowed(unit);
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		validate();
		batch.setColor(Color.WHITE);
		SelectionHandler selectionHandler = GEngine.getInstance().selectionHandler;
		ActionHandler actionHandler = GEngine.getInstance().actionHandler;
		if( (selectionHandler.selectionType == SelectionHandler.SELECTION_TARGETED_ABILITY &&
				selectionHandler.selectedTargetedAbility == ability)
			|| (actionHandler.showingConfirmDialog && actionHandler.confirmCommand instanceof ActivateAbilityCommand)){ // TODO more checks for this specific ability.
			batch.setColor(Color.YELLOW);
		}
		if(!isAllowed){
			batch.setColor(Color.GRAY);
		}
		
		batch.draw(image, getX(), getY(), getWidth(), getHeight());

		if(unit != null && ability != null){
			//batch.setColor(Assets.apTextColor);
			FancyNumbers.drawApNumber(ability.apCost, getX()+getHeight()/6, getY()+getHeight()*0.22f, getHeight()/2.8f, false, batch);
		
		}
		batch.setColor(Color.WHITE);
	}

}