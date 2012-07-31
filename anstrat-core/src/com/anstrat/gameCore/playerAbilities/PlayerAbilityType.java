package com.anstrat.gameCore.playerAbilities;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public enum PlayerAbilityType {
	THUNDERBOLT("Thunderbolt"),
	REMOVE_EFFECTS("Remove Effects"),
	COMETSTRIKE("Comet Strike"),
	SWAP("Swap"),
	ZOMBIFY("Zombify"),
	THORS_RAGE("Thor's Rage"),
	ODINS_BLESSING("Odin's Blessing"),
	HELS_CURSE("Hel's Curse"),
	FREEZE("Freeze"),
	CONFUSION("Confusion");
	
	public transient final String name;
	public transient int manaCost;
	public transient String description;
	
	public static final int GOD_THOR = 0, GOD_ODIN = 1, GOD_HEL = 2, GOD_LOKI = 3;
	public transient int god;
	
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
				int god = getGodByName(root.getChild(i).get("god"));
				
				for(int y = 0; y < root.getChild(i).getChildCount(); y++) {
					loadPlayerAbilityType(root.getChild(i).getChild(y), god);
				}
			}
		} catch (IOException e) {
			throw new GdxRuntimeException(String.format("Failed to parse player ability types from '%s'", file.name()), e);
		}
	}
	
	private static void loadPlayerAbilityType(Element element, int god){
		String name = element.get("name");
		PlayerAbilityType type = getPlayerAbilityTypeByName(name);
		
		// Notify if we're trying to set attributes for a non-existing unit type
		if(type == null){ 
			Gdx.app.error("PlayerAbilityType", String.format("Warning: Found attributes for non-existing player ability type '%s'.", name));
			return;
		}
		
		// Read attribute values from either xml attributes or elements
		// Default values are used for missing values
		//type.graphicsFolder = element.get("imageFolder", type.graphicsFolder);
		type.manaCost = element.getInt("manacost", type.manaCost);
		type.description = element.get("description", type.description);
		type.god = god;
		//type.idleImage = element.get("idleImage", type.idleImage);
		
		Gdx.app.log("PlayerAbilityType", String.format("Loaded attributes for '%s'.", name));
	}
	
	public static PlayerAbilityType getPlayerAbilityTypeByName(String name){
		for(PlayerAbilityType type : PlayerAbilityType.values()){
			if(type.name.equals(name)) return type;
		}
		
		return null;
	}
	
	/**
	 * Returns the integer associated with the god with name. If no such god was found returns -1.
	 * @param name the name of the god
	 * @return the integer associated with the god
	 */
	public static int getGodByName(String name) {
		if(name.equals("Thor")) {
			return GOD_THOR;
		}
		else if (name.equals("Odin")) {
			return GOD_ODIN;
		}
		else if (name.equals("Hel")) {
			return GOD_HEL;
		}
		else if (name.equals("Loki")) {
			return GOD_LOKI;
		}
		else {
			Gdx.app.error("PlayerAbilityType", "Error: God "+name+" does not exist");
			return -1;
		}
	}
	
	/**
	 * Returns all player abilities associated with god. If god was not found returns null
	 * @param god
	 * @return
	 */
	public static PlayerAbilityType[] getAbilitiesFromGod(int god) {
		switch(god) {
		case GOD_THOR:
			return new PlayerAbilityType[]{THORS_RAGE, THUNDERBOLT, COMETSTRIKE};
		case GOD_ODIN:
			return new PlayerAbilityType[]{REMOVE_EFFECTS, ODINS_BLESSING};
		case GOD_HEL:
			return new PlayerAbilityType[]{HELS_CURSE, CONFUSION, ZOMBIFY};
		case GOD_LOKI:
			return new PlayerAbilityType[]{SWAP, FREEZE};
		}
		return null;
	}
}
