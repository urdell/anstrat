package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions.MapType;
import com.anstrat.popup.MapTypePopup;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MapSelecter extends Table {
	
	private TextButton button;
	private Popup popup;
	
	public MapSelecter(final MapTypePopup.MapSelectionListener listener){
		super(Assets.SKIN);
		
		this.button = ComponentFactory.createMenuButton("Select a map", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				popup.show();
			}
		});
		
		this.popup = new MapTypePopup(true, true, new MapTypePopup.MapSelectionListener() {
			@Override
			public void mapSelected(MapType type, String mapName) {
				button.setText(type == MapType.SPECIFIC ? mapName : "Generated: " + type.toString());
				listener.mapSelected(type, mapName);
			}
		});
		
		
		this.defaults().spaceBottom(5);
		add(ComponentFactory.createLabel("Map"));
		row();
		add(button).size(Main.percentWidth * 60, Main.percentHeight*8f).center();
	}
}
