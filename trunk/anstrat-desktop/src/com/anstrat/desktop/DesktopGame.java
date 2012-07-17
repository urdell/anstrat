package com.anstrat.desktop;

import java.util.Arrays;

import org.lwjgl.util.Dimension;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker;
import com.badlogic.gdx.tools.imagepacker.TexturePacker.Settings;

public class DesktopGame {
	
	public enum ScreenOrientation {Portrait, Landscape};
	public static final Dimension HTC_TATTOO = new Dimension(320, 240);
	public static final Dimension GALAXY_S = new Dimension(800, 480);
	public static final Dimension GALAXY_S2 = new Dimension(800, 480);
	public static final Dimension Desktop = new Dimension(600, 360);
	public static final Dimension Xperia_S = new Dimension(1280, 720);
	
	// Testing mode settings
	public static final ScreenOrientation ORIENTATION = ScreenOrientation.Portrait;
	public static final Dimension PHONE = new Dimension(600, 400);
	
	public static void main(String[] args){
		// Pack textures windows
		//packTextures("../../anstrat-android/assets", false);	// Windows
		//packTextures("../anstrat-android/assets", false);		// Linux
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title ="Vengeful Vikings";
		config.width = ORIENTATION == ScreenOrientation.Landscape ? PHONE.getWidth() : PHONE.getHeight();
		config.height = ORIENTATION == ScreenOrientation.Landscape ? PHONE.getHeight() : PHONE.getWidth();
		config.resizable = false;
		config.samples = 2;	// Antialiasing k
		config.vSyncEnabled = true;
		
		handleArguments(args, config);
		
		new LwjglApplication(Main.getInstance(), config);
	}
	
	private static void packTextures(String assetsFolder, boolean incremental){
		Settings settings = new Settings();
        settings.padding = 0;
        settings.maxWidth = 1024;
        settings.maxHeight = 1024;
        settings.incremental = incremental;
        settings.defaultFilterMag = TextureFilter.Linear;
        settings.defaultFilterMin = TextureFilter.Linear;
        TexturePacker.process(settings, assetsFolder + "/textures", assetsFolder + "/textures_packed");
        //TexturePacker.process(settings,  "textures", "textures_packed");
        //TexturePacker.process(settings,  "../../anstrat-android/assets/textures", "../../anstrat-android/assets/textures_packed");
	}
	
	private static void handleArguments(String[] args, LwjglApplicationConfiguration config){
		
		// Used for running from a jar, enables easy switching of textures and data
		if(Arrays.binarySearch(args, "--externalTextures") >= 0){
			System.out.println("Found --externalTextures flag, running with external textures on, textures will be loaded and packed on each run.");
			packTextures(".", true);
		}
		
		// Check if fonts should be generated using freetype
		if(Arrays.binarySearch(args, "-ft") >= 0){
			System.out.println("Found -ft flag, enabling font generation using freetype.");
			Assets.USE_GENERATED_FONTS = true;
		}
		
		// Check if width or height was set using an cmdline argument
		String width = getArgumentValue("-w", args);
		if(width != null){
			try{
				config.width = Integer.parseInt(width);
				System.out.println(String.format("Found -w flag, using custom width: '%d'.", config.width));
			}
			catch(NumberFormatException e){
				System.out.println(String.format("Invalid cmdline argument value for width (-w): '%s'.", width));
			}
		}
		
		String height = getArgumentValue("-h", args);
		if(height != null){
			try{
				config.height = Integer.parseInt(height);
				System.out.println(String.format("Found -h flag, using custom height: '%d'.", config.height));
			}
			catch(NumberFormatException e){
				System.out.println(String.format("Invalid cmdline argument value for height (-h): '%s'.", height));
			}
		}
	}
	
	private static String getArgumentValue(String name, String[] args){
		try{
			for(int i = 0; i < args.length; i++){
				if(args[i].equals(name)){
					// Use the following argument as value
					return args[i + 1].trim();
				}	
			}
		}
		catch(IndexOutOfBoundsException e){
			return null;
		}
		
		return null;
	}
}