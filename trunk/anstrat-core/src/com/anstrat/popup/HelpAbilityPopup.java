package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class HelpAbilityPopup extends Popup{
	
	//private ColorTable unitTable;
	
	public HelpAbilityPopup() {
		this.setMovable(false);
		this.drawOverlay = false;
		
		//Button buttonCancel = ComponentFactory.createButton(Assets.getTextureRegion("cancel"), "image", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		//TODO not extremely dynamic atm... Can't handle more than two teams etc.
		Table unitTable = new Table();
		/*
		if (GameInstance.activeGame.getUserPlayer().team == 0) {
			for (UnitType type : UnitType.TEAMS[0]) {
				unitTable.add(new HelpAbilityPopupUnit(type)).align(Align.left);
				unitTable.row();
				Table table = new Table();
				table.setColor(new Color(Color.BLUE));
				//ColorTable table2 = new ColorTable(new Color(Color.BLUE));
				//table.getColor().a = 0.1f;
				unitTable.add(table).height(Main.percentHeight*10f).fillX();
				unitTable.row();
			}
			for (UnitType type : UnitType.TEAMS[1]) {
				unitTable.add(new HelpAbilityPopupUnit(type)).align(Align.left);
				unitTable.row();
				com.badlogic.gdx.scenes.scene2d.ui.Widget table = new com.badlogic.gdx.scenes.scene2d.ui.Widget();
				table.setColor(new Color(Color.BLUE));
				//ColorTable table2 = new ColorTable(new Color(Color.BLUE));
				//table.getColor().a = 0.1f;
				unitTable.add(table).height(Main.percentHeight*10f).fillX();
				unitTable.row();
			}
		}
		else {
			for (UnitType type : UnitType.TEAMS[1]) {
				unitTable.add(new HelpAbilityPopupUnit(type)).align(Align.left);
				unitTable.row();
				Table table = new Table();
				table.setColor(new Color(Color.BLUE));
				//ColorTable table2 = new ColorTable(new Color(Color.BLUE));
				//table.getColor().a = 0.1f;
				unitTable.add(table).height(Main.percentHeight*10f).fillX();
				unitTable.row();
			}
			for (UnitType type : UnitType.TEAMS[0]) {
				unitTable.add(new HelpAbilityPopupUnit(type)).align(Align.left);
				unitTable.row();
				Table table = new Table();
				//ColorTable table2 = new ColorTable(new Color(Color.BLUE));
				table.setColor(new Color(Color.BLUE));
				//table.getColor().a = 0.1f;
				unitTable.add(table).height(Main.percentHeight*10f).fillX();
				unitTable.row();
			}
		}
		*/
		Image image = new Image(Assets.getTextureRegion("abilitiesmenu"));
		image.setWidth(Main.percentWidth*50);
		//unitTable.add(image);
		NinePatchDrawable emp = new NinePatchDrawable(Assets.SKIN.getPatch("empty"));
		ScrollPaneStyle spst = new ScrollPaneStyle(emp,emp,emp,emp,emp); //first one is PERHAPS background :)
		
		ScrollPane scroll = new ScrollPane(image, spst);
		scroll.setFlickScroll(true);
		scroll.setScrollingDisabled(true, false);
		this.setHeight(Main.percentHeight*50f);
		this.setWidth(Main.percentWidth*50f);
		this.defaults().space(Main.percentHeight).top().center();
		this.add(scroll).size(Main.percentWidth*100f, Main.percentHeight*100f);
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("empty")));
	}
	
	@Override
	public void resize(float width, float height){
		// Force popup to take up the whole window
		this.setSize(width, height);
		super.resize(width, height);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		super.draw(batch, parentAlpha);
	}
	
	@Override 
	public void show(){
		GEngine.getInstance().userInterface.setVisible(false);
		super.show();
	}
	
	@Override public void close(){
		GEngine.getInstance().userInterface.setVisible(true);
		super.close();
	}
}
