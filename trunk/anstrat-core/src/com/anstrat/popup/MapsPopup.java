package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.MapList;
import com.anstrat.guiComponent.Row;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Popup for showing maps.
 * @author Kalle
 */
public class MapsPopup extends Popup {
	
	private TextButton ok;
	private MapList maplist;
	public int randWidth, randHeight;
	
	public MapsPopup(PopupHandler handler, boolean withRandom, String title, String... maps) {
		super(handler, title);
		
		ok = new TextButton("Ok",Assets.SKIN);
		Assets.SKIN.setEnabled(ok, false);
		
		maplist = new MapList(ok);
		maplist.setMaps(withRandom, maps);
		
		TextButton cancel = new TextButton("Cancel",Assets.SKIN);
		
		add(maplist).maxHeight((int)(50*Main.percentHeight));
		add(new Row(ok, cancel));
		ok.setClickListener(new ClickListener() {
	        @Override
	        public void click(Actor actor,float x,float y ){
	        	returnSelection();
	        }
	    });
		cancel.setClickListener(cl);
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
		handler.handlePopupAction(maplist.getSelected());
	}
}
