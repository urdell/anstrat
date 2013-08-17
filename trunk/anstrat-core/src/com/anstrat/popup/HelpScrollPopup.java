package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class HelpScrollPopup extends Popup {

	public HelpScrollPopup(Image... images){
		this.drawOverlay = false;
		
		NinePatchDrawable emp = new NinePatchDrawable(Assets.SKIN.getPatch("empty"));
		ScrollPaneStyle spst = new ScrollPaneStyle(emp,emp,emp,emp,emp);
		
		ScrollPane scroll = new ScrollPane(imageTable(images), spst);
		scroll.setFlickScroll(true);
		scroll.setScrollingDisabled(true, false);
		scroll.setOverscroll(false, false);
		scroll.setSmoothScrolling(true);

		this.add(scroll).size(Main.percentWidth * 100f, Main.percentHeight * 100f);
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("empty")));
	}
	
	private static Table imageTable(Image... images) {
		Table table = new Table();
		
		for(Image image : images) {
			float aspectRatio = image.getWidth() / image.getHeight();
			float width = Main.percentWidth * 100f;
			float height = width / aspectRatio;
			
			table.add(image).size(width, height);
			table.row();
		}
		
		return table;
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
	
	public static Popup basic(){
		return new HelpScrollPopup(
				new Image(Assets.getTextureRegion("help1")),
				new Image(Assets.getTextureRegion("help2")),
				new Image(Assets.getTextureRegion("help3")));
	}
	
	public static Popup ability(){
		return new HelpScrollPopup(
				new Image(Assets.getTextureRegion("abilitiesmenu")));
	}
}
