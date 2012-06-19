package com.anstrat.core;

import com.anstrat.gameCore.State;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.UI;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.menu.MainMenu;
import com.anstrat.menu.MenuScreen;
import com.anstrat.popup.Popup;
import com.anstrat.popup.PopupListener;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class CustomInputProcessor extends InputAdapter {

	public static Popup resignPopup = new Popup(new PopupListener() {
		@Override
		public void handle(String text) {
			State.activeState.gameInstance.resign();
			Main.getInstance().setScreen(MainMenu.getInstance());
		}
	}, "Resign?", true,
			new Label("Are you sure you want to resign?", Assets.SKIN),
			new Row(ComponentFactory.createButton("No",Popup.CANCEL), ComponentFactory.createButton("Yes",Popup.OK)));
	
	@Override
	public boolean keyDown(int keycode) {
		switch(keycode){
			case Input.Keys.ESCAPE:    // Fall through
			case Input.Keys.BACKSPACE:	// Fall through
			case Input.Keys.BACK: {
				Screen screen = Main.getInstance().getScreen();
				if (Popup.currentPopup != null && (Popup.currentPopup.handlesBackspace || (Popup.currentPopup.textFieldSelected() && keycode==Input.Keys.BACKSPACE))) {
					return true;
				}
				else if (Popup.currentPopup != null && (!Popup.currentPopup.textFieldSelected() || keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE)) {
					Popup.currentPopup.close();
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
					if(screen instanceof GEngine)
						GameInstance.saveGameInstances(Gdx.files.local("games.bin"));
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
				if((Main.getInstance().getScreen() instanceof GEngine) && Popup.currentPopup == null){
					Popup.buyUnitPopup.show();
					return true;
				}
				return false;
			}
			case Input.Keys.R: {
				if(Main.getInstance().getScreen() instanceof GEngine){
					resignPopup.show();
				}
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
