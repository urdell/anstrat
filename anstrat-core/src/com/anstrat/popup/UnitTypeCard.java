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
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.TableLayout;

/**
 * A "card" displaying unit summary
 * @author eriter
 */
public class UnitTypeCard extends ColorTable {
	private Label name, cost, description, attack,armor,hp,ap,abilities;
	private Image image;
	public UnitType type;
	
	public UnitTypeCard(UnitType type){
		super(new Color(0f, 0f, 0.8f, 1f));

		//Border
		this.setBackground(Assets.SKIN.getPatch("double-border"));
		
		this.type = type;
		name = new Label(type.name, Assets.SKIN);
		cost = new Label(String.valueOf(type.cost), Assets.SKIN);
		//description = new Label(type.description, new LabelStyle(new BitmapFont(),Color.WHITE));
		description = new Label(type.description, new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE));
		description.setWrap(true);
		image = new Image(GUnit.getTextureRegion(type));
		//statDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_ATTACK)
		attack = new Label(type.name,Assets.SKIN);
		armor = new Label(type.name,Assets.SKIN);
		hp = new Label(type.name,Assets.SKIN);
		ap = new Label(type.name,Assets.SKIN);
		
		TableLayout layout = this.getTableLayout();
		layout.register("cost",cost);
		layout.register("costIcon",new Image(Assets.getTextureRegion("gold")));
		layout.register("name",name);
		layout.register("desc",description);
		layout.register("image",image);
		layout.register("attack",attack);
		layout.register("armor",armor);
		layout.register("hp",hp);
		layout.register("ap",ap);
		
		int imageSize = (int)(Main.percentWidth*40);
		
		layout.parse("align:top,left " +
				//"[name] align:center paddingTop:"+(int)(-3*Main.percentHeight)+" paddingBottom:"+(int)(-2*Main.percentHeight) +
				//"---" +
				"{[image] align:left size:"+imageSize +
				"{*align:left paddingRight:"+(int)(4*Main.percentHeight)+" height:"+(int)(3*Main.percentHeight)+
				"[attack]" + "---"+ 
				"[armor]" + "---"+ 
				"[ap]" + "---" +
				"[hp]}}"+
				"---" +
				"[desc] fill:y expand:y fill:x expand:x" +
				"---" +
				"{*align:left [costIcon] size:"+(int)(4*Main.percentHeight)+" [cost]} align:left height:"+(int)(3*Main.percentHeight));
		
	}
	
	public void setSize(float width, float height){
		this.width = width;
		this.height = height;
		description.width = width;
		description.layout();
	}

	@Override
	public Actor hit(float x, float y) {
		return x > 0 && x < width && y > 0 && y < height ? this : null;
	}
	
	public void setDisabled(boolean disabled){
		if(disabled)
			cost.setColor(Color.RED);
		else
			cost.setColor(Color.WHITE);
	}
	
	public void setType(UnitType type) {
		this.type = type;
		name.setText(type.name);
		cost.setText(String.valueOf(type.cost));
		description.setText(type.description);
		image.setRegion(GUnit.getUnitPortrait(type));
		attack.setText(String.valueOf("Attack: " + type.attack));
		hp.setText(String.valueOf("HP: " + type.maxHP));
		ap.setText(String.valueOf("AP: " + type.maxAP + "(+" + type.APReg + ")"));
	}
} 
