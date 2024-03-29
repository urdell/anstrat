package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class FindMatchMenu extends MenuScreen {
	private static FindMatchMenu me;
	
	private PlayerSelecter playerSelecter;
	private Button goButton;
	
	private String mapName;
	private GameOptions.MapType mapType;

	private NetworkStatus networkStatus;
	
	private FindMatchMenu(){
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
				Main.getInstance().network.findRandomGame(options);
				
				MainMenu.getInstance().pendingGames = 1;
				
				// Go back to menu
				Main.getInstance().setScreen(MainMenu.getInstance());
			}
		});
		
		//goButton.setDisabled(true);
		//Assets.SKIN.setEnabled(goButton, !goButton.isDisabled());
		
		//contents.padTop(3f * Main.percentHeight).center();
		contents.defaults().space(Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		//contents.add(mapSelecter);
		//contents.row();
		contents.add(playerSelecter);
		contents.row();
		//contents.add(fog);
		//contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom(BUTTON_HEIGHT*0.3f);
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(networkStatus);
		contents.add(centerLogin).bottom();
	}
	
	public static synchronized FindMatchMenu getInstance() {
		if(me == null){
			me = new FindMatchMenu();
		}
		return me;
	}
	
	@Override
	public void show() {
		super.show();
		networkStatus.update();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
