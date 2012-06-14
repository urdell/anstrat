package com.anstrat.ai;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.geography.Map;

public class TestBasicAI {

	private State state;
	
	@Before
	public void setUp() throws Exception {
		state = new State(new Map(10, 10), new Player[]{new Player(0, 0, "Player")}, null);
		State.activeState = state;
		for(Player p : state.players){
			AIUtils.assignAI(p, new ScriptAI());
		}
	}
	
	@After
	public void tearDown() throws Exception {
		
	}
	
	@Test
	public void testAIAssigned(){
		assertTrue(state.getCurrentPlayer().ai != null);
	}
	
	@Test
	public void testGeneratesValidCommand(){
		state = State.activeState;
		assertTrue(state.getCurrentPlayer().ai.generateNextCommand().isAllowed());
	}
	
	@Test
	public void testGeneratesSecondCommand(){
		state = State.activeState;
		assertTrue(state.getCurrentPlayer().ai.generateNextCommand().isAllowed());
	}
	
	
}
