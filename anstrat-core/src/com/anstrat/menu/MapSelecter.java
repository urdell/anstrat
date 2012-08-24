package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class MapSelecter extends Table {
	
	private int mapSelection;
	private Label mapLabel;
	
	public MapSelecter(final boolean includeDefaultMaps, final boolean includePlayerMaps){
		
		mapLabel = new Label(Assets.SKIN);
		mapLabel.setText("Random map");
		mapSelection = GameOptions.MAP_RANDOM;
		
		Button mapSpecific = ComponentFactory.createButton("Specific", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				Popup popup = new MapsPopup(new MapsPopupHandler() {
						@Override
						public void mapSelected(String map){
							mapSelection = GameOptions.MAP_SPECIFIC;
							mapLabel.setText(map);
						}
					}, false, "Choose specific map", Assets.getMapList(includePlayerMaps, includeDefaultMaps));
        		
        		popup.show();	
			}
		});
		
		Button mapRandom = ComponentFactory.createButton("Random", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				mapSelection = GameOptions.MAP_RANDOM;
				mapLabel.setText("Random map");
			}	
		});
		
		Button mapGenerated = ComponentFactory.createButton("Generated", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				mapSelection = GameOptions.MAP_GENERATED;
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
