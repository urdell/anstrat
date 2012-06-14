package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.core.Options;
import com.anstrat.gameCore.Building;
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
	private ParentedGBar captureBar;
	private Vector2 screenPos;
	private float flagAnimationStateTime;
	
	private float flagX, flagY;
	private float flagScale = 1f;
	private static final float FLAG_SCALE_CASTLE = .85f;
	private static final float FLAG_SCALE_VILLAGE = .5f;
	
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
	public GBuilding(Building building, GMap map)
	{
		this.building = building;
		this.lastOwner = building.controllerId;
		
		//sprite = new Sprite();
		sprite = new Sprite(getTextureRegion(building.type));
		if (building.type == Building.TYPE_TEMPLE) {
		}
		else if(building.type == Building.TYPE_CASTLE) {
			sprite.setColor(sprite.getColor().r, sprite.getColor().g, sprite.getColor().b, 0);
			map.getTile(building.tileCoordinate).tile.terrain = TerrainType.CASTLE;
			map.getTile(building.tileCoordinate).setTexture(TerrainType.CASTLE);
			
			flagScale = FLAG_SCALE_CASTLE;
		}
		else if(building.type == Building.TYPE_VILLAGE) {
			sprite.setColor(sprite.getColor().r, sprite.getColor().g, sprite.getColor().b, 0);
			map.getTile(building.tileCoordinate).tile.terrain = TerrainType.VILLAGE;
			map.getTile(building.tileCoordinate).setTexture(TerrainType.VILLAGE);
			 
			flagScale = FLAG_SCALE_VILLAGE;
		}
			
		
		// Make buildings take up 90% of a tile
		float scale = (GMap.TILE_HEIGHT * 0.9f) / sprite.getHeight();
		sprite.setScale(scale);
		sprite.flip(false, true);
		
		captureBar = new ParentedGBar(sprite);
		captureBar.setDrawBarText(false);
		captureBar.setIsVertical(true);
		
		if(lastOwner == -1){
			captureBar.setColors(Player.neutralColor, Player.neutralSecondaryColor, Color.BLACK /*Player.neutralSecondaryColor*/);
		}else{
			captureBar.setColors(Player.primaryColor[lastOwner], Player.secondaryColor[lastOwner], Color.BLACK /*Player.secondaryColor[lastOwner]*/);
		}
		/*switch(lastOwner){
			case Player.PLAYER_1_ID: captureBar.setColors(Color.RED, new Color(0.3f, 0f, 0f, 1f), new Color(0.3f, 0f, 0f, 1f)); break;
			case Player.PLAYER_2_ID: captureBar.setColors(Color.CYAN, new Color(0f, 0.55f, 0.55f, 1f), new Color(0f, 0.55f, 0.55f, 1f)); break;
		}*/
		
		// Align right-center of bar to left-center of sprite
		captureBar.setPositionRelativeOrigin(-sprite.getWidth() / 2f - captureBar.getHeight() / 2f, 0f);
				
		screenPos = map.getTile(building.tileCoordinate).getCenter();
		setPosition(screenPos);
	}
	
	public static TextureRegion getTextureRegion(int type){
		switch(type){
		case Building.TYPE_TEMPLE:
			return Assets.getTextureRegion("temple");
		case Building.TYPE_CASTLE:
			return Assets.getTextureRegion("castle");
		case Building.TYPE_VILLAGE:
			return Assets.getTextureRegion("village");
		default:
			return Assets.getTextureRegion("village");
		}
	}
	
	public void render(SpriteBatch batch, float delta)
	{
		sprite.draw(batch);
		
		// Check if building has changed owner
		if(lastOwner != building.controllerId){
			lastOwner = building.controllerId;
		
			if(lastOwner == -1){
				captureBar.setColors(Player.neutralColor, Player.neutralSecondaryColor, Player.neutralSecondaryColor);
			}else{
				captureBar.setColors(Player.primaryColor[lastOwner], Player.secondaryColor[lastOwner], Player.secondaryColor[lastOwner]);
			}
		}
		
		captureBar.setValue((float)building.captureCostRemaining/(float)building.captureCost);
		captureBar.text = String.valueOf(building.captureCostRemaining);
		captureBar.render(batch);
		
		// Render flag (if the village is owned by a player)
		if(lastOwner != -1){
			flagAnimationStateTime += delta;
			
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
		captureBar.update();
		this.boundingBoxOutdated = true;
		
		if(building.type == Building.TYPE_CASTLE){
			this.flagX = sprite.getOriginX() + sprite.getX() + 25f; 
			this.flagY = sprite.getOriginY() + sprite.getY() + 8;
		}
		else if(building.type == Building.TYPE_VILLAGE){
			this.flagX = sprite.getOriginX() + sprite.getX() + 32f;
			this.flagY = sprite.getOriginY() + sprite.getY() + 14f;
		}
	}

	@Override
	protected Rectangle getBoundingRectangle() {
		return sprite.getBoundingRectangle();
	}
}
