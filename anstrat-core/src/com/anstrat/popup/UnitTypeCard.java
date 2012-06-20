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

/**
 * A "card" displaying unit summary
 * @author eriter
 */
public class UnitTypeCard extends ColorTable {
	private Label name, cost, description, attack,armor,hp,ap;//,abilities;
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
		armor = new Label(type.name,Assets.SKIN);
		hp = new Label(type.name,Assets.SKIN);
		ap = new Label(type.name,Assets.SKIN);
		
		this.register("cost",cost);
		this.register("costIcon",new Image(Assets.getTextureRegion("gold")));
		this.register("name",name);
		this.register("desc",description);
		this.register("image",image);
		this.register("attack",attack);
		this.register("armor",armor);
		this.register("hp",hp);
		this.register("ap",ap);
		this.register("apIcon",new Image(Assets.getTextureRegion("hp")));
		this.register("hpIcon",new Image(Assets.getTextureRegion("ap")));
		this.register("atkIcon",new Image(Assets.getTextureRegion("sword")));
		
		int imageSize = (int)(Main.percentWidth*40);
		String iconSize = "size:"+(int)(Main.percentHeight*5)+" paddingRight:"+(int)(Main.percentHeight);
		
		this.parse("align:top,left " +
				"* expand:x fill:x" +
				"{" +
					"[image] align:left size:"+imageSize+" paddingRight:"+(int)(Main.percentHeight) +
					"{" +
						"* align:left" +
						"[atkIcon]"+iconSize+"[attack]" + "---"+ 
						"[apIcon]" +iconSize+"[ap]" + "---" +
						"[hpIcon]" +iconSize+"[hp]" +
					"} align:left " +
					"[] fill expand" +
				"}"+
				"---" +
				"[desc]" +
				"--- [] fill expand ---" +
				"{" +
					"*align:left" +
					"[costIcon] "+iconSize+" [cost] [] expand:x fill:x" +
				"}");
	}
	
	@Override
	public Actor hit(float x, float y) {
		return x > 0 && x < width && y > 0 && y < height ? this : null;
	}
	
	public void setDisabled(boolean disabled){
		if(disabled)
			cost.setColor(Color.LIGHT_GRAY);
		else
			cost.setColor(Color.WHITE);
	}
	
	public void setType(UnitType type) {
		this.type = type;
		name.setText(type.name);
		cost.setText(String.valueOf(type.cost));
		description.setText(type.description);
		image.setRegion(GUnit.getUnitPortrait(type));
		attack.setText(String.valueOf(type.attack));
		hp.setText(String.valueOf(type.maxHP));
		ap.setText(String.valueOf(type.maxAP + "(+" + type.APReg + ")"));
	}
} 
