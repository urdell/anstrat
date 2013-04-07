package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PoisonAnimation extends Animation {

	private Unit unit;
	private GUnit gunit;
	private float stateTime = 0f;
	private int turn;
	private com.badlogic.gdx.graphics.g2d.Animation animation;
	private boolean expended = false;
	
	public PoisonAnimation(Unit unit, int turn){
		this.turn = turn;
		this.unit = unit;
		this.gunit = GEngine.getInstance().getUnit(unit);
		this.lifetimeLeft = 4f;
		animation = Assets.getAnimation("poison");
	}
	
	@Override
	public void run(float deltaTime) {
		stateTime += deltaTime;
		if(!unit.isAlive || GEngine.getInstance().state.turnNr > turn){
			//if(!expended){
			//	expended = true;
			//	lifetimeLeft = 2f;
			//}
			lifetimeLeft = 4f;
		}
		else
			lifetimeLeft = 4f;
	}

	@Override
	public boolean isVisible() {
		return Fog.isVisible(unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}

	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		TextureRegion region = animation.getKeyFrame(stateTime, true);
		float scale = 0.75f;
		Color temp = batch.getColor();
		if(lifetimeLeft < 2f)
			batch.setColor(temp.r, temp.g, temp.b, lifetimeLeft / 2f);
		batch.draw(region, 
				gunit.getPosition().x-region.getRegionWidth() / 2f * scale, 
				gunit.getPosition().y-region.getRegionHeight() / 2f * scale, 
				region.getRegionWidth() * scale, 
				region.getRegionHeight() * scale);
		batch.setColor(temp);			
	}
}