package com.anstrat.command;

import com.anstrat.animation.EndTurnAnimation;
import com.anstrat.core.GameInstance;
import com.anstrat.core.GameInstanceType;
import com.anstrat.core.Main;
import com.anstrat.gameCore.State;
import com.anstrat.gui.GEngine;
import com.anstrat.menu.HotseatNextTurnScreen;

public class EndTurnCommand extends Command{
	
	private static final long serialVersionUID = 2L;
	
	public EndTurnCommand(){}
	
	public EndTurnCommand(int playerIndex){
		super(playerIndex);
	}
	
	@Override
	protected void execute() {
		State.activeState.endTurn();
		
		if (GameInstance.activeGame.getGameType().equals(GameInstanceType.HOTSEAT)) {
			Main.getInstance().setScreen(HotseatNextTurnScreen.getInstance());
			//HotAnimation hanimation = new HotAnimation();
			//GEngine.getInstance().animationHandler.enqueue(hanimation);
			//GEngine.getInstance().selectionHandler.deselect();
		}
		else {
			EndTurnAnimation animation = new EndTurnAnimation();
			GEngine.getInstance().animationHandler.enqueue(animation);
			GEngine.getInstance().selectionHandler.deselect();
		}
	}
}
