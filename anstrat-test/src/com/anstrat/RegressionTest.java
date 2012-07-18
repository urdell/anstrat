package com.anstrat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.anstrat.core.TestGameInstance;
import com.anstrat.gameCore.TestAdjacentTileFlat;
import com.anstrat.gameCore.TestAdjacentTilePointy;
import com.anstrat.gameCore.TestUnit;
import com.anstrat.geography.TestMapFlat;
import com.anstrat.geography.TestMapPointy;

@RunWith(Suite.class)
@SuiteClasses({ 
	TestAdjacentTileFlat.class,
	TestAdjacentTilePointy.class,
	TestMapFlat.class, 
	TestMapPointy.class,
	TestUnit.class,
	TestGameInstance.class
})
public class RegressionTest {
	
}
