package com.anstrat.menu;

import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class NewGameMenu extends MenuScreen {

	private static NewGameMenu me;

	private NewGameMenu() {
		super();
        
		Button hostButton = ComponentFactory.createMenuButton("Host Game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(HostGameMenu.getInstance());
            }
        } );
		
        NetworkDependentTracker.registerNetworkButton(hostButton);
        
        Button joinButton = ComponentFactory.createMenuButton("Join Specific Game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(JoinGameMenu.getInstance());
            }
        });
        
        NetworkDependentTracker.registerNetworkButton(joinButton);

        Button randomButton = ComponentFactory.createMenuButton("Join Random Game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
        		Main.getInstance().network.startRandomGameSearch();
            }
        } );
        
        NetworkDependentTracker.registerNetworkButton(randomButton);
        
        Button hotseatButton = ComponentFactory.createMenuButton("Versus Human",new ClickListener() {
        	@Override
            public void click(Actor actor, float x, float y){
        		
        		MainMenu.versusAI = false;
        		Popup popup = MainMenu.getInstance().getMapsPopup();
        		
        		if(popup != null){
        			// Show map select popup
        			popup.show();
        		}
        		else{
        			// No maps found, create game with a random map
        			GameInstance.createHotseatGame(null).showGame(true);
        		}
            }
        });
        		
        Button aiButton = ComponentFactory.createMenuButton("Versus Computer",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	MainMenu.versusAI = true;
            	Popup popup = MainMenu.getInstance().getMapsPopup();
            	
        		if(popup != null){
        			// Show map select popup
        			popup.show();
        		}
        		else{
        			// No maps found, create game with a random map
        			GameInstance.createAIGame(null, 1).showGame(true);
        		}
            }
        });
        

        Label login = ComponentFactory.createLoginLabel();
        
        contents.padTop((int) (3*Main.percentHeight));
        contents.defaults().space((int)Main.percentWidth).pad(0).top().center();
        contents.add("Online").expandX().padBottom(-5);
        contents.row();
        contents.add(randomButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add(hostButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add(joinButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add("Offline").expandX().padBottom(-5);
        contents.row();
        contents.add(hotseatButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add(aiButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        
        Table inner = new Table();
        inner.defaults().center();
        inner.add(login);
        
        contents.add().fillY().expandY();
        contents.row();
        contents.add(inner);
	}
	
	public static synchronized NewGameMenu getInstance() {
		if(me == null){
			me = new NewGameMenu();
		}
		return me;
	}

	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
