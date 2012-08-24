package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class MapSelecter extends Table {
	
	public static final int NONE = 0;
	public static final int SPECIFIC_MAP = 1;
	public static final int GENERATED_MAP = 2;
	public static final int RANDOM_MAP = 3;
	
	private int mapSelection = NONE;
	private Label mapLabel;
	
	public MapSelecter(){
		
		mapLabel = new Label(Assets.SKIN);
		mapLabel.setText("No map chosen");
		
		Button mapSpecific = ComponentFactory.createButton("Specific", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				Popup popup = new MapsPopup(new MapsPopupHandler() {
						@Override
						public void mapSelected(String map){
							mapSelection = SPECIFIC_MAP;
							mapLabel.setText(map);
						}
					}, false, "Choose specific map", Assets.getMapList(true, true));
        		
        		popup.show();	
			}
		});
		
		Button mapRandom = ComponentFactory.createButton("Random", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				mapSelection = RANDOM_MAP;
				mapLabel.setText("Random custom map");
			}	
		});
		
		Button mapGenerated = ComponentFactory.createButton("Generated", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				mapSelection = GENERATED_MAP;
				mapLabel.setText("Generated map");
			}
		});
		
		row().height(MenuScreen.BUTTON_HEIGHT).fillX().expandX();
		add(mapSpecific);
		add(mapGenerated);
		add(mapRandom);
		row();
		add(mapLabel).fillX().expandX();
		
	}
	
	public int getMapSelection(){
		return mapSelection;
	}
	
	public String getMapName(){
		return mapLabel.getText().toString();
	}
}
