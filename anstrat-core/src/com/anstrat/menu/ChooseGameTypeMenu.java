package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.DisplayNameChangePopup;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ChooseGameTypeMenu extends MenuScreen {
	private static ChooseGameTypeMenu me;

	private ChooseGameTypeMenu() {
		super();
        
		Button findMatchButton = ComponentFactory.createNetworkMenuButtonChooseGame("Find match",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	if(Main.getInstance().network.isLoggedIn())
            		showIfUserNamed(FindMatchMenu.getInstance());
            }
        } );
        
        Button inviteButton = ComponentFactory.createNetworkMenuButtonChooseGame("Invite Friend",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	if(Main.getInstance().network.isLoggedIn())
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
        
        Button hotseatButton = ComponentFactory.createMenuButtonChooseGame("Versus Human",new ClickListener() {
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
        		
        Button aiButton = ComponentFactory.createMenuButtonChooseGame("Versus Computer",new ClickListener() {
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
        
        Image aiIcon = new Image(Assets.getTextureRegion("gametype_ai"));
        Image findMatchIcon = new Image(Assets.getTextureRegion("gametype-find"));
        Image hotseatIcon = new Image(Assets.getTextureRegion("gametype-hotseat"));
        Image inviteFriendIcon = new Image(Assets.getTextureRegion("gametype-invite"));
        
        contents.padTop(4f * Main.percentHeight);
        contents.top();
        contents.defaults().space(Main.percentWidth).top().right();
        contents.row();
        findMatchButton.add(findMatchIcon).width(BUTTON_WIDTH/5).center().align(Align.left).height(BUTTON_HEIGHT).fillY();
        contents.add(findMatchButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        inviteButton.add(inviteFriendIcon).width(BUTTON_WIDTH/5).align(Align.left).expandY();
        contents.add(inviteButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        hotseatButton.add(hotseatIcon).width(BUTTON_WIDTH/5).align(Align.left).height(BUTTON_HEIGHT-5);
        contents.add(hotseatButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        contents.row();
        aiButton.add(aiIcon).width(BUTTON_WIDTH/5).align(Align.left).height(BUTTON_HEIGHT-5);
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
