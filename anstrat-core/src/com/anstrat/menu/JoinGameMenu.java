package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class JoinGameMenu extends MenuScreen {
	
	private static JoinGameMenu me;
	
	private TextField name, password;

	public JoinGameMenu() {
		super();
		
		name     = ComponentFactory.createTextField("Game Name", false);
		password = ComponentFactory.createTextField("Game Password", true);
		Table settings = new Table(Assets.SKIN);
        settings.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
        settings.defaults().height(Main.percentHeight*10f);
        settings.add("Name:");
        settings.add(name).fillX().expandX();
        settings.row();
        settings.add("Password:");
        settings.add(password).fillX().expandX();
		
		Button join = ComponentFactory.createMenuButton( "Join Game",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	if(name.getText()==""){
            		Popup.showGenericPopup("Error", "You must enter a game name.");
            		return;
            	}
            	//Main.getInstance().network.joinGame(name.getText(), password.getText());
            }
        });
		
		contents.padTop(3f*Main.percentHeight);
		contents.defaults().space(5).pad(0).top().width(BUTTON_WIDTH);
		contents.add(settings);
		contents.row();
		contents.add(join).fillY().expandY().height(BUTTON_HEIGHT);
	}
	
	public static synchronized JoinGameMenu getInstance() {
		if(me == null){
			me = new JoinGameMenu();
		}
		return me;
	}

	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
