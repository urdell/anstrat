package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class LifeStealAnimation extends Animation{
	
	private boolean started;
	private float animationStateTime, bloodthirstDuration;
	private GUnit source, target;
	private com.badlogic.gdx.graphics.g2d.Animation sourceAnimation, targetAnimation;
	private Unit attacker, defender;
	private int damage;
	private boolean showedDamage = false, showedHeal = false, facingRight = false;
	private Vector2 offsets;
	private Sprite bloods = null;
	
	//private static final float START_DELAY = 0.5f;
	
	public LifeStealAnimation(Unit source, Unit target, int damage){
	attacker = source;
	defender = target;
	this.damage = damage;
	bloods = new Sprite();
	
	bloodthirstDuration = 0.8f;
	
	sourceAnimation = Assets.getAnimation("wolf-ability");
	targetAnimation = Assets.getAnimation("wolf-ability-effect");
	
	GEngine engine = GEngine.getInstance();
	this.source = engine.getUnit(source);
	this.target = engine.getUnit(target);
	
	this.offsets = this.source.getPosition().cpy().sub(this.target.getPosition());
	
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
		
		facingRight = attacker.tileCoordinate.x <= defender.tileCoordinate.x;
		source.setFacingRight(facingRight);
		target.setFacingRight(!facingRight);
		started = true;
		source.playCustom(sourceAnimation, false);
	}
}

@Override
public void draw(float deltaTime, SpriteBatch batch){
	super.draw(deltaTime, batch);
	float timePassed = length - lifetimeLeft;
	
	com.badlogic.gdx.graphics.g2d.Animation animation = null;
	Vector2 position = null;
	TextureRegion region = null;
	
	animation = sourceAnimation;
	position = source.getPosition();
	
	Color temp = batch.getColor().cpy();
	
	region = sourceAnimation.getKeyFrame(animationStateTime, false);
	
	if(timePassed < bloodthirstDuration / 2f){
		float tint_var = timePassed/(bloodthirstDuration / 2f);
		batch.setColor(1f,1f-tint_var,1f-tint_var,1f);		
	}
	else if(timePassed < bloodthirstDuration){
		float tint_var = 1f-(timePassed - (bloodthirstDuration / 2f))/(bloodthirstDuration / 2f);
		batch.setColor(1f,1f-tint_var,1f-tint_var,1f);
	}
	else
		batch.setColor(Color.WHITE);

	/* 0.85 scale and 15 pixel justifications to match "standard" animation location */
	if(timePassed <= sourceAnimation.animationDuration){
		batch.draw(region, 
				facingRight?position.x - region.getRegionWidth() / 2f * 0.85f:position.x + region.getRegionWidth() / 2f * 0.85f, 
				position.y - region.getRegionHeight() / 2f * 0.85f - 15, 
				facingRight?region.getRegionWidth()*0.85f:-region.getRegionWidth()*0.85f, 
				region.getRegionHeight()*0.85f);
	}
	
	float timeIntoTarget = timePassed - sourceAnimation.animationDuration;
	
	if(timeIntoTarget > 0 && timeIntoTarget < targetAnimation.animationDuration){
		animation = targetAnimation;
		position = target.getPosition();
		region = animation.getKeyFrame(timeIntoTarget, false);
		bloods.setRegion(region);
		bloods.setBounds(0, 0, region.getRegionWidth(), region.getRegionHeight());
		float relativeCompletion = timeIntoTarget / targetAnimation.animationDuration;
		float scaleFactor = (float)Math.pow(relativeCompletion,2)+0.5f;
		float offsetAmt = (float)Math.pow(relativeCompletion,3);
		bloods.setScale(scaleFactor);
		bloods.setPosition(position.x - region.getRegionWidth() / 2f * scaleFactor + offsets.x * offsetAmt, 
				position.y - region.getRegionHeight() / 2f * scaleFactor + offsets.y * offsetAmt);
		// Nice fade
		bloods.setColor(1f,1f,1f,1f-(float)Math.pow(relativeCompletion,4));
		bloods.draw(batch);
	}
	
	if(!showedDamage && timeIntoTarget > 0) {
		showedDamage = true;
		target.updateHealthbar();
		FloatingNumberAnimation fanimation = new FloatingNumberAnimation(target.unit.tileCoordinate, damage, 40f, Color.RED);
		//FloatingTextAnimation fanimation = new FloatingTextAnimation(target.unit.tileCoordinate, String.valueOf(damage), Color.RED);
		GEngine.getInstance().animationHandler.runParalell(fanimation);
	}
	
	if(!showedHeal && timeIntoTarget >= targetAnimation.animationDuration*0.75f){
		showedHeal = true;
		source.updateHealthbar();
		FloatingNumberAnimation fanimation = new FloatingNumberAnimation(source.unit.tileCoordinate, damage, 40f, Color.GREEN);
		//FloatingTextAnimation fanimation = new FloatingTextAnimation(source.unit.tileCoordinate, String.valueOf(damage), Color.GREEN);
		GEngine.getInstance().animationHandler.runParalell(fanimation);
	}
	
	batch.setColor(temp);
	
	animationStateTime += deltaTime;		
}

@Override
public boolean isVisible() {
	// TODO Auto-generated method stub
	return Fog.isVisible(source.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) ||
			Fog.isVisible(target.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
}
}

