package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class LifeStealAnimation extends Animation{
	
	private boolean started;
	private float animationStateTime;
	private GUnit source, target;
	private com.badlogic.gdx.graphics.g2d.Animation sourceAnimation, targetAnimation;
	private Unit attacker, defender;
	
	private static final float START_DELAY = 0.5f;
	
	public LifeStealAnimation(Unit source, Unit target){
	attacker = source;
	defender = target;
	sourceAnimation = Assets.getAnimation("wolf-attack");
	targetAnimation = Assets.getAnimation("wolf-attack-effect");
	
	GEngine engine = GEngine.getInstance();
	this.source = engine.getUnit(source);
	this.target = engine.getUnit(target);
	length = sourceAnimation.animationDuration + targetAnimation.animationDuration;
	lifetimeLeft = length;
}

@Override
public void run(float deltaTime) {
	
	// Run once
	if(!started){
		GEngine ge = GEngine.getInstance();
		ge.updateUI();
		
		Animation mAnimation = new MoveCameraAnimation(target.getPosition());
		ge.animationHandler.runParalell(mAnimation);
		
		boolean facingRight = attacker.tileCoordinate.x <= defender.tileCoordinate.x;
		source.setFacingRight(facingRight);
		target.setFacingRight(!facingRight);
		source.playAttack();
		started = true;	
	}
	
	if(lifetimeLeft <= 0f){
		source.updateHealthbar();
		target.updateHealthbar();
	}
}

@Override
public void draw(float deltaTime, SpriteBatch batch){
	super.draw(deltaTime, batch);
	float timePassed = length - lifetimeLeft;
	
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	Vector2 position = null;
	
	if(timePassed >= START_DELAY + sourceAnimation.animationDuration){
		animation = targetAnimation;
		position = target.getPosition();
		
		TextureRegion region = animation.getKeyFrame(animationStateTime, true);
		batch.draw(region, position.x - region.getRegionWidth() / 2f, position.y + region.getRegionHeight() / 2f);
	}
	else if(timePassed >= START_DELAY){
		animation = sourceAnimation;
		position = source.getPosition();
		
		TextureRegion region = animation.getKeyFrame(animationStateTime, true);
		batch.draw(region, position.x - 3f - region.getRegionWidth() / 2f, position.y + region.getRegionHeight());
	}
	
	animationStateTime += deltaTime;
}

@Override
public boolean isVisible() {
	// TODO Auto-generated method stub
	return Fog.isVisible(source.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) ||
			Fog.isVisible(target.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
}
}

