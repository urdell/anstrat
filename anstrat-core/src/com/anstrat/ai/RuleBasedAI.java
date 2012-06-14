package com.anstrat.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.anstrat.command.Command;

public class RuleBasedAI implements IArtificialIntelligence{

	public boolean printAIMessages = true;
	
	public AIKnowledge knowledge;
	
	public Random random;
	
	public List<Rule> rules;
	public Rule emergencyRule = new EndTurnRule();   //  If all fails, use this rule.
	
	public RuleBasedAI(){
		random = new Random();
		knowledge = new AIKnowledge();
		rules = new ArrayList<Rule>();
		rules.add(new MoveToClosestRule());
		rules.add(new EndTurnRule());
	}
	
	@Override
	public Command generateNextCommand() {
		knowledge.prepare();
		emergencyRule.prepare(knowledge);
		
		int totalValue = 0;
		int bestValue = 0;
		for(Rule r : rules){    // prepare and sum the total value of all rules.
			r.prepare(knowledge);
			if(printAIMessages) System.out.println("AI: " + r.getClass().getName() + " has value " + r.value);
			totalValue += r.value;
			if(r.value > bestValue)
				bestValue = r.value;
		}
		
		Command chosenCommand;
		if(totalValue == 0){    // no good rule, go with emergency rule
			if(printAIMessages) System.out.println("AI: WARNING: no rule is good enough to perform");
			chosenCommand = emergencyRule.command;
			
		}else{				// there are good rules, select one randomly considering value
			int randomValue = Math.abs( random.nextInt() % totalValue )  ;   // range 0 ~~ totalValue-1
			int ruleIndex = -1;  // first one checked will be rule 0
			do{
				ruleIndex++;
				randomValue -= rules.get(ruleIndex).value;
			}while(randomValue >= 0 && ruleIndex+1 < rules.size());  // at last rule, randomValue is at most  (totalValue-1) - totalValue  =  -1
																	//for safety, added check so that next ruleIndex is within array bounds.
			if(printAIMessages) System.out.println("AI: Chose rule " + rules.get(ruleIndex).getClass().getName());
			chosenCommand = rules.get(ruleIndex).command;
		}
		
		if(chosenCommand == null){
			System.out.println("ERROR: AI generated a NULL command");   // should NEVER happen
			return emergencyRule.command;
		}
		if(!chosenCommand.isAllowed()){
			System.out.println("ERROR: AI generated an invalid command");   // should NEVER happen
			return emergencyRule.command;
		}
		return chosenCommand;
	}

}
