package com.anstrat.command;

import com.anstrat.animation.EndTurnAnimation;
import com.anstrat.gameCore.State;
import com.anstrat.gui.GEngine;

public class EndTurnCommand extends Command{
	
	private static final long serialVersionUID = 2L;
	
	@Override
	protected void execute() {
		State.activeState.endTurn();
		
		EndTurnAnimation animation = new EndTurnAnimation();
		GEngine.getInstance().animationHandler.enqueue(animation);
		GEngine.getInstance().selectionHandler.deselect();
	}
}
