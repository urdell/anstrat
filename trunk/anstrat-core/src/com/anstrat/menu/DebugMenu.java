package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.core.Options;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DebugMenu extends MenuScreen {

	private DebugMenu() {
		super();

        contents.padTop((int) (3*Main.percentHeight));
        contents.defaults().space((int) (2*Main.percentWidth)).pad(0).top().width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.add(ComponentFactory.createMenuButton("Show FPS: "+(Options.showFps?"ON":"OFF"), new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Options.showFps = !Options.showFps;
				((TextButton)event.getListenerActor()).setText("Show FPS: "+(Options.showFps?"ON":"OFF"));
			}
		}));
        contents.row();
        contents.add(ComponentFactory.createMenuButton("Reset login", new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	//Main.getInstance().network.logout();
            	Gdx.files.local("login.bin").delete();
            	
        		Main.getInstance().network.resetLogin();
        		Main.getInstance().setScreen(AccountMenu.getInstance());
            }
        })).fillY().expandY();
        contents.row();
        
        Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(ComponentFactory.getNetworkStatusLabel());
		contents.add(centerLogin);
	}
	
	private static MenuScreen instance;
	
	public static synchronized MenuScreen getInstance(){
		if(instance == null) instance = new DebugMenu();
		return instance;
	}

	@Override
	public void dispose() {
		super.dispose();
		instance = null;
	}
}
