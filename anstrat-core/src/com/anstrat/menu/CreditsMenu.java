package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class CreditsMenu extends MenuScreen {
	Table creditsTable;
	
	public CreditsMenu() {
		super();
		
		Table creditsTable = new Table();
		creditsTable.add(new Label(Gdx.files.internal("data/credits.txt").readString(), new LabelStyle(Assets.UI_FONT,Color.BLACK)));
		
		contents.defaults().top().center();
		contents.add(new Label("Credits", new LabelStyle(Assets.MENU_FONT,Color.BLACK)));
		contents.row();
		contents.add(creditsTable).fill().expand();
	}

	private static CreditsMenu me;
	
	public static synchronized CreditsMenu getInstance() {
		if(me == null){
			me = new CreditsMenu();
		}
		return me;
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		//TODO Scroll credits
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
