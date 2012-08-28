package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;

public class TutorialPopup extends Popup{

	TextureRegion tutorialImage;
	int nrClicks = 0;
	
	public TutorialPopup(){
		
		tutorialImage = Assets.getTextureRegion("tutorialImage1");
		
		this.setClickListener(new ClickListener() {
	        @Override
	        public void click(Actor actor,float x,float y ){
	        	nrClicks++;
	        	switch(nrClicks){
	        	case 1:
	        		tutorialImage = Assets.getTextureRegion("tutorialImage2");
	        		break;
	        	case 2:
	        		Popup.getCurrentPopup().close();
	        		nrClicks = 0;
	        	}
	        	
	        }
		});
	}
	
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(tutorialImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	@Override
	public void resize(int width, int height) {
		overlay.setSize(width, height);
		this.width = width;
		this.height = height;
	}
}
