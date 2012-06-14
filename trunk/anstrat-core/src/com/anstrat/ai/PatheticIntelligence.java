package com.anstrat.ai;

import com.anstrat.command.Command;
import com.anstrat.command.EndTurnCommand;
import com.anstrat.gameCore.State;

public class PatheticIntelligence implements IArtificialIntelligence {

	//private ActionMap actionMap;
	
	public PatheticIntelligence(){
		//State state = State.activeState;
		//actionMap = new ActionMap();
	}
	
	@Override
	public Command generateNextCommand() {
		Command bestCommand = null;


		
		if(bestCommand == null || !bestCommand.isAllowed()){ //No or invalid command found -> end turn
			bestCommand = new EndTurnCommand();
		}
		System.out.println("The AI generated a command!");
		return bestCommand;
		
	}
	
	

	
	
	
}
