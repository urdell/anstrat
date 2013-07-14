package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.abilities.AbilityFactory;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HelpAbilityPopupUnit extends Table {

	public HelpAbilityPopupUnit(UnitType type) {
		
		Table unitTypeTable = new Table();
		defaults().left().fillX().expandX();
		
		Image portrait = new Image();
		portrait.setDrawable(new TextureRegionDrawable(GUnit.getTextureRegion(type)));
		unitTypeTable.add(portrait).size(Main.percentWidth*15f, Main.percentHeight*10f).align(Align.center);
		unitTypeTable.row();
		unitTypeTable.add(new Label(type.name, Assets.SKIN)).align(Align.center);
		
		
		Ability tempAbility = AbilityFactory.createAbility(type.ability1Id);
		Label desc = new Label(tempAbility.name+":\n "+tempAbility.description, new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE));
		desc.setWrap(true);
		Table descTable = new Table();
		descTable.top().left().add(desc).padLeft(Main.percentHeight).fillX().expandX();;
		
		Image abilityImage = new Image(Assets.getTextureRegion(tempAbility.iconName));
		
		this.add(unitTypeTable).size(Main.percentWidth*33f, Main.percentHeight*15f).align(Align.left);
		this.add(abilityImage).size(Main.percentWidth*11f, Main.percentHeight*7f);
		this.add(descTable).size(Main.percentWidth*56f, Main.percentHeight*25f);
		
		this.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
	}

}
