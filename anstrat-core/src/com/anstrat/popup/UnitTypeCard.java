package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.DamageModificationIcon;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ColorTable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * A "card" displaying unit summary
 * @author eriter
 */
public class UnitTypeCard extends ColorTable {
	private Label name, cost, description, attack,hp,ap;
	private Table damageModifierTable;
	private Image image;
	public UnitType type;
	
	public UnitTypeCard(boolean showCost){
		super(new Color(0f, 0f, 0.8f, 1f));
		
		float imageSize = Main.percentWidth*40f;
		float iconSize  = Main.percentHeight*5f;

		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("border-thick-updown")));
		
		name = new Label("", Assets.SKIN);
		cost = new Label("", Assets.SKIN);
		
		description = new Label("", new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE));
		description.setWrap(true);
		Table descriptionTable = new Table();
		descriptionTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		descriptionTable.top().left().add(description).padLeft(Main.percentHeight).fillX().expandX();
		
		image = new Image(Assets.WHITE);
		
		attack = new Label("",Assets.SKIN);
		Table attackTable = new Table();
		attackTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		attackTable.defaults().left().padLeft(Main.percentWidth*2);
		attackTable.add(new Image(Assets.getTextureRegion("sword"))).size(iconSize);
		attackTable.add(attack).padLeft(Main.percentHeight).fillX().expandX();

		hp = new Label("",Assets.SKIN);
		Table hpTable = new Table();
		hpTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		hpTable.defaults().left().padLeft(Main.percentWidth*2);
		hpTable.add(new Image(Assets.getTextureRegion("hp"))).size(iconSize);
		hpTable.add(hp).padLeft(Main.percentHeight).fillX().expandX();
		
		ap = new Label("",Assets.SKIN);
		Table apTable = new Table();
		apTable.defaults().left().padLeft(Main.percentWidth*2);
		apTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		apTable.add(new Image(Assets.getTextureRegion("ap"))).size(iconSize);
		apTable.add(ap).padLeft(Main.percentHeight).fillX().expandX();
		
		
		Image costIcon = new Image(Assets.getTextureRegion("gold"));
		
		this.defaults().top().left().expandX().fillX().space(Main.percentHeight);
		
		Table top = new Table();
		top.defaults().top().left();
		top.add(image).size(imageSize);
		top.add(descriptionTable).fill().expandX();
		
		this.add(top).fillX().expandX();
		this.row();
		this.add(attackTable).height(iconSize*1.2f);
		this.row();
		this.add(apTable).height(iconSize*1.2f);
		this.row();
		this.add(hpTable).height(iconSize*1.2f);
		this.row();
		this.add().fill().expand();
		this.row();
		
		Table bottom = new Table();
		bottom.defaults().left();
		
		if(showCost){
			bottom.add(costIcon).size(iconSize).padRight(Main.percentHeight).padLeft(Main.percentWidth*2);
			bottom.add(cost);
		}
		
		bottom.add().expandX().fillX();
		
		damageModifierTable = new Table();
		bottom.add(damageModifierTable);
		
		this.add(bottom);
	}
	
	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return x > 0 && x < getWidth() && y > 0 && y < getHeight() ? this : null;
	}
	
	/**
	 * Marks current unit type as disabled (can't buy).
	 * @param disabled
	 */
	public void setDisabled(boolean disabled){
		cost.setColor(disabled ? Color.LIGHT_GRAY : Color.WHITE);
	}
	
	/**
	 * Show info about a specific unit type.
	 * @param type
	 */
	public void setType(UnitType type) {
		this.type = type;
		name.setText(type.name);
		cost.setText(String.valueOf(type.cost));
		description.setText(type.description);
		image.setDrawable(new TextureRegionDrawable(GUnit.getUnitPortrait(type)));
		attack.setText(String.valueOf(type.attack));
		hp.setText(String.valueOf(type.maxHP));
		ap.setText(String.format("%d (%d)", type.maxAP, type.APReg));
		
		damageModifierTable.clear();
		for(DamageModificationIcon damageIcon : DamageModificationIcon.getAllDamageIcons(type)){
			damageModifierTable.add(damageIcon);
		}
	}
	
	/**
	 * Show info about a specific unit rather than a unit type.
	 * @param unit
	 */
	public void setUnit(Unit unit){
		this.setType(unit.getUnitType());
		this.setColor(GameInstance.activeGame.state.players[unit.ownerId].getColor());
		hp.setText(unit.currentHP+"/"+type.maxHP);
		ap.setText(String.format("%d/%d (%d)", unit.currentAP, type.maxAP, type.APReg));
		attack.setText(String.valueOf(unit.getAttack()));
	}
} 
