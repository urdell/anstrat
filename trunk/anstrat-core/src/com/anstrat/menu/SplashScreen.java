package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SplashScreen implements Screen {

	private static SplashScreen me = null;
	private TextureRegion splashscreen, splashtext;
	
	private SplashScreen()
	{
		splashscreen = Assets.getTextureRegion("splashscreen");
		splashtext = Assets.getTextureRegion("splashscreen-startgame");
	}
	
	public static SplashScreen getInstance()
	{
		if(me==null)
			me = new SplashScreen();
		return me;
	}
	
	float animationLength = 3f;
	float animationTime = 0f;
	
	@Override
	public void render(float delta) {
		SpriteBatch batch = Main.getInstance().batch;
		
		animationTime += delta;
		if(animationTime > animationLength)
			animationTime -= animationLength;
		
		Color color = batch.getColor();
		// Fade between 0.0f-1.0f
		float alpha = animationTime<animationLength/2f?animationTime/animationLength*2f:2.0f-animationTime/animationLength*2f;
		// Fade between 0.5f-1.0f
		// float alpha = animationTime<animationLength/2f?0.5f+animationTime/animationLength:1.5f-animationTime/animationLength;
		
		if(alpha>1.0f)
			alpha = 1.0f;
		else if(alpha<0.0f)
			alpha = 0.0f;
		
		color.a = alpha;
		batch.setColor(color);
		
		batch.begin();
		batch.disableBlending();
		batch.draw(splashscreen,0f,0f,0f,0f,
				Main.percentWidth*100f,Main.percentWidth*175f,1f,1f,0f);
		batch.enableBlending();
		
		batch.draw(splashtext,Main.percentWidth*61.5f, -Main.percentWidth*1f,0f,0f,
				Main.percentWidth*38.5f,Main.percentWidth*15f,1f,1f,0f);
		batch.end();
		
		if(Gdx.input.justTouched())
		{
			color.a = 1;
			batch.setColor(color);
            Main.getInstance().setScreen(MainMenu.getInstance());
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		me = null;
		splashscreen = null;
		splashtext = null;
	}
	
}
