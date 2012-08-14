package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class InviteMatchMenu extends MenuScreen {
	private static InviteMatchMenu me;
	
	private static final String generatedMap = "Generated map";
	private static final String randomServerMap = "Random server map";
	private static final String randomCustomMap = "Random custom map";
	
	private boolean specificMap = false;
	
	private InviteMatchMenu(){
        
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
							mapLabel.setText(map);
						}
					}, false, "Choose specific map", Assets.getMapList(true, true));
        		
        		popup.show();
				
			}
			
		});
		Button mapCustomRandom = ComponentFactory.createButton(randomCustomMap, new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				
				specificMap = false;
				mapLabel.setText("Random custom map");
			}
			
		});
		Button mapServerRandom = ComponentFactory.createButton(randomServerMap, new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				specificMap = false;
				mapLabel.setText("Random server map");
				
			}
			
		});
		Button mapGenerate = ComponentFactory.createButton(generatedMap, new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				specificMap = false;
				mapLabel.setText("Generated map");
				
			}
			
		});
		
		Button god = ComponentFactory.createButton("God", new ClickListener() {

			@Override
			public void click(Actor actor, float x, float y) {
				// TODO select god
				
			}
			
		});
		
		final TextField friendName = ComponentFactory.createTextField("Friend's name", false); 
		
		settings.defaults().height((int)(Main.percentHeight*10));
		settings.add("Find Match");
		settings.row();
		settings.row();
		settings.add("Choose map:");
		settings.row();
		settings.add(mapSpec).size((int)(Main.percentWidth*37), (int)(Main.percentHeight*10));
		settings.add(mapServerRandom).size((int)(Main.percentWidth*37), (int)(Main.percentHeight*10));
		settings.row();
		settings.add(mapGenerate).size((int)(Main.percentWidth*37), (int)(Main.percentHeight*10));
		settings.add(mapCustomRandom).size((int)(Main.percentWidth*37), (int)(Main.percentHeight*10));
		settings.row();
		settings.add(mapLabel).fillX().expandX();
		settings.row();
		settings.add(fog).fillX().expandX();
		settings.row();
		settings.add(god).fillX().expandX();
		        
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
		    public void click(Actor actor,float x,float y ){
				
				String friend = friendName.getMessageText();
				//TODO is friend existing? If so, go ahead, else inform user that friend is not existing
				
				if (specificMap == false) {
					if (mapLabel.getText().toString().equals(generatedMap)) {

		    			//TODO Main.getInstance().network.hostGameRandom(10, 10, 604800000l, name.getText(), password.getText());
					}
					else if (mapLabel.getText().toString().equals(randomServerMap)) {
						//TODO host game
					}
					else if (mapLabel.getText().toString().equals(randomCustomMap)) {
						//TODO host game
					}
				}
				else { //specific map
					String mapName = mapLabel.getText().toString();
					//TODO host game specific map
					
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
	
	public static synchronized InviteMatchMenu getInstance() {
		if(me == null){
			me = new InviteMatchMenu();
		}
		return me;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
