package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.menu.MainMenu;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConfirmPopup extends Popup {
	
	public ConfirmPopup(final GameInstance game) {
		Label message = new Label("Are you sure you want to delete that game?", new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE));
		Button yes = ComponentFactory.createMenuButton("Yes!", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Main.getInstance().games.endGame(game);
    			MainMenu.getInstance().updateGamesList();
    			Popup.getCurrentPopup().close();
			}
		});
		Button no = ComponentFactory.createMenuButton("No...", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		Table buttons = new Table();
		buttons.add(yes).width(Main.percentWidth*20f).height(Main.percentHeight*8f).padRight(Main.percentWidth*10);
		buttons.add(no).width(Main.percentWidth*20f).height(Main.percentHeight*8f);
		
		this.add(message).center();
		this.row();
		this.add(buttons);
	}
	
	@Override
	public void resize(float width, float height){
		// Force popup to take up the whole window
		this.setSize(width, height);
		super.resize(width, height);
	}
}
