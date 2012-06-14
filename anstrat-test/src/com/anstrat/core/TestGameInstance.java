package com.anstrat.core;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.anstrat.TestUtil;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;

public class TestGameInstance {

	private GameInstance instance;
	
	@Before
	public void setUp() throws Exception {
		instance = new GameInstance(0, new Map(10, 10), new Player[0]);
	}
	
	@After
	public void tearDown() throws Exception {
		new File("game.bin").delete();
	}

	@Test
	public void testSerializationNoErrors() throws FileNotFoundException, IOException, ClassNotFoundException {
		TestUtil.writeObject(instance, "game.bin");
		assertTrue(TestUtil.readObject("game.bin") instanceof GameInstance);
	}

	// TODO: Add tests that actually check that all fields are serialized correctly
}
