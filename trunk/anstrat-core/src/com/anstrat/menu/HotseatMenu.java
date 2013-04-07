package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.popup.TeamPopup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HotseatMenu extends MenuScreen {
	private static HotseatMenu me;
	public int player1team = TeamPopup.TEAM_DD, player2team = TeamPopup.TEAM_VV;

	private MapSelecter mapSelecter;
	
	private HotseatMenu(){
		mapSelecter = new MapSelecter();
		
		final CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
		
		final PlayerSelecter player1Selecter = new PlayerSelecter("Player 1");
		final PlayerSelecter player2Selecter = new PlayerSelecter("Player 2");
		
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				if(mapSelecter.getMapTypeSelection() == GameOptions.MapType.SPECIFIC){
					Map map = Assets.loadMap(mapSelecter.getMapNameSelection());
					map.fogEnabled = fog.isChecked();
					Main.getInstance().games.createHotseatGame(map, player1Selecter.getTeam(), player2Selecter.getTeam()).showGame(true);
				}
				else {
					Dimension d = getMapSize(mapSelecter.getMapTypeSelection());
					Main.getInstance().games.createHotseatGame(
							fog.isChecked(),
							d.width,
							d.height,
							player1team,
							player2team).showGame(true);
				}
		   }
		});
		
		contents.padTop(3f*Main.percentHeight).center();
		contents.defaults().space(Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(mapSelecter);
		contents.row();
		contents.add(player1Selecter);
		contents.row();
		contents.add(player2Selecter);
		contents.row();
		contents.add(fog);
		contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom(BUTTON_HEIGHT*0.3f);
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(ComponentFactory.createLoginLabel());
		contents.add(centerLogin).bottom();
	}
	
	private static Dimension getMapSize(GameOptions.MapType t){
		if(t == GameOptions.MapType.GENERATED_SIZE_LARGE){
			return new Dimension(16,16);
		}
		else if(t == GameOptions.MapType.GENERATED_SIZE_MEDIUM){
			return new Dimension(12,12);
		}
		else if(t == GameOptions.MapType.GENERATED_SIZE_SMALL){
			return new Dimension(8,8);
		}
		else if(t == GameOptions.MapType.GENERATED_SIZE_RANDOM){
			// TODO: Randomize! Make sure to keep a good width/height ratio
			return new Dimension(16,16);
		}
		else {
			throw new IllegalArgumentException("Was not expecting " + t);
		}
	}
	
	/**
	 * Custom dimension class. Can't use java.awt on android. 
	 */
	private static class Dimension{
		public int width, height;
		
		public Dimension(int w, int h){
			this.width = w;
			this.height = h;
		}
	}
	
	public static synchronized HotseatMenu getInstance() {
		if(me == null){
			me = new HotseatMenu();
		}
		return me;
	}
}
