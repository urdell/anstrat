package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class HelpBasicPopup extends Popup {

	public HelpBasicPopup(){
		this.drawOverlay = false;
		
		NinePatchDrawable emp = new NinePatchDrawable(Assets.SKIN.getPatch("empty"));
		ScrollPaneStyle spst = new ScrollPaneStyle(emp,emp,emp,emp,emp);
		
		Table table = new Table();
		table.setWidth(Main.percentWidth*100f);
		table.add(new Image(Assets.getTextureRegion("help1")));
		table.row();
		table.add(new Image(Assets.getTextureRegion("help2")));
		table.row();
		table.add(new Image(Assets.getTextureRegion("help3")));
		
		ScrollPane scroll = new ScrollPane(table, spst);
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
	public void show(){
		GEngine.getInstance().userInterface.setVisible(false);
		super.show();
	}
	
	@Override
	public void close(){
		GEngine.getInstance().userInterface.setVisible(true);
		super.close();
	}
}
