package com.anstrat.menu;

import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class ChooseGameTypeMenu extends MenuScreen {
	private static ChooseGameTypeMenu me;

	private ChooseGameTypeMenu() {
		super();
        
		Button findMatchButton = ComponentFactory.createNetworkMenuButton("Find match",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(FindMatchMenu.getInstance());
            }
        } );
        
        Button inviteButton = ComponentFactory.createNetworkMenuButton("Invite Friend",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	//TODO Main.getInstance().setScreen(JoinGameMenu.getInstance());
            }
        });

        Button campaignButton = ComponentFactory.createNetworkMenuButton("Campaign",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
        		//Main.getInstance().network.startRandomGameSearch();
            	//int team = Player.getRandomTeam();
            	//TODO Main.getInstance().network.findRandomGame(team, Player.getRandomGodFromTeam(team));
            }
        } );
        
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
        			Main.getInstance().games.createHotseatGame(null).showGame(true);
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
        			Main.getInstance().games.createAIGame(null, 1).showGame(true);
        		}
            }
        });
        

        Label login = ComponentFactory.createLoginLabel();
        
        contents.padTop((int) (3*Main.percentHeight));
        contents.defaults().space((int)Main.percentWidth).pad(0).top().center();
        contents.row();
        contents.add(findMatchButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add(inviteButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        contents.add(campaignButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
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
