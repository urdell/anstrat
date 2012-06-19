package com.anstrat.gameCore;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.anstrat.TestUtil;
import com.anstrat.geography.Map;

public class TestUnit {

	private Unit unit;
	
	@Before
	public void setUp() throws Exception {
		// Create dummy state
		State.activeState = new State(new Map(10, 10), new Player[0], null);
		unit = new Unit(UnitType.AXE_THROWER, 0);
	}
	
	@Test	
	public void testSerialization() throws FileNotFoundException, IOException, ClassNotFoundException{
		TestUtil.writeObject(unit, "unit.bin");
		Unit dUnit = (Unit) TestUtil.readObject("unit.bin");
		assertTrue(isUnitEqual(unit, dUnit));
		
		new File("unit.bin").delete();
	}
	
	private boolean isUnitEqual(Unit a, Unit b){
		return a.getAPCostAttack() == b.getAPCostAttack() &&
				a.getAPReg() == b.getAPReg() &&
				a.getAttack() == b.getAttack() &&
				a.getMaxAP() == b.getMaxAP() &&
				a.getMaxAttackRange() == b.getMaxAttackRange() &&
				a.getMaxHP() == b.getMaxHP() &&
				a.getMinAttackRange() == b.getMinAttackRange() &&
				a.getName().equals(b.getName()) &&
				a.getUnitType() == b.getUnitType();
	}
}
