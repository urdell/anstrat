package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameOptions.MapType;
import com.anstrat.popup.InvitePopup;
import com.anstrat.popup.InvitePopup.InvitePopupHandler;
import com.anstrat.popup.MapTypePopup.MapSelectionListener;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InviteMatchMenu extends MenuScreen implements MapSelectionListener {
	private static InviteMatchMenu me;
	
	private TextButton friendButton;
	private String invitedFriend;
	
	private MapSelecter mapSelecter;
	private PlayerSelecter playerSelecter;
	private Button goButton;
	
	private String mapName;
	private GameOptions.MapType mapType;
	
	private NetworkStatus networkStatus;
	
	private InviteMatchMenu(){      
		playerSelecter = new PlayerSelecter();
		networkStatus = new NetworkStatus();
		
		final CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
		
		goButton = ComponentFactory.createMenuButton("GO!", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(goButton.isDisabled()) return;
				
				// Load named map (if selected)
				Map map = mapType == GameOptions.MapType.SPECIFIC 
							      ? Assets.loadMap(mapName)
							      : null;

				GameOptions options = new GameOptions(map, mapType, playerSelecter.getGod(), playerSelecter.getTeam(), fog.isChecked());
				
				Main.getInstance().network.invitePlayer(invitedFriend, options);
				
				// Go back to menu
				Main.getInstance().setScreen(MainMenu.getInstance());
			}
		});
		
		friendButton = ComponentFactory.createButton("Choose friend", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new InvitePopup(new InvitePopupHandler() {
					@Override
					public void friendSelected(String friend) {
						Main.getInstance().friends.createFriend(friend);
						Main.getInstance().friends.saveFriends();
						friendButton.setText(friend);
						invitedFriend = friend;
						
						checkGoButtonStatus();
					}
					
				}, "Select or write friend's username");
				popup.show();	
			}
		});
		
		fixLayout();
	}
	
	public static synchronized InviteMatchMenu getInstance() {
		if(me == null){
			me = new InviteMatchMenu();
		}
		else
			me.fixLayout();
		return me;
	}
	
	public void fixLayout(){
		contents.clear();
		
		mapSelecter = new MapSelecter(this);
		
		contents.padTop(Main.percentHeight * 4f);
		contents.top();
		contents.defaults().space(Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(mapSelecter);
		contents.row();
		contents.add(playerSelecter);
		contents.row();
		contents.add(friendButton).height(BUTTON_HEIGHT);
		contents.row();
		//contents.add(fog);
		//contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom(BUTTON_HEIGHT*0.3f);
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(networkStatus);
		contents.add(centerLogin).bottom();
		
		checkGoButtonStatus();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		friendButton = null;
		me = null;
	}
	
	@Override
	public void show() {
		super.show();
		networkStatus.update();
	}
	
	private void checkGoButtonStatus(){
		this.goButton.setDisabled(invitedFriend == null || this.mapType == null);
		Assets.SKIN.setEnabled(goButton, !goButton.isDisabled());
	}

	@Override
	public void mapSelected(MapType type, String mapName) {
		this.mapType = type;
		this.mapName = mapName;
		
		checkGoButtonStatus();
	}
}
