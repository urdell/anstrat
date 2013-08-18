package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.network.NetworkController;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class NetworkStatus extends Label {

	public NetworkStatus(){
		super("", Assets.SKIN);
		update();
	}
	
	public void update(){
		NetworkController controller = Main.getInstance().network;
		
		if (controller.isLoggedIn()) {
			setText("Your name: " + controller.getUser().displayName);
		}
		else {
			setText("Offline");
		}
	}
}