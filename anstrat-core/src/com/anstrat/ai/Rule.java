package com.anstrat.ai;

import com.anstrat.command.Command;

public abstract class Rule {
	
	public Command command;
	/** value of performing the rule. Range from 0 (will never perform) to 1000 (Is unarguably the best choice)
	 * 0 - never perform
	 * 10 - stupid to do
	 * 50 - equal value to ending your turn
	 * 150 - decent action (move towards enemy)
	 * 200 - good option (move towards enemy, having enough ap to attack after)
	 * 300 - Definitely good (attack low health opponent)
	 * 
	 * */
	public int value;
	
	
	/**
	 * Sets command to a valid executable command. May be null if it fails.
	 * Sets the value of performing this function. If no valid command can be generated with the rule, the result is 0
	 */
	public abstract void prepare(AIKnowledge knowledge);


}
