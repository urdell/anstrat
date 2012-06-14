package com.anstrat.gameCore.effects;

import com.anstrat.gameCore.Unit;

public interface AffectsDefense {

	public int rangedDefIncrease(Unit unit);
	public int bluntDefIncrease(Unit unit);
	public int cutDefIncrease(Unit unit);
}
