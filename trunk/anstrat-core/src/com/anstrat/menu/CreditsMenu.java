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
		contents.register("top", new Label("Credits", new LabelStyle(Assets.MENU_FONT,Color.BLACK)));
		creditsTable = new Table();
		contents.register("credits", creditsTable);
		contents.parse("align:top,center " +
				"[top]" +
				"---" +
				"[credits] fill:x expand:x fill:y expand:y");
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
	public void show() {
		creditsTable.clear();
		creditsTable.add(new Label(Gdx.files.internal("data/credits.txt").readString(),new LabelStyle(Assets.UI_FONT,Color.BLACK)));
		creditsTable.layout();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
