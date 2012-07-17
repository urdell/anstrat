package com.anstrat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.anstrat.core.TestGameInstance;
import com.anstrat.gameCore.TestAdjecantAndOrientation;
import com.anstrat.gameCore.TestUnit;
import com.anstrat.geography.TestMap;

@RunWith(Suite.class)
@SuiteClasses({ 
	TestAdjecantAndOrientation.class,
	TestMap.class, 
	TestUnit.class,
	TestGameInstance.class
})
public class RegressionTest {
	
}
