package com.anstrat.desktop;

import java.io.File;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class TexturePacker {

	public static void main(String[] args) {
		pack();
	}
	
	public static void pack(){
		File workingDirectory = new File(System.getProperty("user.dir"));
		
		// Some OS:s run programs from the bin folder when run through Eclipse
		File projectRoot = workingDirectory.getName().equals("bin") ? workingDirectory.getParentFile() : workingDirectory;
		String androidRoot = projectRoot.getParentFile().getPath() + "/anstrat-android";
		
		Settings settings = new Settings();
		settings.maxHeight = 2048;
		settings.maxWidth = 2048;
		settings.filterMag = Texture.TextureFilter.Linear;
		settings.filterMin = Texture.TextureFilter.Linear;
		settings.paddingX = 2;
		settings.paddingY = 2;
		settings.flattenPaths = true;
		
		// Pack loading screen textures
		TexturePacker2.process(
				settings,
				androidRoot + "/graphics/loadingscreen",
				androidRoot + "/assets/textures-loadingscreen",
				"pack");
		
		// Pack rest of the textures
		TexturePacker2.process(
				settings,
				androidRoot + "/graphics/textures",
				androidRoot + "/assets/textures",
				"pack");
	}
}
