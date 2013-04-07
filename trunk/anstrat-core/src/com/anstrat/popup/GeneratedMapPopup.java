package com.anstrat.popup;

import java.util.HashMap;
import java.util.Map;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.network.protocol.GameOptions;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class GeneratedMapPopup extends Popup {
	private Table selected;
	private Map<Table, GameOptions.MapType> buttons = new HashMap<Table, GameOptions.MapType>();
	
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
		
		for(GameOptions.MapType t : GameOptions.MapType.values()){
			if(t != GameOptions.MapType.SPECIFIC){
				Table table = new Table();
				table.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
				table.defaults().align(Align.left).height(4f*Main.percentHeight);
				table.add(new Label(t.toString(), Assets.SKIN));
				table.setName(t.toString());
				list.add(table).fillX().expandX().height(10f*Main.percentHeight);
				list.row();
				
				table.addListener(sizeClick);
				table.setTouchable(Touchable.enabled);
				
				buttons.put(table, t);
				
				// Mark random size as preselected
				if(t == GameOptions.MapType.GENERATED_SIZE_RANDOM){
					setSelected(table);
				}
			}
		}
		
		ScrollPane scroll = new ScrollPane(list);
		scroll.setScrollingDisabled(true, false);
		scroll.setFlickScroll(true);
		this.add(scroll).fill().expand();
		
		this.add(new Row(
				ComponentFactory.createButton("Ok", new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
							handler.sizeSelected(buttons.get(selected));
							Popup.getCurrentPopup().close();
						}
					}),
				ComponentFactory.createButton("Cancel",Popup.POPUP_CLOSE_BUTTON_HANDLER)));
	}
	
	private void setSelected(Table t){
		if(selected != null){
			selected.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		}
		
		t.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("double-border")));
		selected = t;
	}
	
	public static interface GeneratedMapPopupHandler {
		public void sizeSelected(GameOptions.MapType type);
	}
}

