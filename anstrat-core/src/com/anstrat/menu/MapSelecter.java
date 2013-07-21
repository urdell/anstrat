package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.popup.GeneratedMapPopup;
import com.anstrat.popup.GeneratedMapPopup.GeneratedMapPopupHandler;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class MapSelecter extends Table implements GeneratedMapPopupHandler {
	
	private GeneratedMapPopup generatedMapSizePopup;
	private Label mapTitle, mapLabel;
	private MapSelectionHandler handler;
	
	public MapSelecter(final MapSelectionHandler handler){
		super(Assets.SKIN);
		setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		
		this.handler = handler;
		generatedMapSizePopup = new GeneratedMapPopup(this);		
		mapTitle = new Label("Map options", Assets.SKIN);
		mapLabel = new Label("No map chosen", Assets.SKIN);
		
		Button mapSpec = ComponentFactory.createButton("Select map", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new MapsPopup(new MapsPopupHandler() {
						@Override
						public void mapSelected(String map){
							mapLabel.setText(map);
							handler.mapSelected(GameOptions.MapType.SPECIFIC, map);
						}
					}, false, "Choose specific map", Assets.getMapList(true, true));
        		
        		popup.show();
			}
		});
		
		Button mapGenerate = ComponentFactory.createButton("Generate map", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				generatedMapSizePopup.show();
			}
		});	

		Table table = new Table(Assets.SKIN);
		table.add(mapSpec).size(Main.percentWidth*40, Main.percentHeight*8f);
		table.add(mapGenerate).size(Main.percentWidth*40, Main.percentHeight*8f);

		defaults().height(Main.percentHeight*8f);
		add(mapTitle).center();
		row();
		add(table);
		row();
		add(mapLabel).center();
	}

	@Override
	public void sizeSelected(GameOptions.MapType type) {
		mapLabel.setText("Generated map (" + type + ")");
		handler.mapSelected(type, null);
	}
	
	public static interface MapSelectionHandler {
		public void mapSelected(GameOptions.MapType type, String mapName);
	}
}
