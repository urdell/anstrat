package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
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

public class FindMatchMenu extends MenuScreen {
	private static FindMatchMenu me;
	
	
	public static int god = TeamPopup.GOD_ODIN, team = TeamPopup.TEAM_VV;
	
	private boolean specificMap = false;
	private boolean randomMap = false;
	private boolean generatedMap = false;
	
	private FindMatchMenu(){
        
		Table settings = new Table(Assets.SKIN);
		settings.setBackground(Assets.SKIN.getPatch("single-border"));
		
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
							randomMap = false;
							mapLabel.setText(map);
						}
					}, false, "Choose specific map", Assets.getMapList(false, true));
        		
        		popup.show();
				
			}
			
		});
		Button mapServerRandom = ComponentFactory.createButton("Random", new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				specificMap = false;
				generatedMap = false;
				randomMap = true;
				mapLabel.setText("Random map");
				
			}
			
		});
		Button mapGenerate = ComponentFactory.createButton("Generated", new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				specificMap = false;
				generatedMap = true;
				randomMap = false;
				mapLabel.setText("Generated map");
				
			}
			
		});
		
		Button god = ComponentFactory.createButton("God and team", new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				Popup popup = new TeamPopup(FindMatchMenu.god, FindMatchMenu.team, "Select your team and god", new TeamPopupListener() {

					@Override
					public void onChosen(int godChosen, int teamChosen) {
						InviteMatchMenu.god = godChosen;
						InviteMatchMenu.team = teamChosen;
					}
					
				});
				
				popup.show();
			}
			
		});
		
		Table mapTable = new Table();
		mapTable.add(mapSpec).size((int)(Main.percentWidth*25), (int)(Main.percentHeight*10));
		mapTable.add(mapServerRandom).size((int)(Main.percentWidth*25), (int)(Main.percentHeight*10));
		mapTable.add(mapGenerate).size((int)(Main.percentWidth*25), (int)(Main.percentHeight*10));
		
		settings.defaults().height((int)(Main.percentHeight*10));
		settings.add("Find Match");
		settings.row();
		settings.add(mapTable);
		settings.row();
		settings.add(mapLabel).fillX().expandX();
		settings.row();
		settings.add(fog).fillX().expandX();
		settings.row();
		settings.add(god).fillX().expandX();
		        
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
		    public void click(Actor actor,float x,float y ){
				
				if (generatedMap) {
					Main.getInstance().games.createHotseatGame(null, HotseatMenu.player1god, HotseatMenu.player1team, HotseatMenu.player2god, HotseatMenu.player2team).showGame(true);
				}
				else if (randomMap) {
					String[] maps = Assets.getMapList(false, true);
					Main.getInstance().games.createHotseatGame(Assets.loadMap(HotseatMenu.getRandom(maps)), HotseatMenu.player1god, HotseatMenu.player1team, HotseatMenu.player2god, HotseatMenu.player2team).showGame(true);
				}
				else if (specificMap){ //specific map
					String mapName = mapLabel.getText().toString();
					
					Main.getInstance().games.createHotseatGame(Assets.loadMap(mapName), HotseatMenu.player1god, HotseatMenu.player1team, HotseatMenu.player2god, HotseatMenu.player2team).showGame(true);
				}
		   }
		} );
		
		
		contents.padTop((int) (3*Main.percentHeight)).center();
		contents.defaults().space((int)Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(settings);
		contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom((int) (BUTTON_HEIGHT*1.3));
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(ComponentFactory.createLoginLabel());
		contents.add(centerLogin);
	}
	
	public static synchronized FindMatchMenu getInstance() {
		if(me == null){
			me = new FindMatchMenu();
		}
		return me;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
