package com.anstrat.command;

import com.anstrat.animation.EndTurnAnimation;
import com.anstrat.audio.AudioAssets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.GameInstanceType;
import com.anstrat.core.Main;
import com.anstrat.core.Options;
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
		GameInstance active = GameInstance.activeGame;
		
		if (!State.activeState.keyser_soze && 
				active.getGameType().equals(GameInstanceType.HOTSEAT) && !active.isAI()) {
			//AudioAssets.fadeOut();
			Main.getInstance().setScreen(HotseatNextTurnScreen.getInstance());
			//HotAnimation hanimation = new HotAnimation();
			//GEngine.getInstance().animationHandler.enqueue(hanimation);
			//GEngine.getInstance().selectionHandler.deselect();
		}
		else if(!State.activeState.keyser_soze) {
			EndTurnAnimation animation = new EndTurnAnimation();
			GEngine.getInstance().animationHandler.enqueue(animation);
			GEngine.getInstance().selectionHandler.deselect();
			if(Options.soundOn)
				AudioAssets.playTeamMusic();
		}
	}
}
