package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.State;
import com.anstrat.gui.GEngine;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class HelpPopup extends Popup{
	
	private Button basicButton, abilityButton, creditButton;
	
	private static final ClickListener BASIC_BUTTON_LISTENER = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			Popup.getCurrentPopup().close();
			HelpScrollPopup.basic().show();
		}
	};
	private static final ClickListener ABILITY_BUTTON_LISTENER = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			Popup.getCurrentPopup().close();
			HelpScrollPopup.ability().show();
		}
	};
	private static final ClickListener CREDIT_BUTTON_LISTENER = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			Popup.getCurrentPopup().close();
			(new CreditPopup()).show();
		}
	};
	
	public HelpPopup() {
		this.setMovable(false);
		this.drawOverlay = false;
		
		basicButton = ComponentFactory.createButton("Game Basics", BASIC_BUTTON_LISTENER);
		abilityButton = ComponentFactory.createButton("Abilities", ABILITY_BUTTON_LISTENER);
		creditButton = ComponentFactory.createButton("Credits", CREDIT_BUTTON_LISTENER);
		
		Table buttonTable = new Table(Assets.SKIN);
		buttonTable.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("BottomLargePanel"))));
		
		Button buttonCancel = ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		
		//Label title = new Label("Help Menu", Assets.SKIN);
		//title.setStyle(new LabelStyle(Assets.MENU_FONT, Color.BLACK));
		//title.setWidth(Main.percentWidth*90);
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("empty"))); // Overrides the default background with an empty one
		//this.row();
		//this.add(title).align(Align.center);
		this.row();
		this.add(basicButton);			//space
		this.row();
		this.add(abilityButton);			//space
		this.row();
		this.add(creditButton);			
		this.row();
		this.add(buttonCancel);
		
	}
	
	@Override
	public void resize(float width, float height){
		// Force popup to take up the whole window
		this.setSize(width, height);
		super.resize(width, height);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(Assets.getTextureRegion(State.activeState.currentPlayerId==0?"buy-blue":"buy-red"),
				0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		super.draw(batch, parentAlpha);
	}
	
	/**
	 * Check if the unit is afforded before showing popup.
	 */
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
