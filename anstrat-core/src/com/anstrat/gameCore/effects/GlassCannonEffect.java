package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public class GlassCannonEffect extends Effect implements DamageModifier, DamageTakenModifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public float damageModification(Unit unit) {
		return 1.33f;
	}

	@Override
	public float damageTakenModification(Unit unit) {
		// TODO Auto-generated method stub
		return 0;
	}

}
