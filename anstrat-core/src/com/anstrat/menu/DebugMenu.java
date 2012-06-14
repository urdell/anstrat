package com.anstrat.menu;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;

public class DebugMenu extends MenuScreen {

	private DebugMenu() {
		super();
        
		contents.register("clearGames", ComponentFactory.createMenuButton("Clear games", new ClickListener(){
            @Override
            public void click(Actor actor, float x, float y){
            	List<GameInstance> games = new ArrayList<GameInstance>();
            	games.addAll(GameInstance.getActiveGames());
            
            	for(GameInstance gi : games){
            		gi.resign();
            	}
        	
            	Gdx.files.local("games.bin").delete();
            }
        }));
        
		contents.register("clearLogin",ComponentFactory.createMenuButton("Logout", new ClickListener(){
            @Override
            public void click(Actor actor, float x, float y){
            	Main.getInstance().network.logout();
            	Gdx.files.local("login.bin").delete();
        		
        		Main.getInstance().setScreen(AccountMenu.getInstance());
            }
        }));
        
        contents.register("loginname", ComponentFactory.createLoginLabel());
        
        contents.padTop((int) (3*Main.percentHeight));
        contents.parse(	"* spacing:"+(int) (2*Main.percentWidth)+" padding:0 align:top width:"+BUTTON_WIDTH+" height:"+BUTTON_HEIGHT+
    					"[clearGames]"+
    					"---"+
    					"[clearLogin] expand:y"+
    					"---"+
    					"{*align:center [loginname]}");
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
