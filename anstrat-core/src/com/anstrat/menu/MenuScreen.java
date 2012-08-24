package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

/**
 * @author Kalle
 */
public abstract class MenuScreen implements Screen {
	
	public static final int BUTTON_WIDTH  = (int) (85*Main.percentWidth);
	public static final int BUTTON_HEIGHT = (int) (10*Main.percentHeight);
	
	private OrthographicCamera uiCamera;
	protected Stage stage;
	protected Table contents;
	protected static Actor flag;
    
	/**
	 * Constructor
	 * @param background The texture to use as a background for the menu
	 */
	public MenuScreen(){
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false);
		
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true, Main.getInstance().batch);
		stage.setCamera(uiCamera);
        
        // Flag animation
		flag = new Actor(){
			private float flagAnimationStateTime;
			private Animation flagAnimation = Assets.getAnimation("flag-inplace-black");
			
			@Override
			public void draw(SpriteBatch batch, float parentAlpha) {
				batch.draw(flagAnimation.getKeyFrame(flagAnimationStateTime, true), 
						Main.percentWidth*79.5f, Main.percentHeight*13.5f, 0f, 0f, 
								Main.percentWidth*10.5f, Main.percentHeight*7.5f, 0.7f, 0.7f, 0f);
				flagAnimationStateTime += Gdx.graphics.getDeltaTime();
			}

			@Override
			public Actor hit(float x, float y) {
				return null;
			}
		};
		
		stage.addActor(flag);
		
		contents = new Table(Assets.SKIN);
		setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        contents.top().center();
        //contents.setBackground(new NinePatch(Assets.getTextureRegion("MenuBackground")));
        
        stage.addActor(contents);
	}
	
	public void setBounds(float x, float y, float width, float height){
		contents.width = width;
        contents.height = height;
        contents.x = x;
        contents.y = y;
	}
	
	public boolean textFieldSelected(){
		return stage.getKeyboardFocus() instanceof TextField;
	}
	
	@Override
	public void render(float delta) {
		drawBackground();
		stage.act(delta);
		uiCamera.apply(Gdx.graphics.getGL10());
		stage.draw();
		Table.drawDebug(stage);
	}
	
	public void drawBackground() {
		Main.getInstance().batch.begin();
		Main.getInstance().batch.draw(Assets.getTextureRegion("MenuBackground"), 0, 0, 
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Main.getInstance().batch.end();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
	}

	@Override
	public void pause(){
		
	}
	
	@Override
	public void resume(){
		
	}
	
	@Override
	public void show() {
		Main.getInstance().addProcessor(stage);
	}

	@Override
	public void hide() {
		Main.getInstance().removeProcessor(stage);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
