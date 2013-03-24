package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.Map;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.GeneratedMapPopup;
import com.anstrat.popup.GeneratedMapPopup.GeneratedMapPopupHandler;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.anstrat.popup.TeamPopup;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class HotseatMenu extends MenuScreen implements GeneratedMapPopupHandler {
	private static HotseatMenu me;
	
	public int player1team = TeamPopup.TEAM_DD, player2team = TeamPopup.TEAM_VV;
	
	private boolean specificMap = false;
	private boolean generatedMap = false;
	private Dimension mapDimension = new Dimension(10,10);
	//private boolean randomServerdMap = false;
	//private boolean randomCustomMap = false;
	
	private final Label mapTitel, mapLabel;
	private final GeneratedMapPopup mapSizePopup;
	
	private HotseatMenu(){
        
		Table map = new Table(Assets.SKIN);
		map.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		
		//TextField timeLimit = ComponentFactory.createTextField("Time limit (in minutes)", false);
		
		mapSizePopup = new GeneratedMapPopup(this);
		
		final CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
		
		mapTitel = new Label("Map options", Assets.SKIN);
		mapLabel = new Label("No map chosen", Assets.SKIN);
		
		Button mapSpec = ComponentFactory.createButton("Select map", new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new MapsPopup(new MapsPopupHandler() {
						@Override
						public void mapSelected(String map){
							specificMap = true;
							generatedMap = false;
							//randomCustomMap = false;
							//randomServerdMap = false;
							mapLabel.setText(map);
						}
					}, false, "Choose specific map", Assets.getMapList(true, true));
        		
        		popup.show();
				
			}
			
		});
		/*Button mapCustomRandom = ComponentFactory.createButton("Random custom", new ClickListener() {
			

			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				specificMap = false;
				generatedMap = false;
				//randomCustomMap = true;
				//randomServerdMap = false;
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
			
		});*/
		Button mapGenerate = ComponentFactory.createButton("Generate map", new ClickListener() {

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
		mapTable1.add(mapSpec).size(Main.percentWidth*40, Main.percentHeight*8f);
		mapTable1.add(mapGenerate).size(Main.percentWidth*40, Main.percentHeight*8f);
		//mapTable1.add(mapServerRandom).size(Main.percentWidth*37f, Main.percentHeight*8f);
		
		/*Table mapTable2 = new Table();
		mapTable2.add(mapGenerate).size(Main.percentWidth*37f, Main.percentHeight*8f);*/
		//mapTable2.add(mapCustomRandom).size(Main.percentWidth*37f, Main.percentHeight*8f);
		
		map.defaults().height(Main.percentHeight*8f);
		map.add(mapTitel).center();
		map.row();
		map.add(mapTable1);
		map.row();
		map.add(mapLabel).center();
		
		Table player1table = new Table(); 
		Table player2table = new Table();
		
		player1table.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		player2table.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		
		final TextField player1nameButton = ComponentFactory.createTextField("Player 1", false);
		final TextField player2nameButton = ComponentFactory.createTextField("Player 2", false);
		
		Table teamTable1 = new Table();
		Table teamTable2 = new Table();
		
		final CheckBox teamVV1 = ComponentFactory.createCheckBox("");
		final CheckBox teamVV2 = ComponentFactory.createCheckBox("");
		final CheckBox teamDD1 = ComponentFactory.createCheckBox("");
		final CheckBox teamDD2 = ComponentFactory.createCheckBox("");
		
		Image vv1 =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.SWORD)));
		Image dd1 =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.JOTUN)));
		Image vv2 =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.SWORD)));
		Image dd2 =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.JOTUN)));
		
		Button questionButton = ComponentFactory.createButton(Assets.getTextureRegion("help-button"), new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//TODO help popup
		    }
		
		});
		
		Button questionButton2 = ComponentFactory.createButton(Assets.getTextureRegion("help-button"), new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				//TODO help popup
		    }
		
		});
		
		questionButton2.setScaleX(0.5f);
		vv1.setScaleX(0.6f);
		dd1.setScaleX(0.6f);
		vv2.setScaleX(0.6f);
		dd2.setScaleX(0.6f);
		
		teamVV1.setChecked(true);
		teamDD1.setChecked(false);
		teamDD2.setChecked(true);
		teamVV2.setChecked(false);
		
		teamVV1.addListener(new ClickListener() {
			 @Override
	         public void clicked(InputEvent event, float x, float y) {
	         	teamVV1.setChecked(true);
	         	teamDD1.setChecked(false);
	         }
		});
		teamDD1.addListener(new ClickListener() {
			 @Override
	         public void clicked(InputEvent event, float x, float y) {
	         	teamVV1.setChecked(false);
	         	teamDD1.setChecked(true);
	         }
		});
		teamVV2.addListener(new ClickListener() {
			 @Override
	         public void clicked(InputEvent event, float x, float y) {
	         	teamVV2.setChecked(true);
	         	teamDD2.setChecked(false);
	         }
		});
		teamDD2.addListener(new ClickListener() {
			 @Override
	         public void clicked(InputEvent event, float x, float y) {
	         	teamVV2.setChecked(false);
	         	teamDD2.setChecked(true);
	         }
		});
		
		teamTable1.add(teamVV1);
		teamTable1.add(vv1);
		teamTable1.add(teamDD1);
		teamTable1.add(dd1);
		//teamTable1.add(questionButton);
		teamTable2.add(teamVV2);
		teamTable2.add(vv2);
		teamTable2.add(teamDD2);
		teamTable2.add(dd2);
		//teamTable2.add(questionButton2);
		
		/*TextButton player1teamButton = new TextButton("Team", Assets.SKIN);
		player1teamButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new TeamPopup(player1team, "Select "+player1nameButton.getText()+"'s team", new TeamPopupListener(){

					@Override
					public void onChosen(int team) {
						player1team = team;
					}
				});
				popup.show(); 
			}
			
		});
		TextButton player2teamButton = new TextButton("Team", Assets.SKIN);
		player2teamButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new TeamPopup(player2team, "Select "+player2nameButton.getText()+"'s team", new TeamPopupListener(){

					@Override
					public void onChosen(int team) {
						player2team = team;
					}
				});
				popup.show();
			}
		});
		*/
		player1table.defaults().height(Main.percentHeight*8f);
		player1table.add(player1nameButton).fillX().expandX();
		player1table.row();
		player1table.add(teamTable1).fillX().expandX();
		
		player2table.defaults().height(Main.percentHeight*8f);
		player2table.add(player2nameButton).fillX().expandX();
		player2table.row();
		player2table.add(teamTable2).fillX().expandX();
		        
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				
				if(teamDD1.isChecked()) {
					player1team = TeamPopup.TEAM_DD;
				}
				else if (teamVV1.isChecked()){
					player1team = TeamPopup.TEAM_VV;
				}
				if(teamDD2.isChecked()) {
					player2team = TeamPopup.TEAM_DD;
				}
				else if (teamVV2.isChecked()) {
					player2team = TeamPopup.TEAM_VV;
				}
				
				if (generatedMap) {
					Main.getInstance().games.createHotseatGame(fog.isChecked(), mapDimension.width, mapDimension.height , player1team, player2team).showGame(true);
				}
				else if (specificMap) {
					String mapName = mapLabel.getText().toString();
					Map map = Assets.loadMap(mapName);
					map.fogEnabled = fog.isChecked();
					Main.getInstance().games.createHotseatGame(map, player1team, player2team).showGame(true);
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
		//randomCustomMap = false;
		//randomServerdMap = false;
		
		//TODO: Set map size etc.
	}
	
	/**
	 * Custom dimension class. Can't use java.awt on android. 
	 */
	private class Dimension{
		public int width, height;
		
		public Dimension(int w, int h){
			this.width = w;
			this.height = h;
		}
	}
}
