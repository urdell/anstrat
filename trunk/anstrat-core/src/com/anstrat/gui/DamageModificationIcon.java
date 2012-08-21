package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.DamageModification;
import com.anstrat.gameCore.UnitType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class DamageModificationIcon extends Widget{

	public DamageModificationIcon(UnitType source, UnitType target){
		int number = DamageModification.getAttackModifierAsPercent(source, target);
	}
	public DamageModificationIcon(){
		int number = 125;
	}
	
	
	@Override
	public float getPrefWidth(){
		return Gdx.graphics.getWidth()*0.12f;
	}
	@Override
	public float getPrefHeight(){
		return Gdx.graphics.getWidth()*0.12f;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha){
		validate();
		TextureRegion tr = Assets.getTextureRegion("cancel");
		batch.draw(tr, x, y, width, height);
		FancyNumbers.drawNumber(112, x, y+height/10, height/3, false, batch);
	}

}
