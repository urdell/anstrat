package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Invite;
import com.anstrat.core.Main;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class InviteRow extends Table{

	
	public InviteRow(){
		this.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("mana"))));
	}
	public InviteRow(final Invite invite){
		Label topText = new Label("From:", Assets.SKIN);
		Label bottomText = new Label(invite.otherPlayerName, Assets.SKIN);
		Table textTable = new Table();
		
		textTable.add(topText).left();
		textTable.row();
		textTable.add(bottomText).left();
		
		add(textTable).expandX();
		
		this.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("sword"))));
		addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	Main.getInstance().setScreen(new ViewInviteMenu(invite));
	        }
		});
	}
	
	
}
