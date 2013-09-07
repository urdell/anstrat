package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.State;
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
	private int turn, player;
	private com.badlogic.gdx.graphics.g2d.Animation animation;
	public boolean expended = false;
	private State cstate = null;
	
	public PoisonAnimation(Unit unit){
		this.cstate = GEngine.getInstance().state;
		this.turn = GEngine.getInstance().state.turnNr;
		this.player = GEngine.getInstance().state.currentPlayerId;
		this.unit = unit;
		this.gunit = GEngine.getInstance().getUnit(unit);
		this.lifetimeLeft = 4f;
		animation = Assets.getAnimation("poison");
	}
	
	@Override
	public void run(float deltaTime) {
		stateTime += deltaTime;
		if(!(unit.currentHP > 0) || GEngine.getInstance().state.currentPlayerId == player &&
				GEngine.getInstance().state.turnNr > turn){
			if(!expended){
				expended = true;
				lifetimeLeft = 2f;
			}
		}
		else if(!expended){
			lifetimeLeft = 4f;
		}
	}

	@Override
	public boolean isVisible() {
		return Fog.isVisible(unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) &&
				cstate == State.activeState;
	}

	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		TextureRegion region = animation.getKeyFrame(stateTime, true);
		float scale = 0.75f;
		Color temp = batch.getColor();
		if(lifetimeLeft < 2f)
			batch.setColor(temp.r, temp.g, temp.b, lifetimeLeft / 2f);
		else if(stateTime < 2f)
			batch.setColor(temp.r, temp.g, temp.b, stateTime / 2f);
		batch.draw(region, 
				gunit.getPosition().x-region.getRegionWidth() / 2f * scale, 
				gunit.getPosition().y-region.getRegionHeight() / 2f * scale, 
				region.getRegionWidth() * scale, 
				region.getRegionHeight() * scale);
		batch.setColor(temp);			
	}
}