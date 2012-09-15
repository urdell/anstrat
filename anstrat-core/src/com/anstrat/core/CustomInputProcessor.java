package com.anstrat.core;

import com.anstrat.gui.GEngine;
import com.anstrat.gui.SelectionHandler;
import com.anstrat.gui.UI;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.menu.MainMenu;
import com.anstrat.menu.MenuScreen;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CustomInputProcessor extends InputAdapter {

	public static Popup resignPopup = new Popup("Resign?", true,
			new Label("Are you sure you want to resign?", Assets.SKIN),
			new Row(
					ComponentFactory.createButton("No", Popup.POPUP_CLOSE_BUTTON_HANDLER), 
					ComponentFactory.createButton("Yes", new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							Popup.getCurrentPopup().close();
							GameInstance.activeGame.resign();
						}
					})));
	
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode){
			case Input.Keys.ESCAPE:    // Fall through
			case Input.Keys.BACKSPACE:	// Fall through
			case Input.Keys.BACK: {
				Screen screen = Main.getInstance().getScreen();
				Popup currentPopup = Popup.getCurrentPopup();
				if (currentPopup != null && (currentPopup.handlesBackspace || (currentPopup.textFieldSelected() && keycode==Input.Keys.BACKSPACE))) {
					return true;
				}
				else if (currentPopup != null && (!currentPopup.textFieldSelected() || keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE)) {
					currentPopup.close();
					return true;
				}
				else if (screen instanceof MainMenu) {
					Gdx.app.exit();
					return true;
				}
				else if (screen instanceof MenuScreen && ((MenuScreen)screen).textFieldSelected() && keycode==Input.Keys.BACKSPACE) {
					return true;
				}
				else if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE || (keycode == Input.Keys.BACKSPACE && Gdx.app.getType() == ApplicationType.Desktop)) {
					if(screen instanceof GEngine){
						GEngine engine = GEngine.getInstance();
						
						if(engine.selectionHandler.selectionType != SelectionHandler.SELECTION_EMPTY){
							// If a confirm dialog is open, cancel that
							if(engine.actionHandler.showingConfirmDialog){
								engine.actionHandler.confirmCancelPress();
							}
							else{
								// Cancel selection
								engine.selectionHandler.deselect();
								engine.highlighter.clearHighlights();
							}
							
							return true;
						}
						
						// We're leaving the game, save the state
						Main.getInstance().games.saveGameInstances();
						Main.getInstance().setScreen(MainMenu.getInstance());
					}
					else	//Not ingame - go back one screen
						Main.getInstance().popScreen();
					
					return true;
				}
				else {
					return false;
				}
			}
			
			case Input.Keys.MENU: // Fall through
			case Input.Keys.B: {
				// Only show menu when playing a game
				if((Main.getInstance().getScreen() instanceof GEngine) && Popup.getCurrentPopup() == null){
					GEngine.getInstance().userInterface.showBuyUnitPopup();
					return true;
				}
				return false;
			}
			case Input.Keys.R: {
				if(Main.getInstance().getScreen() instanceof GEngine){
					resignPopup.show();
					return true;
				}
				
				return false;
			}
			case Input.Keys.ENTER: {
				if(Main.getInstance().getScreen() instanceof GEngine){
					GEngine.getInstance().actionHandler.endTurnPress();
					return true;
				}
				
				return false;
			}
			case Input.Keys.H: {
				UI gameUI = GEngine.getInstance().userInterface;
				boolean newVisibility = !gameUI.isVisible();
				
				// Toggle visibility
				GEngine.getInstance().userInterface.setVisible(newVisibility);
				
				Gdx.app.log(this.getClass().getSimpleName(), String.format("Toggled visibility, visible = %s.", newVisibility));
			}
			default:
				return false;
		}
	}
}
