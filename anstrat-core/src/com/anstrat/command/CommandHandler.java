package com.anstrat.command;

import com.anstrat.animation.Animation;
import com.anstrat.animation.UberTextAnimation;
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
			
			String reason = "";
			
			if (command instanceof CreateUnitCommand)
				reason = ((CreateUnitCommand) command).getReason();
			else if (command instanceof ActivateTargetedPlayerAbilityCommand)
				reason = ((ActivateTargetedPlayerAbilityCommand) command).getReason();
			else if ( command instanceof ActivateDoubleTargetedPlayerAbilityCommand)
				reason = ((ActivateDoubleTargetedPlayerAbilityCommand) command).getReason();
			else if ( command instanceof ActivatePlayerAbilityCommand)
				reason = ((ActivatePlayerAbilityCommand) command).getReason();
			
			Animation animation = null;
			
			System.out.println("Failed due to: " + reason);
			
			if(reason.equals("toofar"))
				animation = new UberTextAnimation("toofar");
			else
				animation = new UberTextAnimation("notpossible");
			
			if(animation!=null)
				GEngine.getInstance().animationHandler.runParalell(animation);
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
