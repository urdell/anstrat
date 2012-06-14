package com.anstrat.ai;

import com.anstrat.command.Command;

public interface IArtificialIntelligence {
	
	
	
	/**
	 * Will use the active state to generate a command. Must always generate a valid command. 
	 * Eventually this method will return an EndTurn command.
	 * @return
	 */
	public Command generateNextCommand();
	
	

}
