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
import com.badlogic.gdx.math.Vector2;

public class MagicSpearAnimation extends Animation {

	/** Time for entire animation */
	public float attackSpeed = 0.8f; 
	public float impactTime = 0.5f;
	public float impactAnimationTime = 0.3f;
	public float rangedDelay = 0.6f;
	
	private boolean pastImpact = false;
	private boolean pastImpactAnimation = false;
	
	/** Projectile positions */
	private Vector2 start, current, target;
	private float xoffset, yoffset;
	private boolean started;
	private GUnit gAttacker, gDefender;
	
	private Unit sourceUnit, targetUnit;
	private int damage;
	private boolean firstUnit;
	private com.badlogic.gdx.graphics.g2d.Animation sourceAnimation;
	
	public MagicSpearAnimation(Unit sourceUnit, Unit targetUnit, int damage){
		this.sourceUnit = sourceUnit;
		this.targetUnit = targetUnit;
		this.damage = damage;
	
		
		// Set animation timings
		rangedDelay = 0.3f;
		attackSpeed = 1.2f;
		impactTime = 1f;
		impactAnimationTime = impactTime; // start playing exactly when spear hits

		this.length = attackSpeed;
		this.lifetimeLeft = length;
		
		GEngine ge = GEngine.getInstance();
		start = ge.getMap().getTile(sourceUnit.tileCoordinate).getCenter();
		
		target = ge.getMap().getTile(targetUnit.tileCoordinate).getCenter();
		gAttacker = ge.getUnit(sourceUnit);
		gDefender = ge.getUnit(targetUnit);
		xoffset = target.x - start.x;
		yoffset = target.y - start.y;
		current = new Vector2();
		
		sourceAnimation = Assets.getAnimation("valkyrie-ability");
	}
	@Override
	public void run(float deltaTime) {
		
		// Run once
		if (!started) {
			GEngine ge = GEngine.getInstance();
			ge.updateUI();
			if(isVisible()) {
				Animation mAnimation = new MoveCameraAnimation(gDefender.getPosition());
				ge.animationHandler.runParalell(mAnimation);
			}
				
			if(firstUnit){
				gAttacker.healthBar.currentAP = sourceUnit.currentAP;
			}
			
			boolean facingRight = sourceUnit.tileCoordinate.x <= targetUnit.tileCoordinate.x;
			gAttacker.setFacingRight(facingRight);
			gAttacker.playAttack();

			started = true;
		}
		
		if(!pastImpactAnimation && length - lifetimeLeft > impactAnimationTime){ // Time of impact animation (slightly before actual impact
			//GEngine.getInstance().animationHandler.runParalell(new GenericVisualAnimation(Assets.getAnimation(impactAnimationName), target, 100)); // size 100 is slightly smaller than a tile
			gDefender.playHurt();
			pastImpactAnimation = true;
		}
		
		if(!pastImpact && length - lifetimeLeft > impactTime){ // Time of impact
			// Show damage taken etc.
			GEngine ge = GEngine.getInstance();
			FloatingTextAnimation animation = new FloatingTextAnimation(targetUnit.tileCoordinate, String.valueOf(damage), Color.RED);
			ge.animationHandler.runParalell(animation);
			float healthPercentage = (float)targetUnit.currentHP/(float)targetUnit.getMaxHP();
			
			if(healthPercentage < 0f){
				healthPercentage = 0f;
			}
			
			//gDefender.healthBar.setValue(healthPercentage);t
			gDefender.healthBar.setHealth(healthPercentage, targetUnit.currentHP);
			boolean directionLeft = start.x > target.x;
			GEngine.getInstance().animationHandler.runParalell(new BloodAnimation(gDefender,directionLeft));
			
			if(targetUnit.currentHP <= 0){
				Vector2 temp = new Vector2(gDefender.getPosition());
				ge.animationHandler.runParalell(new DeathAnimation(targetUnit, 
						temp.sub(gAttacker.getPosition()).nor()));
			}
			pastImpact = true;
		}

		// Update projectile position
		float timeTaken = attackSpeed - lifetimeLeft;
		float amtOffset = (timeTaken - rangedDelay) / (impactTime - rangedDelay);
		current.set(start.x + xoffset * amtOffset, start.y + yoffset * amtOffset);
	}			
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		float animationTimePassed = length - lifetimeLeft;
		
		// Have the projectile reached its target?
		
			if(animationTimePassed > rangedDelay){
				TextureRegion region = null;
				region = Assets.getAnimation("valkyrie-ability-effect").getKeyFrame(animationTimePassed, true);
				// Draw impact effect
				if(region != null) {
					batch.draw(region, current.x - region.getRegionWidth() / 2, current.y + region.getRegionHeight() / 2);
				}
			}
	}
	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(sourceUnit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) ||
				Fog.isVisible(targetUnit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}
