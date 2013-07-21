package com.anstrat.menu;

import java.util.Random;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.menu.MapSelecter.MapSelectionHandler;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameOptions.MapType;
import com.anstrat.util.Dimension;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class AiMenu extends MenuScreen implements MapSelectionHandler {
	private static AiMenu me;
	public int player1team, player2team;

	private MapSelecter mapSelecter;
	
	private String mapName;
	private GameOptions.MapType mapType;
	private Button goButton;
	
	private AiMenu(){
		mapSelecter = new MapSelecter(this);
		
		final CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
		
		final PlayerSelecter player1Selecter = new PlayerSelecter("Player 1");
		
		goButton = ComponentFactory.createMenuButton("GO!", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Load named map (if selected)
				if(mapType == GameOptions.MapType.SPECIFIC){
					Map map = Assets.loadMap(mapName);
					map.fogEnabled = fog.isChecked();
					player1team = player1Selecter.getTeam();
					Main.getInstance().games.createAIGame(
							map, 
							player1team, 
							1).showGame(true);
				}
				else if(mapType != null){
					Dimension d = GameOptions.MapType.getMapSize(mapType, new Random());
					player1team = player1Selecter.getTeam();
					Main.getInstance().games.createAIGame(
							fog.isChecked(),
							d.width,
							d.height,
							player1team,
							1).showGame(true);
				}
		   }
		});
		goButton.setDisabled(true);
		Assets.SKIN.setEnabled(goButton, !goButton.isDisabled());
		
		contents.padTop(3f*Main.percentHeight).center();
		contents.defaults().space(Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(mapSelecter);
		contents.row();
		contents.add(player1Selecter);
		contents.row();
		contents.add(fog);
		contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom(BUTTON_HEIGHT*0.3f);
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(ComponentFactory.createLoginLabel());
		contents.add(centerLogin).bottom();
		

		mapSelecter.sizeSelected(MapType.GENERATED_SIZE_MEDIUM);
	}
	
	public static synchronized AiMenu getInstance() {
		if(me == null){
			me = new AiMenu();
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

