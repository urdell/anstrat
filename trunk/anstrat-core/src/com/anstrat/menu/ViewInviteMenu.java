package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Invite;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.popup.Popup;
import com.anstrat.popup.TeamPopup;
import com.anstrat.popup.TeamPopup.TeamPopupListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.tablelayout.Cell;

/**
 * 
 * @author Anton
 * Used for responding to a specific invite
 */
public class ViewInviteMenu extends MenuScreen{

	
	long inviteId;
	int chosenTeam = 0, chosenGod = 0;
	
	public ViewInviteMenu(Invite invite){
		
		inviteId = invite.inviteId;
		
		float screenWidth = Gdx.graphics.getWidth();
		Button acceptButton, declineButton, teamSelectButton;
		
		teamSelectButton = ComponentFactory.createMenuButton("God and Team");
		teamSelectButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new TeamPopup(chosenGod, chosenTeam, "Select god and team", new TeamPopupListener(){

					@Override
					public void onChosen(int god, int team) {
						chosenGod = god;
						chosenTeam = team;
					}
				});
				popup.show();
			}
			
		});
		
		acceptButton = ComponentFactory.createMenuButton("Accept",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	Main.getInstance().setScreen(MainMenu.getInstance());
            	Main.getInstance().network.acceptInvite(inviteId, chosenTeam, chosenGod);
            }
        });
		
		
		declineButton = ComponentFactory.createMenuButton("Decline",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	Main.getInstance().setScreen(MainMenu.getInstance());
            	Main.getInstance().network.declineInvite(inviteId);
            }
        });
		
		
		String mapText = "";
		switch(invite.gameOptions.mapChoice){
		case GameOptions.MAP_CUSTOM:
			mapText = "Map: "+ invite.gameOptions.mapName + "\n"+
					"This is a custom map! It may be unfair.";
			break;
		case GameOptions.MAP_GENERATED:
			mapText = "Map is randomly generated.";
			break;
		case GameOptions.MAP_RANDOM:
			mapText = "Map is randomly selected among approved maps.";
			break;
		case GameOptions.MAP_SPECIFIC:
			mapText = "Map: "+ invite.gameOptions.mapName + "\n"+
					"This is an approved map.";
			break;
		}
		if(invite.gameOptions.fog){
			mapText += "\nFog is enabled.";
		}
		else{
			mapText += "\nFog is disabled.";
		}
		
		Label topTextLabel = new Label(invite.otherPlayerName + " has challenged you!\n"+mapText, Assets.SKIN);
		
		contents.padTop(screenWidth/20);
		contents.add(topTextLabel);
		contents.row();
		contents.add(teamSelectButton).size(screenWidth, screenWidth/6);
		contents.row();
		contents.add().expandY();
		contents.row();
		Table bottomRow = new Table();
		contents.add(bottomRow);
		bottomRow.add(declineButton).size(screenWidth/2, screenWidth/6);
		bottomRow.add(acceptButton).size(screenWidth/2, screenWidth/6);
		contents.validate();
		contents.debug();
		
	}
	
}
