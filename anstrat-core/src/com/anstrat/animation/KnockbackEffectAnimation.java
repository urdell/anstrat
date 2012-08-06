
	package com.anstrat.animation;

	import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.abilities.Knockback;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

	public class KnockbackEffectAnimation extends Animation {

		/** Time for entire animation */
		public float attackSpeed = 0.8f; 
		public float impactTime = 0.5f;
		public float impactAnimationTime = 0.3f;
		public float rangedDelay = 0.6f;
		
		private boolean pastImpact = false;
		private boolean pastImpactAnimation = false;
		
		/** Projectile positions */
		private Vector2 start, current, target;
		private float xoffset, yoffset, amtOffset;
		private boolean started;
		private GUnit gAttacker, gDefender;
		private CombatLog cl;
		private float timeElapsed;
		private final static float moveSpeed = 0.5f;
		
		/**Knockback positions*/
		private Vector2 startP, currentP, endP;
		
		private String impactAnimationName;
		
		public KnockbackEffectAnimation(CombatLog cl){
			this.cl = cl;
			
			
			
			this.length = attackSpeed;
			this.lifetimeLeft = length;
			
			GEngine ge = GEngine.getInstance();
			start = ge.getMap().getTile(cl.attacker.tileCoordinate).getCenter();
			
			
			target = ge.getMap().getTile(cl.defender.tileCoordinate).getCenter();
			gAttacker = ge.getUnit(cl.attacker);
			gDefender = ge.getUnit(cl.defender);
			xoffset = target.x - start.x;
			yoffset = target.y - start.y;
			current = new Vector2();
			
		}
		
		@Override
		public void run(float deltaTime) {
			
			// Run once
			if (!started) {
				System.out.println("knockbackEffect");
				TileCoordinate startTile = cl.defender.tileCoordinate;
				TileCoordinate endTile = Knockback.getKnockBackCoordinate(cl.attacker,cl.defender);
				startP = GEngine.getInstance().getMap().getTile(startTile).getCenter();
				endP = GEngine.getInstance().getMap().getTile(endTile).getCenter();
				currentP = new Vector2();
				Vector2 distance = endP.cpy().sub(startP);
				
				// Set animation length proportional to on the unit's movement speed
				float distanceInTiles = distance.len() / GEngine.getInstance().map.TILE_WIDTH;
				length = distanceInTiles / cl.defender.getUnitType().movementSpeed;
				lifetimeLeft = length;
				
			
				if(lifetimeLeft <= 0){
					gDefender.setRotation(0);
					gDefender.setPosition(endP);
					gDefender.playIdle();
				}
				
				else{
					amtOffset = (length-lifetimeLeft)/length;
					currentP.set(startP.x + xoffset*amtOffset, startP.y + yoffset*amtOffset);
					gDefender.setPosition(currentP);
					started = true;
				}
			}
		}
		
		@Override
		public void draw(float deltaTime, SpriteBatch batch){
			super.draw(deltaTime, batch);
			
			/*float animationTimePassed = length - lifetimeLeft;
			
			// Have the projectile reached its target?
			if(animationTimePassed > rangedDelay){
				
				UnitType type = cl.attacker.getUnitType();
				TextureRegion region = null;
				
				if(type == UnitType.AXE_THROWER){
					region = Assets.getAnimation("axe-effect").getKeyFrame(animationTimePassed, true);
				}
				else if((type == UnitType.SHAMAN || type == UnitType.GOBLIN_SHAMAN) && length - lifetimeLeft < impactTime){
					region = Assets.getAnimation("shaman-fireball").getKeyFrame(animationTimePassed, true);
				}
				
				// Draw impact effect
				if(region != null) batch.draw(region, current.x - region.getRegionWidth() / 2, current.y + region.getRegionHeight() / 2);
			}*/
			
		}

		@Override
		public boolean isVisible() {
			// TODO Auto-generated method stub
			return Fog.isVisible(gAttacker.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) ||
					Fog.isVisible(gDefender.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
		}

	}


