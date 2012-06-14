package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class JoinGameMenu extends MenuScreen {
	
	private static JoinGameMenu me;
	
	private TextField name, password;

	public JoinGameMenu() {
		super();
		
		name     = ComponentFactory.createTextField("Game Name", null, false);
		password = ComponentFactory.createTextField("Game Password", null, true);
		Table settings = new Table(Assets.SKIN);
        settings.setBackground(Assets.SKIN.getPatch("single-border"));
        settings.register("name",name);
        settings.register("password",password);
        settings.parse("* height:"+(int)(Main.percentHeight*10) +
        		"'Name: '[name] fill:x expand:x" +
				"---" +
				"'Password: '[password] fill:x expand:x");
		contents.register("settings",settings);
		
		contents.register("join",ComponentFactory.createMenuButton( "Join Game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	if(name.getText()==""){
            		Popup.showGenericPopup("Error", "You must enter a game name.");
            		return;
            	}
            	Main.getInstance().joinGame(name.getText(), password.getText());
            }
        } ));
		contents.register("login", ComponentFactory.createLoginLabel());
		
		contents.padTop((int) (3*Main.percentHeight));
		contents.parse(
				"* spacing:5 padding:0 align:top width:"+BUTTON_WIDTH+
				"[settings] "+
				"---" +
				"[join] fill:y expand:y height:"+BUTTON_HEIGHT+
				"---" +
				"{[login] align:center}");
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
