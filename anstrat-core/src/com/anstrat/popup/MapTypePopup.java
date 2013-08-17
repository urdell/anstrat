package com.anstrat.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.network.protocol.GameOptions;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

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
	
	public static class ScrollChoiceList extends ScrollPane {
		private Table selectedTable;
		private List<SelectionChangeListener> listeners;
		
		public ScrollChoiceList(List<String> labels, List<Object> states, Object preselected) {
			super(new Table(Assets.SKIN).top());
		
			this.listeners = new ArrayList<SelectionChangeListener>();
			Table list = ((Table) this.getWidget());
			
			for(int i = 0; i < labels.size(); i++) {
				String name = labels.get(i);
				final Object state = states.get(i);
				
				final Table table = new Table();
				
				if (state == preselected) {
					selectedTable = table;
				}
				
				setBackground(table, state == preselected);
				table.defaults().align(Align.left).height(4f * Main.percentHeight);
				table.add(ComponentFactory.createLabel(name));
				list.add(table).fillX().expandX().height(10f * Main.percentHeight);
				list.row();
				
				table.addListener(new ClickListener(){
					@Override
					public void clicked (InputEvent event, float x, float y) {
						if (selectedTable != null) {
							setBackground(selectedTable, false);
						}
						
						setBackground(table, true);
						selectedTable = table;
						
						for (SelectionChangeListener listener : listeners) {
							listener.selectionChanged(state);
						}
					}
				});
				
				table.setTouchable(Touchable.enabled);
			}
			
			this.setScrollingDisabled(true, false);
			this.setFlickScroll(true);
		}
		
		private void setBackground(Table table, boolean isSelected) {
			if (isSelected) {
				table.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("double-border")));
			}
			else {
				table.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
			}	
		}
		
		public void clearSelection() {
			if (this.selectedTable != null) {
				this.setBackground(this.selectedTable, false);
			}

			this.selectedTable = null;
		}
		
		public void addSelectionListener(SelectionChangeListener listener) {
			this.listeners.add(listener);
		}
		
		public static interface SelectionChangeListener {
			public void selectionChanged(Object state);
		}
	}
	
	public static class TabPane extends Table {
		private List<Actor> tabs;
		private Table content = new Table();
		
		public TabPane(List<String> labels, List<Actor> tabs){
			this.tabs = tabs;
			
			for (int i = 0; i < labels.size(); i++) {
				addButton(labels.get(i), i);
			}
			
			this.row();
			this.add(content).fill().expand().colspan(labels.size());
			content.top();
			setActiveTab(0);
		}
		
		private void setActiveTab(int index){		
			Actor tab = this.tabs.get(index);
			this.content.clear();
			this.content.add(tab).fillX().expandX();
		}
		
		private void addButton(String text, final int index) {
			Button button = ComponentFactory.createButton(text);
			final TabPane pane = this;

			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					pane.setActiveTab(index);
				}
			});
			
			add(button).size(Main.percentWidth * 40, Main.percentHeight * 8f);
		}
	}
}

