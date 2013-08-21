package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SplashScreen implements Screen {

	private TextureRegion splashscreen;
	private TextureAtlas atlas;
	private SpriteBatch batch;
	private boolean finishedLoading = false;
	private float animationLength = 0.75f;
	
	private Stage stage;
	private Table background;
	
	private Screen finishScreen;
	private boolean initOnFinish;
	
	public SplashScreen(Screen finishScreen, boolean initOnFinish){
		atlas = Assets.manager.get("textures-loadingscreen/pack.atlas");
		splashscreen = atlas.findRegion("splashscreen");

		this.initOnFinish = initOnFinish;
		this.finishScreen = finishScreen;
		
		this.batch = Main.getInstance().batch;
		this.stage = Main.getInstance().overlayStage;
		
		background = new Table();
		Image loadingText = new Image(new TextureRegionDrawable(atlas.findRegion("loading")));
		//loadingText.setSize(Gdx.graphics.getWidth()/3, Gdx.graphics.getHeight()/2);
		loadingText.addAction(Actions.forever( Actions.sequence(Actions.fadeIn( animationLength ), Actions.fadeOut( animationLength ))));
		background.add(loadingText);
		background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void render(float delta) {
		batch.begin();
		batch.disableBlending();
		batch.draw(splashscreen,0f,0f,0f,0f,Main.percentWidth*100f,Main.percentWidth*178.087f,1f,1f,0f);			
		batch.enableBlending();
		batch.end();
		
		//Load textures a bit, when finished load the rest (Main.init)
		if(Assets.manager.update() && finishedLoading==false){
			if (initOnFinish) {
				Main.getInstance().init();
			}

			finishedLoading = true;
			Main.getInstance().setScreen(finishScreen != null ? finishScreen : MainMenu.getInstance());
		}
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
		Main.getInstance().invites.playerLeftSplashScreen();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}
