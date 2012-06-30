package com.anstrat.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.anstrat.command.AttackCommand;
import com.anstrat.command.MoveCommand;
import com.anstrat.core.Assets;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.geography.Pathfinding;
import com.anstrat.geography.Tile;
import com.anstrat.geography.TileCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Highlighter {
	
	public static final int HIGHLIGHT_OFF   = 0;
	public static final int HIGHLIGHT_TILE  = 1;
	public static final int HIGHLIGHT_ENEMY = 2;
	public static final int HIGHLIGHT_UNIT  = 3;
	public static final int BORDER_SPAWN = 10;
	public static final int BORDER_ABILITY = 11;
	
	private HashMap<TileCoordinate, Integer> highlights = new HashMap<TileCoordinate, Integer>();
	private OutlineHighlight outline;
	private OutlineHighlight rangeOutline;
	
	private Sprite unitHighlight, enemyHighlight;
	private GEngine gEngine;

	public Highlighter(GEngine gEngine){
		this.gEngine = gEngine;
		unitHighlight = new Sprite(Assets.getTextureRegion("shield"));
		unitHighlight.setScale(-1f);
		enemyHighlight = new Sprite(Assets.getTextureRegion("sword"));
		enemyHighlight.setScale(-1f);
	}
	
	/**
	 * Needs to be set last.
	 * @param coordinates
	 * @param borderType
	 */
	public void setOutline(List<TileCoordinate> coordinates, int borderType){
		Color color = Color.MAGENTA;
		boolean pulsing = false;
		switch(borderType){
		case BORDER_SPAWN:
			color = Color.GREEN;
			pulsing = true;
			break;
		case BORDER_ABILITY:
			color = Color.RED;
			pulsing = true;
		}
		outline = new OutlineHighlight(coordinates, color, pulsing);
	}
	
	public void showRange(TileCoordinate center, int range){
		Tile[][] tiles = State.activeState.map.tiles;
		List<TileCoordinate> coordinates = new ArrayList<TileCoordinate>();
		for(int i=0;i<tiles.length;i++)
		{
			for(int j=0;j<tiles[i].length;j++)
			{
				if(Pathfinding.getDistance(center, tiles[i][j].coordinates) <= range){
					coordinates.add(tiles[i][j].coordinates);
				}
			}
		}
		//TODO work in progress
		rangeOutline = new OutlineHighlight(coordinates, Color.RED, true);
		
	}
	
	/**
	 * Given tiles, highlights them with the color representing a units range/path.
	 * @param coordinates The tiles to highlight.
	 */
	public void highlightTiles(List<TileCoordinate> coordinates)
	{
		if(coordinates==null)
			return;
		clearHighlights();
		

		for(TileCoordinate tc : coordinates){
			highlights.put(tc, HIGHLIGHT_OFF);

		}
		applyHighlights();
	}
	
	/**
	 * Highlights a single tile.
	 * @param coordinate Tile to be highlighted
	 */
	public void highlightTile(TileCoordinate coordinate){
		clearHighlights();
		highlights.put(coordinate, HIGHLIGHT_UNIT);
		applyHighlights();
	}
	
	/**
	 * Applies all highlights.
	 */
	public void applyHighlights(){
		for(GTile[] row : gEngine.map.tiles)
			for(GTile tile : row){
				if(highlights.containsKey(tile.tile.coordinates)){
					if(highlights.get(tile.tile.coordinates)!=HIGHLIGHT_OFF)
						tile.setHighlight(HIGHLIGHT_TILE);
					
					switch(highlights.get(tile.tile.coordinates)){
					case HIGHLIGHT_UNIT:
						Unit unit = StateUtils.getUnitByTile(tile.tile.coordinates);
						if(unit!=null && unit.ownerId!=State.activeState.currentPlayerId)
							tile.setHighlight(HIGHLIGHT_ENEMY);
						else
							tile.setHighlight(HIGHLIGHT_UNIT);
						break;
					default:
						break;
					}
				}
				else
					tile.setHighlight(HIGHLIGHT_TILE);
			}
	}
	
	/**
	 * Returns the {@link Sprite} associated with that highlight type.
	 * @param type Only HIGHLIGHT_ENEMY or HIGHLIGHT_UNIT as of now, otherwise returns null.
	 * @return
	 */
	private Sprite getHighlightSprite(int type){
		switch(type){
		case HIGHLIGHT_ENEMY:
			return enemyHighlight;
		case HIGHLIGHT_UNIT:
			return unitHighlight;
		default:
			return null;
		}
	}
	
	/**
	 * Removes all highlights from tiles.
	 * @author Kalle 
	 */
	public void clearHighlights(){
		highlights.clear();
		for(GTile[] row : gEngine.map.tiles)
			for(GTile tile : row)
				tile.setHighlight(HIGHLIGHT_OFF);
		outline=null;
		rangeOutline = null;
	}
	/**
	 * renders items in foreground - above units.
	 * @param batch
	 */
	public void render(SpriteBatch batch){
		//Draw icons for units in range.
		/*for(TileCoordinate tile : highlights.keySet()){
			Sprite highlight = getHighlightSprite(highlights.get(tile));
			if(highlight != null){
				GTile selected = gEngine.getMap().getTile(tile);
				highlight.setPosition(selected.getPosition().x+10, selected.getPosition().y+10);
				highlight.draw(batch);
			}
		}*/
		
		
		
		// Draw cost for all actions
		if(gEngine.actionMap.isValid){
			ActionMap am = gEngine.actionMap;
			for(TileCoordinate c : am.actionTypeMap.keySet()){
				if(am.actionTypeMap.get(c) != ActionMap.ACTION_NULL){
					Vector2 pos = gEngine.map.getTile(c).getPosition();
					Assets.STANDARD_FONT.setScale(0.8f);
					Assets.STANDARD_FONT.setColor(Color.WHITE);
					//Assets.STANDARD_FONT.draw(batch, ""+am.costMap.get(c), pos.x+GMap.TILE_WIDTH*0.6f, pos.y+GMap.TILE_HEIGHT*0.6f);
				}
			}
		}
	}
	/**
	 * Renders stuff in background - above terrain but below units.
	 * @param batch
	 */
	public void renderBackground(SpriteBatch batch){
		// Draw border around selected unit
		if(gEngine.selectionHandler.selectionType == SelectionHandler.SELECTION_UNIT){
			Unit unit = gEngine.selectionHandler.selectedUnit;
			Color color;
			if(unit.ownerId == State.activeState.currentPlayerId)
				color = State.activeState.players[unit.ownerId].getColor();
			else
				color = Color.WHITE;
			gEngine.getMap().getTile(unit.tileCoordinate).renderOutline(Gdx.gl10, color, 3.5f);
		}
		
		
		if(outline != null){
			outline.render();
		}
		if(rangeOutline != null){
			rangeOutline.render();
		}
		
		if(gEngine.actionHandler.showingConfirmDialog){   // Render outline of target
			Color targetColor = Color.WHITE;
			if(gEngine.actionHandler.confirmCommand instanceof MoveCommand)
				targetColor = Color.CYAN;
			if(gEngine.actionHandler.confirmCommand instanceof AttackCommand)
				targetColor = Color.RED;
			gEngine.actionHandler.confirmTile.renderOutline(Gdx.gl10, targetColor, 6f);
		}
	}
	

}
