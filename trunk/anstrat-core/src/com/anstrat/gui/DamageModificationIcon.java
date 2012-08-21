package com.anstrat.gui;

import java.util.ArrayList;
import java.util.List;

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

	int number;
	TextureRegion portrait;
	
	public DamageModificationIcon(UnitType source, UnitType target){
		number = DamageModification.getAttackModifierAsPercent(source, target);
		portrait = Assets.getTextureRegion(target.portrait);
	}
	public DamageModificationIcon(){
		number = 125;
		portrait = Assets.getTextureRegion("bigguyportrait");
	}
	public static List<DamageModificationIcon> getAllDamageIcons(UnitType source){
		List<DamageModificationIcon> list = new ArrayList<DamageModificationIcon>();
		for(UnitType target : UnitType.values()){
			if(DamageModification.getAttackModifierAsPercent(source, target) != 100){
				list.add(new DamageModificationIcon(source, target));
			}
		}	
		return list;
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
		batch.draw(portrait, x, y, width, height);
		FancyNumbers.drawNumberPercent(number, x, y+height/50, height/3, false, batch);
	}

}
