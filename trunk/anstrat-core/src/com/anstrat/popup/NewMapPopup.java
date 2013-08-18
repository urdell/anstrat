package com.anstrat.popup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.guiComponent.ScrollChoiceList;
import com.anstrat.guiComponent.ScrollChoiceList.SelectionChangeListener;
import com.anstrat.mapEditor.MapEditor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class NewMapPopup extends Popup {

	private MapSizePopup widthPopup, heightPopup;
	private TextButton widthButton, heightButton;
	
	public NewMapPopup(){
		super("Create new map");
	
		this.widthPopup = new MapSizePopup("Width", new SelectionChangeListener() {			
			@Override
			public void selectionChanged(Object state) {
				widthButton.setText(state.toString());
			}
		});
		
		this.heightPopup = new MapSizePopup("Height", new SelectionChangeListener() {
			@Override
			public void selectionChanged(Object state) {
				heightButton.setText(state.toString());
			}
		});
		
		widthButton = ComponentFactory.createMenuButton(String.valueOf(this.widthPopup.getSize()));
		heightButton = ComponentFactory.createMenuButton(String.valueOf(this.heightPopup.getSize()));

		widthButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				widthPopup.show();
			}
		});

		heightButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				heightPopup.show();
			}
		});
		
		Actor label = ComponentFactory.createLabel("X");
		label.setWidth(Main.percentWidth * 20f);

		add(new Row(widthButton,
					label,
					heightButton));
		
		add(new Row(
			ComponentFactory.createButton("Ok", new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
	    			MapEditor.getInstance().actionHandler.createNewMap(widthPopup.getSize(),
	    															   heightPopup.getSize());
					Popup.getCurrentPopup().close();
				}
			}),
			ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER)));
	}
	
	private static class MapSizePopup extends Popup {
		
		private static final List<Integer> SIZES = Arrays.asList(new Integer[]{ 5, 10, 15, 20 });
		private static final int DEFAULT_SIZE = 10;
		private int size;
		
		public MapSizePopup(String caption, SelectionChangeListener listener){
			super(caption);
			
			List<String> labels = new ArrayList<String>();
			
			for (Integer size : SIZES) {
				labels.add(size.toString());
			}

			final ScrollChoiceList list = new ScrollChoiceList(labels, SIZES, DEFAULT_SIZE);
			list.addSelectionListener(listener);
			this.size = (Integer) list.getSelected();
			
			this.add(list).height(Main.percentHeight * 30f);
			this.row();
			this.add(ComponentFactory.createButton("Ok", new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					size = (Integer) list.getSelected();
					Popup.getCurrentPopup().close();
				}
			}));
		}
		
		public int getSize(){
			return size; 
		}
		
		@Override
		public float getPrefWidth(){
			return Main.percentWidth * 20f;
		}
	}
}
