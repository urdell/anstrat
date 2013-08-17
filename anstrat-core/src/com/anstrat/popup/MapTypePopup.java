package com.anstrat.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.guiComponent.ScrollChoiceList;
import com.anstrat.guiComponent.TabPane;
import com.anstrat.network.protocol.GameOptions;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Popup for choosing a {@link GameOptions.MapType}.
 */
public class MapTypePopup extends Popup {
	
	private Object selected;
	private MapSelectionListener listener;
	
	public MapTypePopup(boolean showCustom, boolean showGenerated, MapSelectionListener listener){
		super("Choose map");
		
		this.listener = listener;
		final List<ScrollChoiceList> lists = new ArrayList<ScrollChoiceList>();
		
		if (showGenerated) {
			lists.add(generatedMapsScrollList());
		}
		
		if (showCustom) {
			lists.add(customMapsScrollList());
		}
			
		for (final ScrollChoiceList list : lists) {
			list.addSelectionListener(new ScrollChoiceList.SelectionChangeListener() {	
				@Override
				public void selectionChanged(Object state) {
					for (ScrollChoiceList other : lists) {
						if (other != list) {
							other.clearSelection();
						}
					}
					
					selected = state;
				}
			});
		}

		if (lists.size() > 1) {
			TabPane pane = new TabPane(
					Arrays.asList(new String[]{ "Generated", "Custom" }),
					Arrays.asList(lists.toArray(new Actor[lists.size()])));
			
			this.add(pane).height(Main.percentWidth * 70f).fillY().expandY();
		}
		else {
			// Don't show as tab pane
			this.add(lists.get(0)).maxHeight(Main.percentHeight * 70f).fillY().expandY();
		}
		
		this.add(new Row(
				ComponentFactory.createButton("Ok", new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						notifyListeners(selected);
						Popup.getCurrentPopup().close();
					}
				}),
				ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER)));
		
		// Select any preselected value
		for(ScrollChoiceList list : lists) {
			if (list.getSelected() != null) {
				selected = list.getSelected();
				notifyListeners(selected);
				break;
			}
		}
	}
	
	private void notifyListeners(Object state){
		if (state instanceof String) {
			String mapName = (String) state;
			listener.mapSelected(GameOptions.MapType.SPECIFIC, mapName);
		}
		else if (state instanceof GameOptions.MapType) {
			GameOptions.MapType type = (GameOptions.MapType) state;
			listener.mapSelected(type, null);
		}	
	}
	
	private static ScrollChoiceList generatedMapsScrollList() {
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<GameOptions.MapType> states = new ArrayList<GameOptions.MapType>();
		
		for(GameOptions.MapType t : GameOptions.MapType.values()){
			if(t != GameOptions.MapType.SPECIFIC){
				labels.add(t.toString());
				states.add(t);
			}
		}
		
		return new ScrollChoiceList(labels, states, GameOptions.MapType.GENERATED_SIZE_MEDIUM);
	}
	
	private static ScrollChoiceList customMapsScrollList(){
		List<String> filenames = Arrays.asList(Assets.getMapList(true, true));
		return new ScrollChoiceList(filenames, filenames, null);
	}
	
	public static interface MapSelectionListener {
		public void mapSelected(GameOptions.MapType type, String mapName);
	}
}

