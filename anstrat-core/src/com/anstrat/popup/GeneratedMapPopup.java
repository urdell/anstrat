package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

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
			public void click(Actor actor, float x, float y) {
				setSelected((Table)actor);
			}
		};
		
		String[] strs = {"Random","Large","Medium","Small"};
		for(String str : strs){
			Table table = new Table(str);
			table.setBackground(Assets.SKIN.getPatch("single-border"));
			table.defaults().left().height((int)(4*Main.percentHeight));
			table.add(new Label(str,Assets.SKIN));
			list.add(table).fillX().expandX().height((int)(10*Main.percentHeight));
			list.row();
			
			table.setClickListener(sizeClick);
			if(str.equalsIgnoreCase(RANDOM))
				setSelected(table);
		}
		
		FlickScrollPane scroll = new FlickScrollPane(list);
		scroll.setScrollingDisabled(true, false);
		this.add(scroll).fill().expand();
		
		this.add(new Row(
				ComponentFactory.createButton("Ok",new ClickListener() {
						@Override
						public void click(Actor actor, float x, float y) {
							handler.sizeSelected(selected.name);
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
			selected.setBackground(Assets.SKIN.getPatch("single-border"));
		}
		
		t.setBackground(Assets.SKIN.getPatch("double-border"));
		selected = t;
	}
	
	public static interface GeneratedMapPopupHandler {
		public void sizeSelected(String size);
	}
}
