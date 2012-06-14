package com.anstrat.menu;

import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;

public class NewGameMenu extends MenuScreen {

	private static NewGameMenu me;
	//private TextButton loggedInButton;

	private NewGameMenu() {
		super();
        
		Button hostButton = ComponentFactory.createMenuButton("Host Game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(HostGameMenu.getInstance());
            }
        } );
        contents.register( "host", hostButton);
        NetworkDependentTracker.registerNetworkButton(hostButton);
        
        Button joinButton = ComponentFactory.createMenuButton("Join Specific Game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(JoinGameMenu.getInstance());
            }
        });
        contents.register("join", joinButton);
        NetworkDependentTracker.registerNetworkButton(joinButton);

        Button randomButton = ComponentFactory.createMenuButton("Join Random Game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
        		Main.getInstance().startRandomGameSearch();
            }
        } );
        contents.register("random", randomButton);
        NetworkDependentTracker.registerNetworkButton(randomButton);
        
        contents.register("hotseat",ComponentFactory.createMenuButton("Versus Human",new ClickListener() {
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
        } ));
        
        contents.register("ai", ComponentFactory.createMenuButton("Versus Computer",new ClickListener() {
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
        } ));
        
        /*loggedInButton = ComponentFactory.createMenuButton("Not logged in",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(AccountMenu.getInstance());
            }
        } );*/
        //NetworkDependentTracker.registerNetworkButton(loggedInButton);
        //layout.register("login", loggedInButton);
        
        contents.register( "login", ComponentFactory.createLoginLabel());
        
        contents.padTop((int) (3*Main.percentHeight));
        String size = String.format(" size:%d,%d", BUTTON_WIDTH, BUTTON_HEIGHT);
        
        contents.parse( " * spacing:"+(int)Main.percentWidth+" padding:0 align:top,center"
        			  + "'Online' expand:x padb:-5"
					  + "---"
    				  + "[random]" + size
    				  + "---"
    				  + "[host]" + size
    				  + "---"
    				  + "[join]" + size
       				  + "---"
    				  + "'Offline' expand:x padb:-5"
    				  + "---"
    				  + "[hotseat]" + size
    				  + "---"
    				  + "[ai] fill:y expand:y" + size
    				  + "---"
    				  + "{[login] align:center}" + size);
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
