package com.anstrat.animation;

import com.anstrat.audio.AudioAssets;
import com.anstrat.audio.DelayedSound;
import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GMap;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class AttackAnimation extends Animation{
	/** Time for entire animation */
	public float attackSpeed = 0.8f; 
	public float impactTime = 0.5f;
	private String impactAnimationName;
	public float rangedDelay = 0.6f;
	
	private boolean pastImpact = false;
	
	/** Projectile positions */
	private Vector2 start, current, target;
	private float xoffset, yoffset;
	private boolean started;
	private GUnit gAttacker, gDefender;
	private CombatLog cl;
	private DelayedSound swingSfx;
	private com.badlogic.gdx.graphics.g2d.Animation light = null;
	
	public AttackAnimation(CombatLog combatLog){
		this.cl = combatLog;
		
		// Set animation timings
		switch(cl.attacker.getUnitType()){
			case AXE_THROWER: {
				attackSpeed = 1.5f;
				impactTime = (attackSpeed + rangedDelay) / 2;
				break;
			}
			case GOBLIN_SHAMAN: {
				rangedDelay = 0.3f;
				attackSpeed = 1.2f;
				impactTime = 1f;
				break;
			}
			case SHAMAN: {
				rangedDelay = 0.3f;
				attackSpeed = 1.2f;
				impactTime = 4f/9f;
				break;
			}
		}
		
		this.swingSfx = AudioAssets.getSound(cl.attacker.getUnitType().swingSfx);
		
		this.length = attackSpeed;
		this.lifetimeLeft = length;
		this.impactAnimationName = String.format("%s-attack-effect", cl.attacker.getUnitType().graphicsFolder);
		
		GEngine ge = GEngine.getInstance();
		start = ge.getMap().getTile(cl.attacker.tileCoordinate).getCenter();
		UnitType attackUnitType = cl.attacker.getUnitType();
		
		target = ge.getMap().getTile(cl.defender.tileCoordinate).getCenter();
		gAttacker = ge.getUnit(cl.attacker);
		gDefender = ge.getUnit(cl.defender);
		xoffset = target.x - start.x;
		yoffset = target.y - start.y;
		current = new Vector2();
		
		if(attackUnitType == UnitType.GOBLIN_SHAMAN){
			GMap map = GEngine.getInstance().map;
			start.y -= map.TILE_HEIGHT / 3f;
			target.y -= map.TILE_HEIGHT / 3f;
		}
		else if(attackUnitType == UnitType.SHAMAN){
			start = target;
		}
		else if(attackUnitType == UnitType.AXE_THROWER){
			GMap map = GEngine.getInstance().map;
			start.y -= map.TILE_HEIGHT / 2f;
			target.y -= map.TILE_HEIGHT / 2f;
		}
	}
	
	@Override
	public void run(float deltaTime) {
		
		if(swingSfx!=null)
			swingSfx.run(deltaTime);
		
		// Run once
		if (!started) {
			GEngine ge = GEngine.getInstance();
			ge.updateUI();
			
			//moveCamera(); delegated outside
			
			//gAttacker.healthBar.text = String.valueOf(cl.newAttackerAP);
			gAttacker.healthBar.currentAP = cl.newAttackerAP;
			
			boolean facingRight = cl.attacker.tileCoordinate.x <= cl.defender.tileCoordinate.x;
			gAttacker.setFacingRight(facingRight);
			gDefender.setFacingRight(!facingRight);
			gAttacker.playAttack();
			
			started = true;
		}
		
		if(!pastImpact && length - lifetimeLeft > impactTime){ // Time of impact
			// Start impact animation
			if(cl.attacker.getUnitType() != UnitType.SHAMAN)
				GEngine.getInstance().animationHandler.runParalell(new GenericVisualAnimation(Assets.getAnimation(impactAnimationName),
						target, 100)); // size 100 is slightly smaller than a tile
			AudioAssets.playSound(cl.attacker.getUnitType().impactSfx);
	
			// Show damage taken etc.
			GEngine ge = GEngine.getInstance();
			//FloatingTextAnimation animation = new FloatingTextAnimation(cl.defender.tileCoordinate, String.valueOf(cl.attackDamage), Color.RED);
			FloatingNumberAnimation animation = new FloatingNumberAnimation(cl.defender.tileCoordinate, cl.attackDamage, 40f, Color.RED);
			ge.animationHandler.runParalell(animation);
			float healthPercentage = (float)cl.newDefenderHP/(float)cl.defender.getMaxHP();
			
			if(healthPercentage < 0f){
				healthPercentage = 0f;
			}
			
			//gDefender.healthBar.setValue(healthPercentage);
			gDefender.healthBar.setHealth(healthPercentage, cl.newDefenderHP);
			
			// Hurt and blood animation
			boolean directionLeft = start.x > target.x;
			GEngine.getInstance().animationHandler.runParalell(new BloodAnimation(gDefender, directionLeft));
			gDefender.playHurt();
			
			if(cl.newDefenderHP <= 0){
				Vector2 temp = new Vector2(gDefender.getPosition());
				ge.animationHandler.runParalell(new DeathAnimation(cl.defender, 
						temp.sub(gAttacker.getPosition()).nor()));
			}
			
			pastImpact = true;
		}

		// Update projectile position
		switch(cl.attacker.getUnitType()){
			case AXE_THROWER: {
				// Throwing axe
				float amtOffset = (attackSpeed - rangedDelay-Math.abs(lifetimeLeft * 2-(attackSpeed - rangedDelay))) / (attackSpeed - rangedDelay);
				current.set(start.x + xoffset * amtOffset, start.y + yoffset * amtOffset);
				break;
			}
			case SHAMAN:
				break;
			case GOBLIN_SHAMAN: {
				// Fireball
				float timeTaken = attackSpeed - lifetimeLeft;
				float amtOffset = (timeTaken - rangedDelay) / (impactTime - rangedDelay);
				current.set(start.x + xoffset * amtOffset, start.y + yoffset * amtOffset);
			}
		}
	}
	
	public void moveCamera(){
		if(isVisible()){
			Animation mAnimation = new MoveCameraAnimation(gDefender.getPosition());
			GEngine.getInstance().animationHandler.enqueue(mAnimation);
		}
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		super.draw(deltaTime, batch);
		
		float animationTimePassed = length - lifetimeLeft;
		UnitType type = cl.attacker.getUnitType();
		TextureRegion region = null;
		
		if(type == UnitType.SHAMAN){
			if(light==null){
				light = Assets.getAnimation("lightning");
				start.y -= light.getKeyFrame(0).getRegionHeight()*1.05;
			}
			if(animationTimePassed > impactTime && animationTimePassed < impactTime +  light.animationDuration){
				region = light.getKeyFrame(animationTimePassed-impactTime, false);
				batch.draw(region, start.x - region.getRegionWidth()/2, start.y + region.getRegionWidth()/2);
			}
		}	
		// Has the projectile reached its target?
		else if(animationTimePassed > rangedDelay){			
			if(type == UnitType.AXE_THROWER){
				region = Assets.getAnimation("axe-effect").getKeyFrame(animationTimePassed, true);
			}
			else if(type == UnitType.GOBLIN_SHAMAN && length - lifetimeLeft < impactTime){
				region = Assets.getAnimation("shaman-fireball").getKeyFrame(animationTimePassed, true);
			}
			
			// Draw projectile
			if(region != null) batch.draw(region, current.x - region.getRegionWidth() / 2, current.y + region.getRegionHeight() / 2);
		}
		
	}

	@Override
	public boolean isVisible() {
		return Fog.isVisible(gAttacker.unit.tileCoordinate, GameInstance.activeGame.getUserPlayer().playerId) ||
				Fog.isVisible(gDefender.unit.tileCoordinate, GameInstance.activeGame.getUserPlayer().playerId);
	}
}
