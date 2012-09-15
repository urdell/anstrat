package com.anstrat.guiComponent;

import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class FriendList extends Table {
	
	private TextField textField;
	private Button okButton;
	private Table list;
	
	
	
	public FriendList(TextField textField, Button button){
		super(Assets.SKIN);
		this.textField = textField;
		this.okButton = button;
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
	}
	
	/**
	 * Sets the maps of the list.
	 * @param withRandom Include random map?
	 * @param filenames F
	 */
	public void setFriends(List<String> friends){
		list = new Table(Assets.SKIN);
		list.top();
		
		//MAPS
		for(String friend : friends){
			//Table map = formatMap(mapPath);
			final TextButton button = new TextButton(friend, Assets.SKIN);
			if(friend != null){
				button.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent event, float x, float y) {
						textField.setText(button.getText().toString());
						Assets.SKIN.setEnabled(okButton, true);
					}
					
				});
				list.row();
				list.add(button).fillX().expandX().height(10f*Main.percentHeight);
			}
		}
		
		ScrollPane scroll = new ScrollPane(list);
		scroll.setScrollingDisabled(true, false);
		scroll.setFlickScroll(true);
		this.add(scroll).fill().expand();
	}
	
}
