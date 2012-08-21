package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.FriendList;
import com.anstrat.guiComponent.Row;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;

public class InvitePopup extends Popup {
	private TextButton ok;
	private TextField textField;
	private FriendList friendlist;
	private InvitePopupHandler handler;
	
	public InvitePopup(InvitePopupHandler handler, String title) {
		super(title);
		this.handler = handler;
		
		ok = ComponentFactory.createButton("Ok", new ClickListener() {
	        @Override
	        public void click(Actor actor,float x,float y ){
	        	returnSelection();
				Popup.currentPopup.close();
	        }
	    });
		Assets.SKIN.setEnabled(ok, false);
		textField = ComponentFactory.createTextField("", false);
		textField.setTextFieldListener(new TextFieldListener() {

			@Override
			public void keyTyped(TextField textField, char key) {
				if(textField.getText().equals("")) {
					Assets.SKIN.setEnabled(ok, false);
				}
				else {
					Assets.SKIN.setEnabled(ok, true);
				}
				
			}
			
		});
		friendlist = new FriendList(textField, ok);
		friendlist.setFriends(Main.getInstance().friends.getFriends());
		
		TextButton cancel = ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		add(friendlist).maxHeight((int)(50*Main.percentHeight));
		row();
		add(textField).expandX().fillX();
		row();
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
		if (textField.getText() != null && !textField.getText().toString().equals(""))
			handler.friendSelected(textField.getText());
	}
	
	public static interface InvitePopupHandler {
		public void friendSelected(String friend);
	}
}
