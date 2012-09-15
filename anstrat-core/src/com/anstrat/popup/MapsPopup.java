package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.MapList;
import com.anstrat.guiComponent.Row;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Popup for showing maps.
 * @author Kalle
 */
public class MapsPopup extends Popup {
	
	private TextButton ok;
	private MapList maplist;
	public int randWidth, randHeight;
	private MapsPopupHandler handler;
	
	public MapsPopup(MapsPopupHandler handler, boolean withRandom, String title, String... maps) {
		super(title);
		this.handler = handler;
		
		ok = ComponentFactory.createButton("Ok", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
	        	returnSelection();
				Popup.getCurrentPopup().close();
	        }
	    });
		Assets.SKIN.setEnabled(ok, false);
		
		maplist = new MapList(ok);
		maplist.setMaps(withRandom, maps);
		
		TextButton cancel = ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		add(maplist).maxHeight((int)(50*Main.percentHeight));
		add(new Row(ok, cancel));
	}
	
	/**
	 * Check if anything is selected, if so enable ok button.
	 */
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		//if(ok.touchable==false && maplist.getSelected()!=null) Assets.SKIN.setEnabled(ok, true);
		super.draw(batch, parentAlpha);
	}

	/**
	 * Return selected entry directly to popup handler.
	 */
	public void returnSelection(){
		if(maplist.getSelected().equalsIgnoreCase("RANDOM")){
			if(maplist.invalidRandSize==true)
				return;
			try{
				randWidth  = maplist.randWidth;
				randHeight = maplist.randHeight;
			} catch(NumberFormatException e){
				randWidth  = 10;
				randHeight = 10;
			}
		}
		
		handler.mapSelected(maplist.getSelected());
	}
	
	public static interface MapsPopupHandler {
		public void mapSelected(String map);
	}
}
