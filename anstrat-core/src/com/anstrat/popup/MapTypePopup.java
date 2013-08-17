package com.anstrat.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	public MapTypePopup(boolean showCustom, boolean showGenerated){
		super("Map");
	
		final List<ScrollChoiceList> lists = new ArrayList<ScrollChoiceList>();
		
		if (showCustom) {
			lists.add(generatedMapsScrollList());
		}
		
		if (showGenerated) {
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
				}
			});
		}

		if (lists.size() > 1) {
			TabPane pane = new TabPane(
					Arrays.asList(new String[]{ "Custom", "Generated" }),
					Arrays.asList(lists.toArray(new Actor[lists.size()])));
			
			this.add(pane).maxHeight(Main.percentHeight * 70f).fillY().expandY();
		}
		else {
			// Don't show as tab pane
			this.add(lists.get(0)).maxHeight(Main.percentHeight * 70f).fillY().expandY();
		}
		
		this.add(new Row(
				ComponentFactory.createButton("Ok", new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
							Popup.getCurrentPopup().close();
						}
					}),
				ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER)));
	}
	
	private static ScrollChoiceList generatedMapsScrollList() {
		ArrayList<String> labels = new ArrayList<String>();
		ArrayList<Object> states = new ArrayList<Object>();
		
		for(GameOptions.MapType t : GameOptions.MapType.values()){
			if(t != GameOptions.MapType.SPECIFIC){
				labels.add(t.toString());
				states.add(t);
			}
		}
		
		return new ScrollChoiceList(labels, states, GameOptions.MapType.GENERATED_SIZE_MEDIUM);
	}
	
	private static ScrollChoiceList customMapsScrollList(){
		List<String> labels = Arrays.asList(new String[]{"ABC", "DEF", "BLEH", "NEEE", "AAAA", "ACACACA", "ASDASD"});
		return new ScrollChoiceList(labels, Arrays.asList(labels.toArray()), null);
	}
	
	public static interface MapSelectionListener {
		public void mapSelection(GameOptions.MapType type, String mapName);
	}
}

