package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class GeneratedMapPopup extends Popup {
	
	public static final String RANDOM = "Random";
	public static final String LARGE  = "Large";
	public static final String MEDIUM = "Medium";
	public static final String SMALL  = "Small";
	
	public Table selected;
	
	public GeneratedMapPopup(final GeneratedMapPopupHandler handler){
		super("Choose size");
		
		Table list = new Table(Assets.SKIN);
		list.top();
		
		ClickListener sizeClick = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setSelected((Table)event.getListenerActor());
			}
		};
		
		String[] strs = {RANDOM,LARGE,MEDIUM,SMALL};
		for(String str : strs){
			Table table = new Table();
			table.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
			table.defaults().align(Align.left).height(4f*Main.percentHeight);
			table.add(new Label(str,Assets.SKIN));
			table.setName(str);
			list.add(table).fillX().expandX().height(10f*Main.percentHeight);
			list.row();
			
			table.addListener(sizeClick);
			table.setTouchable(Touchable.enabled);
			if(str.equalsIgnoreCase(RANDOM))
				setSelected(table);
		}
		
		ScrollPane scroll = new ScrollPane(list);
		scroll.setScrollingDisabled(true, false);
		scroll.setFlickScroll(true);
		this.add(scroll).fill().expand();
		
		this.add(new Row(
				ComponentFactory.createButton("Ok", new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
							handler.sizeSelected(selected.getName());	//selected.name
							Popup.getCurrentPopup().close();
						}
					}),
				ComponentFactory.createButton("Cancel",Popup.POPUP_CLOSE_BUTTON_HANDLER)));
	}
	
	/**
	 * Sets specified table as selected.
	 */
	public void setSelected(Table t){
		if(selected != null){
			selected.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		}
		
		t.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("double-border")));
		selected = t;
	}
	
	public static interface GeneratedMapPopupHandler {
		public void sizeSelected(String size);
	}
}

