package com.anstrat.menu;

import java.util.Random;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameOptions.MapType;
import com.anstrat.popup.Popup;
import com.anstrat.popup.MapTypePopup.MapSelectionListener;
import com.anstrat.util.Dimension;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HotseatMenu extends MenuScreen implements MapSelectionListener {
	private static HotseatMenu me;

	private MapSelecter mapSelecter;
	private String mapName;
	private GameOptions.MapType mapType;
	private Button goButton;
	
	private HotseatMenu(){
		final PlayerSelecter player1Selecter = new PlayerSelecter("Player 1");
		final PlayerSelecter player2Selecter = new PlayerSelecter("Player 2");
		
		goButton = ComponentFactory.createMenuButton("GO!", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Player p1 = new Player(0, player1Selecter.getPlayerName(), player1Selecter.getTeam());
				Player p2 = new Player(1, player2Selecter.getPlayerName(), player2Selecter.getTeam());
				
				Player[] players = new Player[]{ p1, p2 };
				
				Popup.showGenericPopup(player1Selecter.getPlayerName(), "asdas");
				
				// Load named map (if selected)
				if(mapType == GameOptions.MapType.SPECIFIC){
					Map map = Assets.loadMap(mapName);
					Main.getInstance().games.createHotseatGame(map, players).showGame(true);
				}
				else if(mapType != null){
					Dimension d = GameOptions.MapType.getMapSize(mapType, new Random());
					Main.getInstance().games.createHotseatGame(d.width, d.height, players).showGame(true);
				}
		   }
		});
		
		goButton.setDisabled(true);
		Assets.SKIN.setEnabled(goButton, !goButton.isDisabled());
		
		mapSelecter = new MapSelecter(this);
		
		contents.top().padTop(Main.percentHeight * 4f);
		contents.defaults().space(0).pad(0).top().width(BUTTON_WIDTH);
		contents.add(mapSelecter);
		contents.row();
		contents.add(player1Selecter);
		contents.row();
		contents.add(player2Selecter);
		contents.row();
		//contents.add(fog);
		//contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom(BUTTON_HEIGHT*0.3f);
	}
	
	public static synchronized HotseatMenu getInstance() {
		if(me == null){
			me = new HotseatMenu();
		}
		return me;
	}

	@Override
	public void mapSelected(MapType type, String mapName) {
		this.mapType = type;
		this.mapName = mapName;
		this.goButton.setDisabled(false);
		Assets.SKIN.setEnabled(goButton, !goButton.isDisabled());
	}
}
