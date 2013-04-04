package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.APPieIcon;
import com.anstrat.gui.DamageModificationIcon;
import com.anstrat.gui.GUnit;
import com.anstrat.gui.NumberIcon;
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
 */
public class UnitTypeCard extends ColorTable {
	private NumberIcon attack, hp, cost;
	private Label name, description, ap, attackModifierText, attackModLabel;
	private Table apTable, damageModifierTable;
	private Image image;
	public UnitType type;
	private APPieIcon apPie;
	private Label apText;
	
	public UnitTypeCard(boolean showCost){
		super(new Color(0f, 0f, 0.8f, 1f));
		
		float imageSize = Main.percentWidth*40f;
		float iconSize  = Main.percentHeight*5f;

		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("border-thick-updown")));
		
		name = new Label("", Assets.SKIN);
		
		description = new Label("", new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE));
		description.setWrap(true);
		Table descriptionTable = new Table();
		descriptionTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		descriptionTable.top().left().add(description).padLeft(Main.percentHeight).fillX().expandX();
		
		image = new Image(Assets.WHITE);
		
		Label attackText = new Label("Attack:", Assets.SKIN);
		attack = new NumberIcon(1, 1, Color.RED);
		Table attackTable = new Table();
		attackTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		attackTable.defaults().left().padLeft(Main.percentWidth*2);
		//attackTable.add(new Image(Assets.getTextureRegion("sword"))).size(iconSize);
		attackTable.add(attackText);
		attackTable.add(attack).padLeft(Main.percentHeight).fillX().expandX();
		
		attackModifierText = new Label("Attack Modifier:", Assets.SKIN);
		attackModLabel = new Label("", Assets.SKIN);
		damageModifierTable = new Table();
		damageModifierTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		damageModifierTable.defaults().left().padLeft(Main.percentWidth*2);
		damageModifierTable.add(attackModifierText);
		damageModifierTable.add(attackModLabel).padLeft(Main.percentHeight).fillX().expandX();
		
		Label hpText = new Label("Health:", Assets.SKIN);
		hp = new NumberIcon(1,1,Color.GREEN);
		Table hpTable = new Table();
		hpTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		hpTable.defaults().left().padLeft(Main.percentWidth*2);
		hpTable.add(hpText);
		//hpTable.add(new Image(Assets.getTextureRegion("hp"))).size(iconSize);
		hpTable.add(hp).padLeft(Main.percentHeight).fillX().expandX();
		
		Label costText = new Label("Cost:", Assets.SKIN);
		cost = new NumberIcon(1,1,Color.YELLOW);
		Table costTable = new Table();
		costTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		costTable.defaults().left().padLeft(Main.percentWidth*2);
		
		
		apText = new Label("Action Points:", Assets.SKIN);
		ap = new Label("",Assets.SKIN);
		apTable = new Table();
		apTable.defaults().left().padLeft(Main.percentWidth*2);
		apTable.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("fadetoblack")));
		//apTable.add(new Image(Assets.getTextureRegion("ap"))).size(iconSize);
		apTable.add(apText);
		apTable.add(ap).padLeft(Main.percentHeight).fillX().expandX();
		
		//Image costIcon = new Image(Assets.getTextureRegion("gold"));
		
		this.defaults().top().left().expandX().fillX().space(Main.percentHeight);
		
		Table top = new Table();
		top.defaults().top().left();
		top.add(image).size(imageSize);
		top.add(descriptionTable).fill().expandX();
		
		this.add(top).fillX().expandX();
		this.row();
		this.add(attackTable).height(iconSize*1.2f);
		this.row();
		this.add(damageModifierTable).height(iconSize*1.2f);
		this.row();
		this.add(apTable).height(iconSize*1.2f);
		this.row();
		this.add(hpTable).height(iconSize*1.2f);
		this.row();
		if(showCost){
			//bottom.add(costIcon).size(iconSize).padRight(Main.percentHeight).padLeft(Main.percentWidth*2);
			//bottom.add(cost);
			costTable.add(costText);
			costTable.add(cost).padLeft(Main.percentHeight).fillX().expandX();
			this.add(costTable).height(iconSize*1.2f);
			this.row();
		}
		
		this.add().fill().expand();
		this.row();
		
		//Table bottom = new Table();
		//bottom.defaults().left();
		
		
		
		//bottom.add().expandX().fillX();
		
		
		//bottom.add(damageModifierTable);
		
		//this.add(bottom);
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
		cost.setColor(disabled ? Color.LIGHT_GRAY : Color.YELLOW);
	}
	
	/**
	 * Show info about a specific unit type.
	 * @param type
	 */
	public void setType(UnitType type) {
		this.type = type;
		name.setText(type.name);
		cost.setNumberAndSize(type.cost, 30f);
		description.setText(type.description);
		image.setDrawable(new TextureRegionDrawable(GUnit.getUnitPortrait(type)));
		attack.setNumberAndSize(type.attack, 30f);
		hp.setNumberAndSize(type.maxHP, 30f);
		//ap.setText(String.format("%d (%d)", type.maxAP, type.APReg));
		//APPieDisplay.draw(ap.getX(), ap.getY(), 30, type.APReg, type.maxAP, type.APReg, 2, GEngine.getInstance().batch, false, 1);
		
		apTable.clear();
		apTable.add(apText);
		apPie = new APPieIcon(type.maxAP, type.APReg);
		apTable.add(apPie);
		apTable.add(ap).padLeft(Main.percentHeight).fillX().expandX();
		damageModifierTable.clear();
		damageModifierTable.add(attackModifierText);
		for(DamageModificationIcon damageIcon : DamageModificationIcon.getAllDamageIcons(type)){
			damageModifierTable.add(damageIcon);
		}
		damageModifierTable.add(attackModLabel).padLeft(Main.percentHeight).fillX().expandX();
	}
	
	/**
	 * Show info about a specific unit rather than a unit type.
	 * @param unit
	 */
	public void setUnit(Unit unit){
		this.setType(unit.getUnitType());
		this.setColor(GameInstance.activeGame.state.players[unit.ownerId].getColor());
		
		
		//TODO need update and use numbericons instead of string.
		
		//hp.setText(unit.currentHP+"/"+type.maxHP);
		//ap.setText(String.format("%d/%d (%d)", unit.currentAP, type.maxAP, type.APReg));
		//APPieDisplay.draw(ap.getX(), ap.getY(), 30, unit.getAPReg(), unit.getMaxAP(), unit.getAPReg(), 2, GEngine.getInstance().batch, false, 1);
		//attack.setText(String.valueOf(unit.getAttack()));
	}
} 
