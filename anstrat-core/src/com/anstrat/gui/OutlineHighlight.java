package com.anstrat.gui;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.geography.Map;
import com.anstrat.geography.TileCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;

public class OutlineHighlight {
	
	private List<BorderHighlight> borderHighlights = new ArrayList<BorderHighlight>();
	private Color color;
	private float minR, minG, minB;
	private boolean pulsing = false;
	private float timePassed=0f;
	
	
	public OutlineHighlight(List<TileCoordinate> coordinates, Color c, boolean pulsing){
		minR = c.r;
		minG = c.g;
		minB = c.b;
		this.color = new Color(c.r, c.g, c.b, c.a);
		this.pulsing = pulsing;
		GMap map = GEngine.getInstance().map;
		for(TileCoordinate coordinate : coordinates){
			GTile gTile = map.getTile(coordinate);
			BorderHighlight bh = new BorderHighlight(gTile);
			borderHighlights.add(bh);
			
			for(TileCoordinate other : coordinates){
				int orientation = map.map.getAdjacentOrientation(coordinate, other);
				if(orientation != Map.NOT_ADJACENT){ // is adjacent
					bh.sb[orientation] = false; // disable border between the tiles.
				}
			}
		}
	}

	public void render(){
		GL10 gl = Gdx.gl10;
		if(pulsing){
			timePassed += Gdx.graphics.getDeltaTime();
			float sinValue = Math.abs((float)Math.sin(timePassed*3));
			color.r = minR+(1-minR)*sinValue;
			color.g = minG+(1-minG)*sinValue;
			color.b = minB+(1-minB)*sinValue;
		}
		
		for(BorderHighlight bh : borderHighlights){
			bh.render(gl, color);
		}
	}
	
	private static class BorderHighlight{
		private GTile gTile;
		/** SW, S, SE, NE, N, NW */
		public boolean[] sb = {true, true, true, true, true, true}; //shown borders. 
		public BorderHighlight(GTile gTile){
			this.gTile = gTile;
		}
		public void render(GL10 gl, Color c){
			for(int edgeId = 0; edgeId < 6; edgeId++){
				if(sb[edgeId])
					gTile.renderEdgeOutline(edgeId, gl, c, 4f);
			}
		}
	}
}


