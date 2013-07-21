package com.anstrat.ai;

import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.gameCore.State;

public class AIRunner {
	
	final static float TIME_BETWEEN_ACTIONS = 2.5f;
	
	static float timeToNextAction = TIME_BETWEEN_ACTIONS;

	public static void run(float deltaTime){
		
		if(State.activeState != null &&
				State.activeState.getCurrentPlayer().getAI() != null){
			timeToNextAction -= deltaTime;
			
			if(timeToNextAction <= 0){
				Command command = State.activeState.getCurrentPlayer().getAI().generateNextCommand();
				CommandHandler.execute(command);
				timeToNextAction = TIME_BETWEEN_ACTIONS;
			}
			
		}
		
	}
	
	
	
}
