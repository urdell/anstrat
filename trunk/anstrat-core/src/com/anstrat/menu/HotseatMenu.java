package com.anstrat.menu;

import java.awt.Dimension;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.GeneratedMapPopup;
import com.anstrat.popup.GeneratedMapPopup.GeneratedMapPopupHandler;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.anstrat.popup.TeamPopup;
import com.anstrat.popup.TeamPopup.TeamPopupListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class HotseatMenu extends MenuScreen implements GeneratedMapPopupHandler {
	private static HotseatMenu me;
	
	public int player1god = PlayerAbilityType.GOD_HEL, player1team = TeamPopup.TEAM_DD, player2god = PlayerAbilityType.GOD_HEL, player2team = TeamPopup.TEAM_VV;
	
	private boolean specificMap = false;
	private boolean generatedMap = false;
	private Dimension mapDimension = new Dimension(10,10);
	private boolean randomServerdMap = false;
	private boolean randomCustomMap = false;
	
	private final Label mapLabel;
	private final GeneratedMapPopup mapSizePopup;
	
	private HotseatMenu(){
        
		Table map = new Table(Assets.SKIN);
		map.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		
		//TextField timeLimit = ComponentFactory.createTextField("Time limit (in minutes)", false);
		
		mapSizePopup = new GeneratedMapPopup(this);
		
		final CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
		
		mapLabel = new Label("No map chosen", Assets.SKIN);
		
		Button mapSpec = ComponentFactory.createButton("Specific", new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new MapsPopup(new MapsPopupHandler() {
						@Override
						public void mapSelected(String map){
							specificMap = true;
							generatedMap = false;
							randomCustomMap = false;
							randomServerdMap = false;
							mapLabel.setText(map);
						}
					}, false, "Choose specific map", Assets.getMapList(true, true));
        		
        		popup.show();
				
			}
			
		});
		Button mapCustomRandom = ComponentFactory.createButton("Random custom", new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				specificMap = false;
				generatedMap = false;
				randomCustomMap = true;
				randomServerdMap = false;
				mapLabel.setText("Random custom map");
			}
			
		});
		Button mapServerRandom = ComponentFactory.createButton("Random server", new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				specificMap = false;
				generatedMap = false;
				randomCustomMap = false;
				randomServerdMap = true;
				mapLabel.setText("Random server map");
				
			}
			
		});
		Button mapGenerate = ComponentFactory.createButton("Generated", new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				/*specificMap = false;
				generatedMap = true;
				randomCustomMap = false;
				randomServerdMap = false;
				mapLabel.setText("Generated map");*/
				mapSizePopup.show();
			}
			
		});
		
		
		Table mapTable1 = new Table();
		mapTable1.add(mapSpec).size(Main.percentWidth*37f, Main.percentHeight*8f);
		mapTable1.add(mapServerRandom).size(Main.percentWidth*37f, Main.percentHeight*8f);
		
		Table mapTable2 = new Table();
		mapTable2.add(mapGenerate).size(Main.percentWidth*37f, Main.percentHeight*8f);
		mapTable2.add(mapCustomRandom).size(Main.percentWidth*37f, Main.percentHeight*8f);
		
		map.defaults().height(Main.percentHeight*8f);
		map.add(mapTable1);
		map.row();
		map.add(mapTable2);
		map.row();
		map.add(mapLabel).center();
		
		Table player1table = new Table(); 
		Table player2table = new Table();
		
		player1table.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		player2table.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		
		final TextButton player1nameButton = new TextButton("Player 1", Assets.SKIN);
		final TextButton player2nameButton = new TextButton("Player 2", Assets.SKIN);
		TextButton player1teamButton = new TextButton("God and Team", Assets.SKIN);
		player1teamButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new TeamPopup(player1god, player1team, "Select "+player1nameButton.getText()+"'s god and team", new TeamPopupListener(){

					@Override
					public void onChosen(int god, int team) {
						player1god = god;
						player1team = team;
					}
				});
				popup.show();
			}
			
		});
		TextButton player2teamButton = new TextButton("God and Team", Assets.SKIN);
		player2teamButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new TeamPopup(player2god, player2team, "Select "+player2nameButton.getText()+"'s god and team", new TeamPopupListener(){

					@Override
					public void onChosen(int god, int team) {
						player2god = god;
						player2team = team;
					}
				});
				popup.show();
			}
		});
		
		player1table.defaults().height(Main.percentHeight*8f);
		player1table.add(player1nameButton).fillX().expandX();
		player1table.row();
		player1table.add(player1teamButton).fillX().expandX();
		
		player2table.defaults().height(Main.percentHeight*8f);
		player2table.add(player2nameButton).fillX().expandX();
		player2table.row();
		player2table.add(player2teamButton).fillX().expandX();
		        
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (generatedMap) {
					Main.getInstance().games.createHotseatGame(fog.isChecked(), mapDimension.width, mapDimension.height ,player1god, player1team, player2god, player2team).showGame(true);
				}
				else if (randomServerdMap) {
					String[] maps = Assets.getMapList(false, true);
					Map map = Assets.loadMap(getRandom(maps));
					map.fogEnabled = fog.isChecked();
					Main.getInstance().games.createHotseatGame(map, player1god, player1team, player2god, player2team).showGame(true);
				}
				else if (randomCustomMap) {
					String[] maps = Assets.getMapList(true, true);
					Map map = Assets.loadMap(getRandom(maps));
					map.fogEnabled = fog.isChecked();
					Main.getInstance().games.createHotseatGame(map, player1god, player1team, player2god, player2team).showGame(true);
				}
				else if (specificMap) { //specific map
					String mapName = mapLabel.getText().toString();
					Map map = Assets.loadMap(mapName);
					map.fogEnabled = fog.isChecked();
					Main.getInstance().games.createHotseatGame(map, player1god, player1team, player2god, player2team).showGame(true);
				}
		   }
		} );
		
		
		contents.padTop(3f*Main.percentHeight).center();
		contents.defaults().space(Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(map);
		contents.row();
		contents.add(player1table);
		contents.row();
		contents.add(player2table);
		contents.row();
		contents.add(fog);
		contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom(BUTTON_HEIGHT*0.3f);
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(ComponentFactory.createLoginLabel());
		contents.add(centerLogin).bottom();
	}
	
	public static String getRandom(String... strings) {
		int amount = strings.length;
		double random = Math.random();
		return strings[(int)(amount*random)];
	}
	
	public static synchronized HotseatMenu getInstance() {
		if(me == null){
			me = new HotseatMenu();
		}
		return me;
	}

	@Override
	public void sizeSelected(String size) {
		mapLabel.setText("Generated map ("+size+")");
		if (size.equals(GeneratedMapPopup.LARGE)) {
			mapDimension = new Dimension(16,16);
		}
		else if (size.equals(GeneratedMapPopup.MEDIUM)) {
			mapDimension = new Dimension(12,12);
		}
		else if (size.equals(GeneratedMapPopup.SMALL)) {
			mapDimension = new Dimension(8,8);
		}
		else { //TODO random???
			mapDimension = new Dimension(16,16);
		}
		specificMap = false;
		generatedMap = true;
		randomCustomMap = false;
		randomServerdMap = false;
		
		//TODO: Set map size etc.
	}
}
