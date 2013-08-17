package com.anstrat.guiComponent;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class ScrollChoiceList extends ScrollPane {
	private Table selectedTable;
	private Object selected;
	private List<SelectionChangeListener> listeners;
	
	public ScrollChoiceList(List<String> labels, List<?> states, Object preselected) {
		super(new Table(Assets.SKIN).top());
	
		this.selected = preselected;
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
					selected = state;
					
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
		this.selected = null;
	}
	
	public Object getSelected() {
		return this.selected;
	}
	
	public void addSelectionListener(SelectionChangeListener listener) {
		this.listeners.add(listener);
	}
	
	public static interface SelectionChangeListener {
		public void selectionChanged(Object state);
	}
}