package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Options;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.geography.TerrainType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GBuilding extends GObject {

	public Building building;
	private Sprite sprite;
	private Vector2 screenPos;
	private float flagAnimationStateTime;
	private com.badlogic.gdx.graphics.g2d.Animation portalAnimation = null;
	
	private float flagX, flagY;
	private float flagScale = 1f;
	private static final float FLAG_SCALE_CASTLE = .85f;
	private static final float FLAG_SCALE_VILLAGE = .5f;
	private boolean portal = false;
	private float portalScale = 0.5f;
	
	/**
	 * @return the screenPos
	 */
	public Vector2 getScreenPos() {
		return screenPos;
	}

	private int lastOwner;
	
	/**
	 * Initialized at correct position
	 * @param unit
	 */
	public GBuilding(Building building, GMap map, int team)
	{
		this.building = building;
		this.lastOwner = building.controllerId;
		
		sprite = new Sprite(getTextureRegion(building.type, team));
		
		if(building.type == Building.TYPE_CASTLE) {
			sprite.setColor(sprite.getColor().r, sprite.getColor().g, sprite.getColor().b, 0);
			if(team==0){
				map.getTile(building.tileCoordinate).tile.terrain = TerrainType.CASTLE;
				map.getTile(building.tileCoordinate).setTexture(TerrainType.CASTLE);
			}
			else{
				portalAnimation = Assets.getAnimation("portal-effect");
				portal = true;
				map.getTile(building.tileCoordinate).tile.terrain = TerrainType.PORTAL;
				map.getTile(building.tileCoordinate).setTexture(TerrainType.PORTAL);
			}
			
			flagScale = FLAG_SCALE_CASTLE;
		}
		else if(building.type == Building.TYPE_VILLAGE) {
			sprite.setColor(sprite.getColor().r, sprite.getColor().g, sprite.getColor().b, 0);
			map.getTile(building.tileCoordinate).tile.terrain = TerrainType.VILLAGE;
			map.getTile(building.tileCoordinate).setTexture(TerrainType.VILLAGE);
			 
			flagScale = FLAG_SCALE_VILLAGE;
		}
			
		
		// Make buildings take up 90% of a tile
		float scale = (map.TILE_HEIGHT * 0.9f) / sprite.getHeight();
		sprite.setScale(scale);
		sprite.flip(false, true);
				
		screenPos = map.getTile(building.tileCoordinate).getCenter();
		setPosition(screenPos);
	}
	
	public static TextureRegion getTextureRegion(int type, int team){
		switch(type){
		case Building.TYPE_RUNE:
			return Assets.getTextureRegion("runestone-gray");
		case Building.TYPE_CASTLE:
			if(team==0)
				return Assets.getTextureRegion("castle");
			else{
				return Assets.getTextureRegion("portal-base");
			}
		case Building.TYPE_VILLAGE:
			return Assets.getTextureRegion("village");
		default:
			return Assets.getTextureRegion("village");
		}
	}
	
	public void render(SpriteBatch batch, float delta)
	{
		//sprite.draw(batch);
		flagAnimationStateTime += delta;
		
		if(portal){
			TextureRegion tr = portalAnimation.getKeyFrame(flagAnimationStateTime, true);
			batch.draw(tr, sprite.getX() + tr.getRegionWidth()*(1f-portalScale) / 2f, 
					sprite.getY() + tr.getRegionHeight()*(1f-portalScale) / 2f, 
					tr.getRegionWidth() * portalScale, 
					tr.getRegionHeight() * portalScale);
		}
		
		// Check if building has changed owner
		if(lastOwner != building.controllerId){
			lastOwner = building.controllerId;
		}
		
		// Render flag (if the village is owned by a player and village is not fogged)
		if(building.type == Building.TYPE_CASTLE || 
				lastOwner != -1 && 
				Fog.isVisible(building.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId)){
			
			
			// Player0 = blue, Player0 = red
			TextureRegion region = Assets.getAnimation(lastOwner == 0 ? "flag-inplace-blue" : "flag-inplace-red").getKeyFrame(flagAnimationStateTime, true);
			batch.draw(region, flagX, flagY, 
					0f,			 				// originX
					region.getRegionHeight(), 	// originY
					region.getRegionWidth(),	// width 
					region.getRegionHeight(),	// height
					flagScale, 					// scaleX
					flagScale,					// scaleY 
					0f);						// rotation
		}
		
		if(Options.DEBUG_SHOW_OWNER && building.controllerId >= 0){
			Player p = State.activeState.players[building.controllerId];
			Assets.MENU_FONT.setColor(Color.BLACK);
			Assets.MENU_FONT.setScale(1.3f);
			Assets.MENU_FONT.draw(batch, ""+p.playerId, screenPos.x-10, screenPos.y-20);
		}
	}
	
	/**
	 * 
	 * @param position Center of the Unit at it's new position.
	 */
	public void setPosition(Vector2 position){
		sprite.setPosition(position.x-sprite.getWidth()/2f, position.y-sprite.getHeight()/2f);
		
		this.boundingBoxOutdated = true;
		
		// The offsets depends on where the flag pole is positioned in relation to the center of the building texture
		if(building.type == Building.TYPE_CASTLE){
			this.flagX = sprite.getOriginX() + sprite.getX() + 27f;
			this.flagY = sprite.getOriginY() + sprite.getY() - 80f;	//- 5f
		}
		else if(building.type == Building.TYPE_VILLAGE){
			this.flagX = sprite.getOriginX() + sprite.getX() + 33f;
			this.flagY = sprite.getOriginY() + sprite.getY() - 93f;	//+ 3f
		}
	}

	@Override
	protected Rectangle getBoundingRectangle() {
		return sprite.getBoundingRectangle();
	}
}
