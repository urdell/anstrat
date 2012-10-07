package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Invite;
import com.anstrat.core.Main;
import com.anstrat.network.protocol.GameOptions;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
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
		TextureRegion mapTypeRegion = Assets.getTextureRegion("cancel");
		switch(invite.gameOptions.mapChoice){
		case GameOptions.MAP_CUSTOM:
			mapTypeRegion = Assets.getTextureRegion("kamikaze-button");
			break;
		case GameOptions.MAP_GENERATED:
			mapTypeRegion = Assets.getTextureRegion("terrain-button");
			break;
		case GameOptions.MAP_RANDOM:
			mapTypeRegion = Assets.getTextureRegion("help-button");
			break;
		case GameOptions.MAP_SPECIFIC:
			mapTypeRegion = Assets.getTextureRegion("open-button");
			break;
		}
		Image mapTypeImage = new Image(mapTypeRegion);
		
		textTable.add(topText).align(Align.left).maxHeight(25);
		textTable.row();
		textTable.add(bottomText).align(Align.left).maxHeight(25);
		
		add(textTable).align(Align.left).fillX().expandX();
		add(mapTypeImage).align(Align.right).maxSize(50);
		
		this.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("sword"))));
		addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	Main.getInstance().setScreen(new ViewInviteMenu(invite));
	        }
		});
		this.setTouchable(Touchable.enabled);
	}
	
	
}
