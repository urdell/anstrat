package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HotseatNextTurnScreen implements Screen {
	private static HotseatNextTurnScreen me = null;
	private TextureRegion splashscreen;
	private SpriteBatch batch;
	
	private Stage stage;
	private Table background;
	private TextureAtlas atlas;
	
	private HotseatNextTurnScreen(){
		//atlas = new TextureAtlas("textures-loadingscreen/pack.atlas");
		//splashscreen = atlas.findRegion("splashscreen");
		splashscreen = Assets.getTextureRegion("white-line-hard");

		this.batch = Main.getInstance().batch;
		this.stage = Main.getInstance().overlayStage;
		
		background = new Table();
		background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		background.setTouchable(Touchable.enabled);
		background.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	for(GUnit gUnit : GEngine.getInstance().gUnits.values()){
    				gUnit.updateHealthbar();
    			}
    			GEngine.getInstance().updateUI();
    			GEngine.getInstance().userInterface.updateCurrentPlayer();
            	GameInstance.activeGame.showGame(false);
            }
        });
	}
	
	public static HotseatNextTurnScreen getInstance(){
		if(me==null)
			me = new HotseatNextTurnScreen();
		return me;
	}
	
	@Override
	public void render(float delta) {
		float prevX = Assets.MENU_FONT.getScaleX();
		float prevY = Assets.MENU_FONT.getScaleY();
		batch.begin();
		Color temp = batch.getColor();
		batch.enableBlending();
		batch.setColor(0f,0f,0f,1f);
		batch.draw(splashscreen,0f,0f,0f,0f,Main.percentWidth*100f,Main.percentHeight*100f,1f,1f,0f);
		batch.setColor(temp);
		String text = GEngine.getInstance().state.players[GEngine.getInstance().state.currentPlayerId].getDisplayName();
		Assets.MENU_FONT.setScale(1.5f, 1.5f);
		float width = Assets.MENU_FONT.getBounds(text).width;
		Assets.MENU_FONT.draw(batch, text, Main.percentWidth*50f-width/2, Main.percentHeight*80f);
		text = "Touch screen to play.";
		Assets.MENU_FONT.setScale(1.25f, 1.25f);
		width = Assets.MENU_FONT.getBounds(text).width;
		Assets.MENU_FONT.draw(batch, text, Main.percentWidth*50f-width/2, Main.percentHeight*40f);
		Assets.MENU_FONT.setScale(prevX,prevY);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		stage.addActor(background);
	}

	@Override
	public void hide() {
		background.remove();
		this.dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		me = null;
		if(atlas!=null) atlas.dispose();
	}
}
