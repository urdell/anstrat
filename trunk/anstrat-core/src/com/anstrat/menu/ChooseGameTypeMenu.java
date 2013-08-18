package com.anstrat.menu;

import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.DisplayNameChangePopup;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ChooseGameTypeMenu extends MenuScreen {
	private static ChooseGameTypeMenu me;

	private ChooseGameTypeMenu() {
		super();
        
		Button findMatchButton = ComponentFactory.createNetworkMenuButton("Find match",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	showIfUserNamed(FindMatchMenu.getInstance());
            }
        } );
        
        Button inviteButton = ComponentFactory.createNetworkMenuButton("Invite Friend",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	showIfUserNamed(InviteMatchMenu.getInstance());
            }
        });

        /*Button campaignButton = ComponentFactory.createNetworkMenuButton("Campaign",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
        		//Main.getInstance().network.startRandomGameSearch();
            	//int team = Player.getRandomTeam();
            	//TODO Main.getInstance().network.findRandomGame(team, Player.getRandomGodFromTeam(team));
            }
        } ); */
        
        Button hotseatButton = ComponentFactory.createMenuButton("Versus Human",new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		
        		Main.getInstance().setScreen(HotseatMenu.getInstance());
        		/*
        		MainMenu.versusAI = false;
        		Popup popup = MainMenu.getInstance().getMapsPopup();
        		
        		if(popup != null){
        			// Show map select popup
        			popup.show();
        		}
        		else{
        			// No maps found, create game with a random map
        			Main.getInstance().games.createHotseatGame(null).showGame(true);
        		}*/
            }
        });
        		
        Button aiButton = ComponentFactory.createMenuButton("Versus Computer",new ClickListener() {
            @Override
            public void clicked(InputEvent event,float x,float y ){
            	Main.getInstance().setScreen(AiMenu.getInstance());
            	/*
            	MainMenu.versusAI = true;
            	Popup popup = MainMenu.getInstance().getMapsPopup();
            	
        		if(popup != null){
        			// Show map select popup
        			popup.show();
        		}
        		else{
        			// No maps found, create game with a random map
        			Main.getInstance().games.createAIGame(null, 1).showGame(true);
        		}*/
            }
            
        });
        
        contents.padTop(4f * Main.percentHeight);
        contents.top();
        contents.defaults().space(Main.percentWidth).pad(0).top().center();
        contents.row();
        contents.add(findMatchButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add(inviteButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add(hotseatButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add(aiButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
	}
	
	private void showIfUserNamed(final Screen screen){
    	if(Main.getInstance().network.getUser().usingDefaultName){
    		// Force user to choose a name
    		new DisplayNameChangePopup(new Runnable() {
				public void run() {
					Main.getInstance().setScreen(screen);
				}
			}).show();
    	}
    	else{
    		// Show directly
    		Main.getInstance().setScreen(screen);
    	}
	}
	
	public static synchronized ChooseGameTypeMenu getInstance() {
		if(me == null){
			me = new ChooseGameTypeMenu();
		}
		return me;
	}

	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
