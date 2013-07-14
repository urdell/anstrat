package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class HotAnimation extends Animation {
	
	public HotAnimation(){
		this.length = 5f;
		this.lifetimeLeft = length;
	}
	
	@Override
	public void run(float deltaTime) {
		lifetimeLeft -= deltaTime;
		System.out.println("helo i alive");
	}			
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		TextureRegion tr = Assets.getTextureRegion("MenuBackground");
		Main.getInstance().batch.draw(tr, 25, 25, Main.percentWidth*100f, Main.percentHeight*100f);
		System.out.println(batch.getColor());
	}
	
	@Override
	public boolean isVisible() {
		return true;
	}
}
