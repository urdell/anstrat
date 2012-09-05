package com.anstrat.menu;

import com.anstrat.core.Invite;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;

/**
 * 
 * @author Anton
 * Used for responding to a specific invite
 */
public class ViewInviteMenu extends MenuScreen{

	long inviteId;
	
	public ViewInviteMenu(Invite invite){
		
		inviteId = invite.inviteId;
		
		Button respondButton = ComponentFactory.createMenuButton("Ugly response to this game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(MainMenu.getInstance());
            	Main.getInstance().network.acceptInvite(inviteId, 1, 1);
            }
        });
		contents.add(respondButton);
	}
	
}
