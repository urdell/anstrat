package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.core.Assets.Pair;
import com.anstrat.core.Options;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.effects.BerserkEffect;
import com.anstrat.gameCore.effects.Effect;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GUnit extends GObject {
	
	public enum AnimationState { IDLE, SPECIAL_IDLE, WALK, ATTACK, DEATH, ABILITY, CUSTOM }
	
	private Animation[] animations;
	private boolean animationLooping[];
	
	public Unit unit;
	private Sprite sprite;
	private Vector2 centerPosition;
	private float alpha = 1f;
	public ParentedGBar oldHealthBar;
	public HealthBar healthBar;
	
	public float animationSpeed = 1f;
	
	// Animation related
	private float animationTime;
	private AnimationState animationState = AnimationState.IDLE;
	private boolean animationFacingRight = true;
	
	/**
	 * Initialized at correct position
	 * @param unit
	 * @param tileHeightFraction the height of this unit as a fraction of the tile height, unit width and height will be scaled accordingly
	 */
	public GUnit(Unit unit)
	{	
		this.unit = unit;
		sprite = new Sprite(getTextureRegion(unit.getUnitType()));
		sprite.setScale(0.85f);
		sprite.flip(false, true);
		
		healthBar = new HealthBar();
		healthBar.setColors(Player.primaryColor[unit.ownerId], Player.secondaryColor[unit.ownerId], Color.BLACK);
		
		oldHealthBar = new ParentedGBar(sprite);
		oldHealthBar.setColors(Player.primaryColor[unit.ownerId], Player.secondaryColor[unit.ownerId], Color.BLACK /*new Color(0f, 0.3f, 0f, 1f))*/);
		oldHealthBar.setValue(1f);
		oldHealthBar.text = String.valueOf(unit.currentAP);
		
		// Align bottom-center of bar to bottom-center of sprite
		oldHealthBar.setPositionRelativeOrigin(0f, sprite.getHeight() / 2f - oldHealthBar.getHeight()*0.0f);
		
		Vector2 screenPos = GEngine.getInstance().map.getTile(unit.tileCoordinate).getCenter();
		setPosition(screenPos);
		
		Pair<Animation[], boolean[]> result = Assets.getAnimations(unit.getUnitType());
		animations = result.a;
		animationLooping = result.b;
		
		updateHealthbar();
		
		// Check unit for effects
		// TODO: Quick / ugly fix
		
		// If Berserk effect is active, double the animation speed
		for(Effect e : unit.getEffects()){
			if(e instanceof BerserkEffect){
				animationSpeed = 2f;
				break;
			}
		}
	}

	/**
	 * Will be useful later when we have different unit graphics.
	 * @return
	 */
	public static TextureRegion getTextureRegion(UnitType type){
		return Assets.getTextureRegion(type.idleImage);
	}
	
	public static TextureRegion getUnitPortrait(UnitType type){
		return Assets.getTextureRegion(type.portrait);
	}
	
	public void setAlpha(float alpha){
		this.alpha = alpha;
		oldHealthBar.setAlpha(alpha);
		Color c = sprite.getColor();
		sprite.setColor(c.r, c.g, c.b, alpha);
	}
	
	public float getAlpha(){
		return this.alpha;
	}
	
	public void render(SpriteBatch batch, float delta){
		
		Animation currentAnimation = animations[animationState.ordinal()];
		
		if(currentAnimation != null){
			TextureRegion region = currentAnimation.getKeyFrame(animationTime * animationSpeed, true);
			
			// If region is not flipped even though we're not facing right, flip
			if(region != null){
				int regionWidth = region.getRegionWidth();
				if(regionWidth < 0 && animationFacingRight || regionWidth > 0 && !animationFacingRight){
					region.flip(true, false);
				}
				
				sprite.setRegion(region);
				
				animationTime += Gdx.graphics.getDeltaTime()*Options.speedFactor;
				
				if(!animationLooping[animationState.ordinal()] && currentAnimation.isAnimationFinished(animationTime)){
					playIdle();
				}
			}else
				System.err.println("Tried to draw null region in GUnit.render "+animationTime);
		}
		
		sprite.draw(batch);

		Color c = batch.getColor();
		//batch.setColor(c.r, c.g, c.b, alpha);
		//batch.draw(Assets.unitTeamIndicators[unit.ownerId], sprite.getX() + sprite.getWidth() / 2f - 16f, sprite.getY() - 16f);
		batch.setColor(c);
		
		// Render health bar  information update handled by animations
		/*float healthPercentage = (float)unit.currentHP/(float)unit.getMaxHP();
		if(healthPercentage < 0f) healthPercentage = 0f;
		
		healthBar.value = healthPercentage;
		healthBar.text = String.valueOf(unit.currentAP);*/
		//oldHealthBar.render(batch);
		healthBar.draw(batch, sprite.getColor().a);
	}
	
	public void updateHealthbar(){
		float healthPercentage = (float)unit.currentHP/(float)unit.getMaxHP();
		if(healthPercentage < 0f) healthPercentage = 0f;
		
		oldHealthBar.setValue(healthPercentage);
		oldHealthBar.text = String.valueOf(unit.currentAP);
		
		// new healthbar
		healthBar.currentAP = unit.currentAP;
		healthBar.APReg = unit.getAPReg();
		healthBar.maxAP = unit.getMaxAP();
		healthBar.setHealth(healthPercentage);
	}
	
	/**
	 * @param position the new center location of this {@link GUnit}
	 */
	public void setPosition(Vector2 position){
		centerPosition = position;
		sprite.setPosition(position.x - sprite.getWidth() / 2f, position.y - sprite.getHeight() / 2f-15);
		healthBar.setPosition(centerPosition);
		oldHealthBar.update();
		boundingBoxOutdated = true;
	}
	
	/**
	 * @return the center location of this {@link GUnit}
	 */
	public Vector2 getPosition(){
		//return new Vector2(sprite.getX() + sprite.getOriginX(), sprite.getY() + sprite.getOriginY());
		return centerPosition;
	}
	
	public void setRotation(float degrees){
		sprite.setRotation(degrees);
		boundingBoxOutdated = true;
	}
	
	public void rotate(float degrees){
		sprite.rotate(degrees);
		boundingBoxOutdated = true;
	}
	
	public void setTexture(Texture texture){
		sprite.setTexture(texture);
	}
	
	// Animation related
	
	public void playCustom(Animation animation, boolean loop){
		animationTime = 0f;
		animationState = AnimationState.CUSTOM;
		animations[animationState.ordinal()] = animation;
		animationLooping[animationState.ordinal()] = loop;
	}
	
	public void playIdle(){
		animationTime = 0;
		animationState = AnimationState.IDLE;
	}
	
	public void playSpecialIdle(){
		animationTime = 0;
		animationState = AnimationState.SPECIAL_IDLE;
	}
	
	public void playWalk(){
		animationTime = 0;
		animationState = AnimationState.WALK;
	}
	
	public void setFacingRight(boolean facingRight){
		this.animationFacingRight = facingRight;
	}
	
	public void playAttack(){
		animationTime = 0;
		animationState = AnimationState.ATTACK;
	}
	public void playDeath(){
		animationTime = 0;
		animationState = AnimationState.DEATH;
	}

	@Override
	protected Rectangle getBoundingRectangle() {
		return this.sprite.getBoundingRectangle();
	}
	
	public boolean isFacingRight()
	{
		return animationFacingRight;
	}
}
