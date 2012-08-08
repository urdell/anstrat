package com.anstrat.ai;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;

public class AIUtils {
	/**
	 * 
	 * @return a unit that the current player has control over, and which has the most ap.
	 */
	public static Unit unitWithMostAP(){
		int mostAPFound = -1;
		Unit foundUnit = null;
		for(Unit u : State.activeState.unitList.values()){
			if(u.ownerId == State.activeState.currentPlayerId && u.currentAP > mostAPFound){
				mostAPFound = u.currentAP;
				foundUnit = u;
			}
		}
		return foundUnit;
	}
}
