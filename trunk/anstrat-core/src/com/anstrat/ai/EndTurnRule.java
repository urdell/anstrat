package com.anstrat.ai;

import com.anstrat.command.EndTurnCommand;

public class EndTurnRule extends Rule {

	@Override
	public void prepare(AIKnowledge knowledge) {

		command = new EndTurnCommand();
		value = 50; // value equal to ending your turn.
	}


}
