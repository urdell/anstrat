package com.anstrat.gameCore;

import java.io.Serializable;

import com.anstrat.ai.IArtificialIntelligence;
import com.badlogic.gdx.graphics.Color;

/**
 * 
 * Contains info about resources etc.
 *
 */
public class Player implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final int PLAYER_1_ID = 0;
	public static final int PLAYER_2_ID = 1;
	public static final int PLAYER_3_ID = 2;
	public static final int PLAYER_4_ID = 3;

	public static Color neutralColor = Color.GRAY;
	public static Color neutralSecondaryColor = Color.DARK_GRAY;
	
	public transient IArtificialIntelligence ai = null;
	
	// TODO: Proper colors for players 3-4 as well, this is only for 2 players
	public static final Color[] primaryColor = {new Color(0.2f, 0.2f, 1f, 1f), new Color(1f, 0.15f, 0.15f, 1f), 
												new Color(0f, 0f, 0f, 0f), new Color(0f, 0f, 0f, 0f)};
	public static final Color[] secondaryColor = {new Color(0f, 0f, 0.3f, 1f), new Color(0.3f, 0f, 0f, 1f),
													new Color(0f, 0f, 0f, 0f), new Color(0f, 0f, 0f, 0f)};
	
	public final int team;
	public String displayedName;
	
	public final int playerId;
	
	public int gold = 50;
	
	public int mana = 10;
	
	public final int god;
	
	public boolean[][] visibleTiles;
	
	public Player(int playerID, String displayName, int team, int god){
		this.playerId = playerID;
		this.displayedName = displayName;
		this.team = team;
		this.god = god;
	}
	
	public Color getColor(){
		return primaryColor[playerId];
	}
	public Color getSecondaryColor(){
		return secondaryColor[playerId];
	}
	public static int getRandomGodFromTeam(int team) {
		int random = (int)(Math.random()*2+2*team);
		return random;
	}
}
