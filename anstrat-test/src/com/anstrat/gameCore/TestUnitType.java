package com.anstrat.gameCore;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.badlogic.gdx.files.FileHandle;

public class TestUnitType {

	@Test
	public void testLoadUnitAttributesFromFile() {
		UnitType.loadAttributesFromFile(new FileHandle("testUnits.xml"));
		UnitType t = UnitType.AXE_THROWER;
		
		assertEquals(20, t.maxHP);
		//assertEquals(5, t.HPReg);   //no definition of hp reg
		assertEquals(6, t.attack);
		assertEquals(1, t.minAttackRange);
		assertEquals(1, t.maxAttackRange);
		assertEquals(7, t.maxAP);
		assertEquals(4, t.APReg);
		assertEquals(2, t.APCostAttacking);
	}
}
