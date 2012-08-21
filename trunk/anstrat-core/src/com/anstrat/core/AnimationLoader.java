package com.anstrat.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class AnimationLoader {
	private Map<String, Animation> animations;
	private Map<String, String> alias;
	
	public AnimationLoader(FileHandle file, TextureAtlas atlas){
		animations = new HashMap<String, Animation>();
		alias = new HashMap<String, String>();
		loadAnimationsFromFile(file, atlas);
	}
	
	public boolean animationExists(String name){
		return animations.containsKey(name);
	}
	
	public Animation getAnimation(String name){
		String animationName = !animations.containsKey(name) ? alias.get(name) : name; 
		Animation animation = animations.get(animationName);
		
		if(animation == null){
			throw new IllegalArgumentException(String.format("The animation named '%s' does not exist.", animationName));
		}
		
		return animation;
	}
	
	private void loadAnimationsFromFile(FileHandle file, TextureAtlas atlas){
		try {
			Element root = new XmlReader().parse(file);
			int defaultFPS = root.getInt("fps", 6);
			int animationCount = root.getChildCount();
			
			for (int i = 0; i < animationCount; i++) {
				loadAnimation(root.getChild(i), defaultFPS, atlas);
			}
			
			Gdx.app.log("AnimationLoader", String.format("Successfully loaded %d animations from '%s'.", animationCount, file));
			
		} catch (IOException e) {
			throw new IllegalArgumentException(String.format("Failed to parse animations from '%s'.", file.name()), e);
		}
	}
	
	private void loadAnimation(Element element, int defaultFPS, TextureAtlas atlas){
		int fps = element.getInt("fps", defaultFPS);
		String animationName = element.get("name");
		String aliasof = element.getAttribute("aliasof", null);
		
		if(aliasof != null){
			// This animation is just another name for another animation
			alias.put(animationName, aliasof);
			return;
		}
		
		// Parse frames
		ArrayList<TextureRegion> keyFrames = new ArrayList<TextureRegion>();
		
		for(int i = 0; i < element.getChildCount(); i++){
			Element frame = element.getChild(i);
			String frameImageName = frame.get("image");
			
			TextureRegion atlasRegion = atlas.findRegion(frameImageName);
			if(atlasRegion == null) throw new IllegalArgumentException(String.format("Couldn't find frame image '%s'.", frameImageName));
			
			// Create new region so that it can flipped etc without affecting the texture region in the atlas
			TextureRegion frameImage = new TextureRegion(atlasRegion);
			frameImage.flip(false, true);
			
			// Add frames
			for(int j = 0; j < frame.getInt("count", 1); j++){
				keyFrames.add(frameImage);
			}
		}
		
		animations.put(animationName, new Animation(1f / fps, keyFrames.toArray(new TextureRegion[keyFrames.size()])));
	}
}
