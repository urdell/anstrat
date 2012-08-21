package com.anstrat.guiComponent;

import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class FriendList extends Table {
	
	private TextField textField;
	private Button okButton;
	private Table list;
	
	
	
	public FriendList(TextField textField, Button button){
		super(Assets.SKIN);
		this.textField = textField;
		this.okButton = button;
		this.setBackground(Assets.SKIN.getPatch("single-border"));
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
				button.setClickListener(new ClickListener() {

					@Override
					public void click(Actor actor, float x, float y) {
						textField.setText(button.getText().toString());
						Assets.SKIN.setEnabled(okButton, true);
					}
					
				});
				list.row();
				list.add(button).fillX().expandX().height((int)(10*Main.percentHeight));
			}
		}
		
		FlickScrollPane scroll = new FlickScrollPane(list);
		scroll.setScrollingDisabled(true, false);
		this.add(scroll).fill().expand();
	}
	
}
