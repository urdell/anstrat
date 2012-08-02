package com.anstrat.menu;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.core.Options;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class DebugMenu extends MenuScreen {

	private DebugMenu() {
		super();
		
		Button fps = ComponentFactory.createMenuButton("Show FPS: "+(Options.showFps?"ON":"OFF"), new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				Options.showFps = !Options.showFps;
				((TextButton)actor).setText("Show FPS: "+(Options.showFps?"ON":"OFF"));
			}
		});
		
		Button clearGames = ComponentFactory.createMenuButton("Clear games", new ClickListener(){
            @Override
            public void click(Actor actor, float x, float y){
            	List<GameInstance> games = new ArrayList<GameInstance>();
            	games.addAll(GameInstance.getActiveGames());
            
            	for(GameInstance gi : games){
            		gi.resign();
            	}
        	
            	Gdx.files.local("games.bin").delete();
            }
        });
        
		Button clearLogin = ComponentFactory.createMenuButton("Logout", new ClickListener(){
            @Override
            public void click(Actor actor, float x, float y){
            	//Main.getInstance().network.logout();
            	Gdx.files.local("login.bin").delete();
        		
        		Main.getInstance().setScreen(AccountMenu.getInstance());
            }
        });
        
		Label loginName = ComponentFactory.createLoginLabel();
        
        contents.padTop((int) (3*Main.percentHeight));
        contents.defaults().space((int) (2*Main.percentWidth)).pad(0).top().width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.add(fps);
        contents.row();
        contents.add(clearGames);
        contents.row();
        contents.add(clearLogin).expandY();
        contents.row();
        
        Table inner = new Table();
        inner.defaults().center();
        inner.add(loginName);
        
        contents.add(inner);
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
