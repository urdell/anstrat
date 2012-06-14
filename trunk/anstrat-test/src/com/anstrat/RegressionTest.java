package com.anstrat;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.anstrat.ai.TestBasicAI;
import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.core.TestGameInstance;
import com.anstrat.gameCore.TestAdjecantAndOrientation;
import com.anstrat.gameCore.TestUnit;
import com.anstrat.gameCore.TestUnitType;
import com.anstrat.geography.TestMap;
import com.anstrat.gui.TestGMap;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.jogl.JoglApplication;
import com.badlogic.gdx.backends.jogl.JoglApplicationConfiguration;

@RunWith(Suite.class)
@SuiteClasses({ 
	TestMap.class, 
	TestGMap.class,
	TestUnit.class,
	TestUnitType.class,
	TestGameInstance.class,
	TestBasicAI.class,
	TestAdjecantAndOrientation.class
})
public class RegressionTest {
	
	private static CountDownLatch lock = new CountDownLatch(1);
	
	@BeforeClass
	public static void init() throws InterruptedException, InvocationTargetException{
		Assets.USE_GENERATED_FONTS = false;
		
		Gdx.app = new JoglApplication(new ApplicationListener() {
			
			@Override
			public void resume() {
				Main.getInstance().resume();
			}
			
			@Override
			public void resize(int width, int height) {
				Main.getInstance().resize(width, height);
			}
			
			@Override
			public void render() {
				Main.getInstance().render();
			}
			
			@Override
			public void pause() {
				Main.getInstance().pause();
			}
			
			@Override
			public void dispose() {
				Main.getInstance().dispose();
			}
			
			@Override
			public void create() {
				Main.getInstance().create();
				lock.countDown();
			}
		}, new JoglApplicationConfiguration());
		
		// Wait for program to initialize
		lock.await();
	}
	
	@AfterClass
	public static void dispose(){
		Gdx.app.exit();
	}
	
	public static void main(String[] args) throws InterruptedException{
		Assets.USE_GENERATED_FONTS = false;
		new JoglApplication(Main.getInstance(), new JoglApplicationConfiguration());
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				System.exit(0);
			}
		});
	}
}
