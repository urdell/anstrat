package com.anstrat.gameCore.playerAbilities;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public enum PlayerAbilityType {
	SMITE("Smite");
	
	public static final int ATTACK_TYPE_RANGED = 0;
	public static final int ATTACK_TYPE_BLUNT = 1;
	public static final int ATTACK_TYPE_CUT = 2;
	
	public transient final String name;
	public transient int manaCost;
	public transient String description;
	
	public transient String graphicsFolder;
	
	public String idleImage;
	
	private PlayerAbilityType(String name){
		this.name = name;
		
		// Set defaults
		this.graphicsFolder = "swordsman";
		this.manaCost = 20;
		this.description = "This should not be here";
	}	
	
	public static void loadAttributesFromFile(FileHandle file){
		try {
			Element root = new XmlReader().parse(file);
		
			for (int i = 0; i < root.getChildCount(); i++) {
				loadPlayerAbilityType(root.getChild(i));
			}
		} catch (IOException e) {
			throw new GdxRuntimeException(String.format("Failed to parse player ability types from '%s'", file.name()), e);
		}
	}
	
	private static void loadPlayerAbilityType(Element element){
		String name = element.get("name");
		PlayerAbilityType type = getPlayerAbilityTypeByName(name);
		
		// Notify if we're trying to set attributes for a non-existing unit type
		if(type == null){ 
			Gdx.app.error("UnitType", String.format("Warning: Found attributes for non-existing unit type '%s'.", name));
			return;
		}
		
		// Read attribute values from either xml attributes or elements
		// Default values are used for missing values
		//type.graphicsFolder = element.get("imageFolder", type.graphicsFolder);
		type.manaCost = element.getInt("manacost", type.manaCost);
		type.description = element.get("description", type.description);
		//type.idleImage = element.get("idleImage", type.idleImage);
		
		Gdx.app.log("PlayerAbilityType", String.format("Loaded attributes for '%s'.", name));
	}
	
	public static PlayerAbilityType getPlayerAbilityTypeByName(String name){
		for(PlayerAbilityType type : PlayerAbilityType.values()){
			if(type.name.equals(name)) return type;
		}
		
		return null;
	}
}
