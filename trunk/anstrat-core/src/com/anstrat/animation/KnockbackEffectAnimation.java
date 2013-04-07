
	package com.anstrat.animation;

	import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.CombatLog;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.abilities.Knockback;
import com.anstrat.geography.TileCoordinate;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

	public class KnockbackEffectAnimation extends Animation {

		/** Time for entire animation */
		public float attackSpeed = 0.8f; 
		public float impactTime = 0.5f;
		public float impactAnimationTime = 0.3f;
		public float rangedDelay = 0.6f;
		private float stateTime = 0f;
		
		private boolean pastImpact = false;
		private boolean pastImpactAnimation = false;
		
		/** Projectile positions */
		private Vector2 start, target;
		private float xoffset, yoffset, amtOffset;
		private boolean started;
		private GUnit gAttacker, gDefender;
		private CombatLog cl;
		private final static float moveSpeed = 0.5f;
		private TileCoordinate kbCoord;
		
		/**Knockback positions*/
		private Vector2 startP, offsets, endP;
		
		private String impactAnimationName;
		
		public KnockbackEffectAnimation(CombatLog cl, TileCoordinate knockbackCoordinate){
			this.cl = cl;
			this.kbCoord = knockbackCoordinate;
			this.length = attackSpeed;
			this.lifetimeLeft = length;
			
			GEngine ge = GEngine.getInstance();
			start = ge.getMap().getTile(cl.attacker.tileCoordinate).getCenter();
			
			
			target = ge.getMap().getTile(cl.defender.tileCoordinate).getCenter();
			gAttacker = ge.getUnit(cl.attacker);
			gDefender = ge.getUnit(cl.defender);		
		}
		
		@Override
		public void run(float deltaTime) {
			
			// Run once
			if (!started) {
				System.out.println("knockbackEffect");
				TileCoordinate endTile = cl.defender.tileCoordinate;
				TileCoordinate startTile = cl.attacker.tileCoordinate;
				
				/* Don't look at this code */
				
				startP = GEngine.getInstance().getMap().getTile(startTile).getCenter();
				endP = GEngine.getInstance().getMap().getTile(endTile).getCenter();
				startP.sub(startP.cpy().sub(endP).mul(0.5f));
				System.out.println(startP);
				System.out.println(endP);
				offsets = endP.cpy().sub(startP);
				
				// Set animation length proportional to on the unit's movement speed
				//float distanceInTiles = distance.len() / GEngine.getInstance().map.TILE_WIDTH;
				//length = distanceInTiles / cl.defender.getUnitType().movementSpeed;			
				started = true;
			}
			
			amtOffset = stateTime/length;
			amtOffset = (float) Math.sqrt(amtOffset);
			
			if(lifetimeLeft <= 0){
				gDefender.setPosition(endP);
				gDefender.playIdle();
			}				
			else{
				gDefender.setPosition(new Vector2(startP.x + offsets.x*amtOffset, startP.y + offsets.y*amtOffset));				
			}
			
			stateTime += deltaTime;
		}
		
		@Override
		public void draw(float deltaTime, SpriteBatch batch){
			super.draw(deltaTime, batch);
		}

		@Override
		public boolean isVisible() {
			// TODO Auto-generated method stub
			return Fog.isVisible(gAttacker.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) ||
					Fog.isVisible(gDefender.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
		}

	}


