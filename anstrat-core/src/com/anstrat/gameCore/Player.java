package com.anstrat.gameCore;

import java.io.Serializable;

import com.anstrat.ai.IArtificialIntelligence;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
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
	
	private transient IArtificialIntelligence ai = null;
	
	// TODO: Proper colors for players 3-4 as well, this is only for 2 players
	public static final Color[] primaryColor = {new Color(0f, 0f, 0.6f, 1f), new Color(0.6f, 0f, 0f, 1f), 
												new Color(0f, 0f, 0f, 0f), new Color(0f, 0f, 0f, 0f)};
	public static final Color[] secondaryColor = {new Color(0f, 0f, 0.3f, 1f), new Color(0.3f, 0f, 0f, 1f),
													new Color(0f, 0f, 0f, 0f), new Color(0f, 0f, 0f, 0f)};
	
	private String displayedName;
	
	public final int playerId;
	
	public int gold = 50;
	
	public int mana = 10;
	
	public final int team;
	
	public boolean[][] visibleTiles;
	
	public Player(int playerID, String displayName, int team){
		this.playerId = playerID;
		this.displayedName = displayName;
		this.team = team;
	}
	
	public void assignAI(IArtificialIntelligence ai){
		this.ai = ai;
		this.displayedName = "AI";
	}
	
	/**
	 * @return the ai controlling this player or <code>null</code> if none
	 */
	public IArtificialIntelligence getAI(){
		return this.ai;
	}
	
	public Color getColor(){
		return primaryColor[playerId];
	}
	
	public Color getSecondaryColor(){
		return secondaryColor[playerId];
	}
	
	public String getDisplayName(){
		return this.displayedName;
	}
	
	public static int getRandomTeam(){
		return (int)(Math.random() * UnitType.TEAMS.length);
	}
	
	public static int getRandomGod() {
		return (int)(Math.random() * PlayerAbilityType.GODS.length);
	}
	
	@Override
	public boolean equals(Object object){
		if(!(object instanceof Player)) return false;
		
		Player other = (Player) object;
		return playerId == other.playerId;
	}
	
	@Override
	public int hashCode(){
		return playerId * 31;
	}
}
