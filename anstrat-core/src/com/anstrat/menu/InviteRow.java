package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Invite;
import com.anstrat.core.Main;
import com.anstrat.network.protocol.GameOptions;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
		
		float rowWidth = Gdx.graphics.getWidth();
		
		Label topText = new Label("Invite from:", Assets.SKIN);
		topText.setColor(Color.BLACK);
		Label bottomText = new Label(invite.otherPlayerName, Assets.SKIN);
		bottomText.setColor(Color.BLACK);
		Table textTable = new Table();
		TextureRegion mapTypeRegion = Assets.getTextureRegion("cancel");
		
		if(invite.gameOptions.mapType == GameOptions.MapType.SPECIFIC){
			mapTypeRegion = Assets.getTextureRegion("mapIconSpecific");
		}
		else {
			mapTypeRegion = Assets.getTextureRegion("mapIconGenerated");
		}
		
		Image mapTypeImage = new Image(mapTypeRegion);
		
		TextureRegion fogRegion;
		if(invite.gameOptions.fog){
			fogRegion = Assets.getTextureRegion("eye");
		}
		else{
			fogRegion = Assets.getTextureRegion("eyeOff");
		}
		Image fogImage = new Image(fogRegion);
		
		textTable.add(topText).align(Align.left).maxHeight(rowWidth/14);
		textTable.row();
		textTable.add(bottomText).align(Align.left).maxHeight(rowWidth/14);
		
		add(textTable).align(Align.left).fillX().expandX();
		add(fogImage).align(Align.right).maxSize(rowWidth/7);
		add(mapTypeImage).align(Align.right).maxSize(rowWidth/7);
		
		NinePatch ninePatch = new NinePatch(Assets.getTextureRegion("goldBorder"), 5, 5, 5, 5);
		ninePatch.setMiddleHeight(10);
		this.setBackground(new NinePatchDrawable(ninePatch));
		addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	Main.getInstance().setScreen(new ViewInviteMenu(invite));
	        }
		});
		this.setTouchable(Touchable.enabled);
	}
	
	
}
