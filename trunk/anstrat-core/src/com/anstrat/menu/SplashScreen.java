package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SplashScreen implements Screen {

	private static SplashScreen me = null;
	private TextureRegion splashscreen;
	private TextureAtlas atlas;
	private SpriteBatch batch;
	private boolean finishedLoading = false;
	private float animationLength = 0.75f;
	
	private Stage stage;
	private Table background;
	
	private SplashScreen(){
		atlas = Assets.manager.get("textures-loadingscreen/pack.atlas");
		splashscreen = atlas.findRegion("splashscreen");
		
		this.batch = Main.getInstance().batch;
		this.stage = Main.getInstance().overlayStage;
		
		background = new Table();
		Image loadingText = new Image(new TextureRegionDrawable(atlas.findRegion("loading")));
		loadingText.addAction(Actions.forever( Actions.sequence(Actions.fadeIn( animationLength ), Actions.fadeOut( animationLength ))));
		background.add(loadingText);
		background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		background.setTouchable(Touchable.enabled);
		background.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	if(finishedLoading)
            		Main.getInstance().setScreen(MainMenu.getInstance());
            }
        });
	}
	
	public static SplashScreen getInstance(){
		if(me==null)
			me = new SplashScreen();
		return me;
	}
	
	@Override
	public void render(float delta) {
		batch.begin();
		batch.disableBlending();
		batch.draw(splashscreen,0f,0f,0f,0f,Main.percentWidth*100f,Main.percentHeight*100f,1f,1f,0f);
		batch.enableBlending();
		batch.end();
		
		//Load textures a bit, when finished load the rest (Main.init)
		if(Assets.manager.update() && finishedLoading==false){
			Main.getInstance().init();
			finishedLoading = true;
			background.clear();
			Image startText = new Image(new TextureRegionDrawable(atlas.findRegion("splashscreen-startgame")));
			startText.addAction(Actions.forever( Actions.sequence(Actions.fadeOut( animationLength ), Actions.fadeIn( animationLength ))));
			background.bottom().right().add(startText).size(Main.percentWidth*38.5f,Main.percentHeight*8f)
				.padBottom(-Main.percentWidth*0.25f);//.padRight(Main.percentWidth*1f);
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
