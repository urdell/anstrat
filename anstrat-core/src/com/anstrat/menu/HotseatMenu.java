package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.anstrat.popup.TeamPopup;
import com.anstrat.popup.TeamPopup.TeamPopupListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class HotseatMenu extends MenuScreen {
	private static HotseatMenu me;
	
	public int player1god = PlayerAbilityType.GOD_HEL, player1team = TeamPopup.TEAM_DD, player2god = PlayerAbilityType.GOD_HEL, player2team = TeamPopup.TEAM_VV;
	
	private boolean specificMap = false;
	private boolean generatedMap = false;
	private boolean randomServerdMap = false;
	private boolean randomCustomMap = false;
	
	private HotseatMenu(){
        
		Table map = new Table(Assets.SKIN);
		map.setBackground(Assets.SKIN.getPatch("single-border"));
		
		//TextField timeLimit = ComponentFactory.createTextField("Time limit (in minutes)", false);
		
		
		
		CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
		
		final Label mapLabel = new Label(Assets.SKIN);
		mapLabel.setText("No map chosen");
		
		Button mapSpec = ComponentFactory.createButton("Specific", new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
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
			public void click(Actor actor, float x, float y) {
				
				specificMap = false;
				generatedMap = false;
				randomCustomMap = true;
				randomServerdMap = false;
				mapLabel.setText("Random custom map");
			}
			
		});
		Button mapServerRandom = ComponentFactory.createButton("Random server", new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				specificMap = false;
				generatedMap = false;
				randomCustomMap = false;
				randomServerdMap = true;
				mapLabel.setText("Random server map");
				
			}
			
		});
		Button mapGenerate = ComponentFactory.createButton("Generated", new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				specificMap = false;
				generatedMap = true;
				randomCustomMap = false;
				randomServerdMap = false;
				mapLabel.setText("Generated map");
				
			}
			
		});
		
		
		Table mapTable1 = new Table();
		mapTable1.add(mapSpec).size((int)(Main.percentWidth*37), (int)(Main.percentHeight*8));
		mapTable1.add(mapServerRandom).size((int)(Main.percentWidth*37), (int)(Main.percentHeight*8));
		
		Table mapTable2 = new Table();
		mapTable2.add(mapGenerate).size((int)(Main.percentWidth*37), (int)(Main.percentHeight*8));
		mapTable2.add(mapCustomRandom).size((int)(Main.percentWidth*37), (int)(Main.percentHeight*8));
		
		map.defaults().height((int)(Main.percentHeight*8));
		map.add(mapTable1);
		map.row();
		map.add(mapTable2);
		map.row();
		map.add(mapLabel).center();
		
		Table player1table = new Table(); 
		Table player2table = new Table();
		
		player1table.setBackground(Assets.SKIN.getPatch("single-border"));
		player2table.setBackground(Assets.SKIN.getPatch("single-border"));
		
		final TextButton player1nameButton = new TextButton("Player 1", Assets.SKIN);
		final TextButton player2nameButton = new TextButton("Player 2", Assets.SKIN);
		TextButton player1teamButton = new TextButton("God and Team", Assets.SKIN);
		player1teamButton.setClickListener(new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
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
		player2teamButton.setClickListener(new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
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
		
		player1table.defaults().height((int)(Main.percentHeight*8));
		player1table.add(player1nameButton).fillX().expandX();
		player1table.row();
		player1table.add(player1teamButton).fillX().expandX();
		
		player2table.defaults().height((int)(Main.percentHeight*8));
		player2table.add(player2nameButton).fillX().expandX();
		player2table.row();
		player2table.add(player2teamButton).fillX().expandX();
		        
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
		    public void click(Actor actor,float x,float y ){
				if (generatedMap) {
					Main.getInstance().games.createHotseatGame(null, player1god, player1team, player2god, player2team).showGame(true);
				}
				else if (randomServerdMap) {
					String[] maps = Assets.getMapList(false, true);
					Main.getInstance().games.createHotseatGame(Assets.loadMap(getRandom(maps)), player1god, player1team, player2god, player2team).showGame(true);
				}
				else if (randomCustomMap) {
					String[] maps = Assets.getMapList(true, true);
					Main.getInstance().games.createHotseatGame(Assets.loadMap(getRandom(maps)), player1god, player1team, player2god, player2team).showGame(true);
				}
				else if (specificMap) { //specific map
					String mapName = mapLabel.getText().toString();
					
					Main.getInstance().games.createHotseatGame(Assets.loadMap(mapName), player1god, player1team, player2god, player2team).showGame(true);
				}
		   }
		} );
		
		
		contents.padTop((int) (3*Main.percentHeight)).center();
		contents.defaults().space((int)Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(map);
		contents.row();
		contents.add(player1table);
		contents.row();
		contents.add(player2table);
		contents.row();
		contents.add(fog);
		contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom((int) (BUTTON_HEIGHT*0.3));
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
}
