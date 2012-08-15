package com.anstrat.command;

import com.anstrat.animation.Animation;
import com.anstrat.animation.FullscreenTextAnimation;
import com.anstrat.core.GameInstance;
import com.anstrat.geography.Pathfinding;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;

public final class CommandHandler {

	/**
	 * Will only be called from the gui on the local client
	 * @param command
	 */
	public static void execute(Command command){
		if(command.isAllowed()){			
			Gdx.app.log("CommandHandler", String.format("Executing valid '%s' command.", command));
			GameInstance.activeGame.onCommandExecute(command);
			command.execute();
			Pathfinding.ranges.clear();
		}
		else{
			Gdx.app.log("CommandHandler", String.format("Attempted to execute an invalid '%s' command. Disallowed.", command));
			
			if (command instanceof CreateUnitCommand) {
				Animation animation = new FullscreenTextAnimation(((CreateUnitCommand) command).getReason());
				GEngine.getInstance().animationHandler.runParalell(animation);
			}
		}
	}
	
	/**
	 * Will only be called when an incoming network turn is received
	 * @param turn
	 */
	public static void executeNetwork(Command command){
		if(command.isAllowed()){
			command.execute();
			
			Gdx.app.log("CommandHandler", String.format("Got a valid '%s' command from the server.", command));
		}
		else
			Gdx.app.log("CommandHandler", String.format("Got a invalid '%s' command from the server!", command));	
	}
}
