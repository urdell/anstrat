package com.anstrat.menu;

import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
	private TextureAtlas atlas;
	private SpriteBatch batch;
	
	private Stage stage;
	private Table background;
	
	private HotseatNextTurnScreen(){
		atlas = new TextureAtlas("textures-loadingscreen/pack");	//pack.atlas
		splashscreen = atlas.findRegion("splashscreen");
		this.batch = Main.getInstance().batch;
		this.stage = Main.getInstance().overlayStage;
		
		background = new Table();
		background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		background.setTouchable(Touchable.enabled);
		background.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
		batch.begin();
		batch.disableBlending();
		batch.draw(splashscreen,0f,0f,0f,0f,Main.percentWidth*100f,Main.percentWidth*175f,1f,1f,0f);
		batch.enableBlending();
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
		splashscreen = null;
		batch = null;
		atlas.dispose();
	}
}
