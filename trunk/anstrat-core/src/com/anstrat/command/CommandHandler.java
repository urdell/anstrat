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
		System.out.println("Executig cometstrike");
		if(command.isAllowed()){			
			Gdx.app.log("CommandHandler", String.format("Executing valid '%s' command.", command));
			GameInstance.activeGame.onCommandExecute(command);
			command.execute();
			System.out.println("CometStriking");
			Pathfinding.ranges.clear();
		}
		else{
			/*
			 * Code not used at the moment, at least not for TargetPlayerAbilities
			 * to change error message, this code is instead implemented in Click in ActionHandler
			 * 
			 */
			System.out.println("CometStrike Not Valid");
			Gdx.app.log("CommandHandler", String.format("Attempted to execute an invalid '%s' command. Disallowed.", command));
			
			if (command instanceof CreateUnitCommand) {
				Animation animation = new FullscreenTextAnimation(((CreateUnitCommand) command).getReason());
				GEngine.getInstance().animationHandler.runParalell(animation);
			}
			else if (command instanceof ActivateTargetedPlayerAbilityCommand) {
				System.out.println(command.toString());
				Animation animation = new FullscreenTextAnimation(((ActivateTargetedPlayerAbilityCommand) command).getReason());
				GEngine.getInstance().animationHandler.runParalell(animation);
			}
			else if ( command instanceof ActivateDoubleTargetedPlayerAbilityCommand) {
				System.out.println(command.toString());
				Animation animation = new FullscreenTextAnimation(((ActivateDoubleTargetedPlayerAbilityCommand) command).getReason());
				GEngine.getInstance().animationHandler.runParalell(animation);
			}
			else if ( command instanceof ActivatePlayerAbilityCommand) {
				System.out.println(command.toString());
				Animation animation = new FullscreenTextAnimation(((ActivatePlayerAbilityCommand) command).getReason());
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
