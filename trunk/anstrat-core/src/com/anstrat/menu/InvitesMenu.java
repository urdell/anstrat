package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Invite;
import com.anstrat.core.Main;
import com.anstrat.network.protocol.GameOptions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

public class InvitesMenu extends MenuScreen{

	private static InvitesMenu me;
	private InvitesMenu(){
		//contents.debug();
		contents.align(Align.top);
		contents.add(new Label("Invite menu", Assets.SKIN)).expandX();
		contents.row();
		if(Main.getInstance().invites.getInvites().isEmpty()){
			//If there are no invites - add a dummy invite for testing
			Main.getInstance().invites.recievedInvite(1337, "Dummy invite sender", new GameOptions(null, GameOptions.MapType.GENERATED_SIZE_MEDIUM, 1, 1, true));
		}
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