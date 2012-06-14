package com.anstrat.ai;

import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.gameCore.State;

public class AIRunner {
	
	final static float TIME_BETWEEN_ACTIONS = 0.5f;
	
	static float timeToNextAction = TIME_BETWEEN_ACTIONS;

	public static void run(float deltaTime){
		
		if(State.activeState != null &&
				State.activeState.getCurrentPlayer().ai != null){
			timeToNextAction -= deltaTime;
			
			if(timeToNextAction <= 0){
				Command command = State.activeState.getCurrentPlayer().ai.generateNextCommand();
				CommandHandler.execute(command);
				timeToNextAction = TIME_BETWEEN_ACTIONS;
			}
			
		}
		
	}
	
	
	
}
