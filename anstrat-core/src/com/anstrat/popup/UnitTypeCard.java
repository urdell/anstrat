package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ColorTable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

/**
 * A "card" displaying unit summary
 * @author eriter
 */
public class UnitTypeCard extends ColorTable {
	private Label name, cost, description, attack,hp,ap;
	private Image image;
	public UnitType type;
	
	public UnitTypeCard(UnitType type){
		super(new Color(0f, 0f, 0.8f, 1f));

		this.setBackground(Assets.SKIN.getPatch("double-border"));
		
		this.type = type;
		name = new Label(type.name, Assets.SKIN);
		cost = new Label(String.valueOf(type.cost), Assets.SKIN);
		description = new Label(type.description, new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE));
		description.setWrap(true);
		image = new Image(GUnit.getTextureRegion(type));
		attack = new Label(type.name,Assets.SKIN);
		hp = new Label(type.name,Assets.SKIN);
		ap = new Label(type.name,Assets.SKIN);
		Image hpIcon = new Image(Assets.getTextureRegion("hp"));
		Image apIcon = new Image(Assets.getTextureRegion("ap"));
		Image attackIcon = new Image(Assets.getTextureRegion("sword"));
		Image costIcon = new Image(Assets.getTextureRegion("gold"));
		
		int imageSize = (int)(Main.percentWidth*40);
		int iconSize = (int)(Main.percentHeight*5);
		
		this.top().left();
		this.defaults().expandX().fillX();
		
		Table outer1 = new Table();
		outer1.add(image).left().size(imageSize).padRight((int)(Main.percentHeight));
		
		Table inner = new Table();
		inner.defaults().left();
		inner.add(attackIcon).size(iconSize).padRight((int)(Main.percentHeight));
		inner.add(attack);
		inner.row();
		inner.add(apIcon).size(iconSize).padRight((int)(Main.percentHeight));
		inner.add(ap);
		inner.row();
		inner.add(hpIcon).size(iconSize).padRight((int)(Main.percentHeight));
		inner.add(hp);
		
		outer1.add(inner).left();
		outer1.add().fill().expand();
		
		this.add(outer1);
		this.row();
		this.add(description);
		this.row();
		this.add().fill().expand();
		this.row();
		
		Table outer2 = new Table();
		outer2.defaults().left();
		outer2.add(costIcon).size(iconSize).padRight((int)(Main.percentHeight));
		outer2.add(cost);
		outer2.add().expandX().fillX();
		
		this.add(outer2);
	}
	
	@Override
	public Actor hit(float x, float y) {
		return x > 0 && x < width && y > 0 && y < height ? this : null;
	}
	
	public void setDisabled(boolean disabled){
		cost.setColor(disabled ? Color.LIGHT_GRAY : Color.WHITE);
	}
	
	public void setType(UnitType type) {
		this.type = type;
		name.setText(type.name);
		cost.setText(String.valueOf(type.cost));
		description.setText(type.description);
		image.setRegion(GUnit.getUnitPortrait(type));
		attack.setText(String.valueOf(type.attack));
		hp.setText(String.valueOf(type.maxHP));
		ap.setText(String.format("%d (%d)", type.maxAP, type.APReg));
	}
} 
