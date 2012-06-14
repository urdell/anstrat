package com.anstrat.ai;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.ActionMap;

/**
 * Gathers general information that the AI can use in its decision making. 
 * @author Anton
 *
 */
public class AIKnowledge {
	
	
	public Unit consideredUnit;
	public ActionMap consideredActionMap;
	
	public List<Unit> enemyUnits = new ArrayList<Unit>();
	public List<Unit> friendlyUnits = new ArrayList<Unit>();
	

	public AIKnowledge(){
		consideredActionMap = new ActionMap();
	}
	/**
	 * prepare the knowledge to be used.
	 */
	public void prepare(){
		
		consideredUnit = AIUtils.unitWithMostAP();
		consideredActionMap.prepare(consideredUnit);
		enemyUnits.clear();
		friendlyUnits.clear();
		for(Unit u : State.activeState.unitList.values()){
			if(u.ownerId == State.activeState.currentPlayerId){
				friendlyUnits.add(u);
			}else{
				enemyUnits.add(u);
			}
		}
		
	}
	
	
}
