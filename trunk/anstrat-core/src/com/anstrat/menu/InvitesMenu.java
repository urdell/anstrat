package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Invite;
import com.anstrat.core.Main;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class InvitesMenu extends MenuScreen{

	private static InvitesMenu me;
	
	private InvitesMenu(){
		refresh();
	}
	
	public void refresh(){
		contents.clear();
		contents.align(Align.top);
		contents.add(new Label("Invite menu", Assets.SKIN)).expandX();
		contents.row();

		for(Invite invite : Main.getInstance().invites.getInvites()){
			contents.add(new InviteRow(invite)).expandX().fillX();
			contents.row();
		}
	}
	
	public static synchronized InvitesMenu getInstance() {
		if(me == null){
			me = new InvitesMenu();
		}
		return me;
	}
	
}
