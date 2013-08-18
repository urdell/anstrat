package com.anstrat.gameCore;

import java.io.IOException;

import com.anstrat.geography.TerrainType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * Describes a unit type
 * 
 * @author eriter
 *
 */
public enum UnitType {	
	BERSERKER("Berserker"), 
	SHAMAN("Shaman"), 
	AXE_THROWER("Axe Thrower"),
	SWORD("Swordsman"),
	WOLF("Wolf"),
	HAWK("Hawk"),
	VALKYRIE("Valkyrie"),
	FALLEN_WARRIOR("Fallen Warrior"),
	DARK_ELF("Dark Elf"),
	GOBLIN_SHAMAN("Goblin Shaman"),
	JOTUN("Jotun"),
	TROLL("Troll");
	
	public static UnitType[][] TEAMS = {
		{BERSERKER, SHAMAN, AXE_THROWER, SWORD, WOLF, HAWK},
		{VALKYRIE, FALLEN_WARRIOR, DARK_ELF, GOBLIN_SHAMAN, JOTUN, TROLL},
	};
	
	public transient final String name;
	
	public transient String graphicsFolder;
	public transient String swingSfx;
	public transient String impactSfx;
	
	public String idleImage;
	public String portrait;
	public String nameLabel;
	public float movementSpeed;
	
	// HP
	public transient int maxHP;
	public transient int HPReg;

	// Attack
	public int attack;
	public int minAttackRange;
	public int maxAttackRange;
	
	// AP
	public transient int maxAP;
	public transient int APReg;
	public transient int APStart;
	public transient int APCostAttacking;
	
	public transient int cost;
	
	public transient int ability1Id = 0;
	public transient int ability2Id = 0;
	public transient int effect1Id = 0;
	public transient int effect2Id = 0;
	
	public transient String description;
	
	// Terrain type penalties
	public transient final int[] terrainTypePenalties;

	
	
	private UnitType(String name){
		this.name = name;
		
		// Set defaults
		this.graphicsFolder = "swordsman";
		this.maxHP = 20;
		this.HPReg = 1;
		this.attack = 6;
		this.maxAP = 9;
		this.minAttackRange = 1;
		this.maxAttackRange = 1;
		this.APCostAttacking = 2;
		this.cost = 20;
		this.description = "This should not be here";
		
		// Default terrain type penalties
		terrainTypePenalties = new int[TerrainType.values().length];
		for(TerrainType t : TerrainType.values())
			terrainTypePenalties[t.ordinal()] = t.penalty;
	}	
	
	/**
	 * @return the terrain penalty or {@link Integer.MAX_VALUE} if this UnitType cannot enter the given terrain.
	 */
	public int getTerrainPenalty(TerrainType type){
		return terrainTypePenalties[type.ordinal()];
	}
	
	public static void loadAttributesFromFile(FileHandle file){
		try {
			Element root = new XmlReader().parse(file);
		
			for (int i = 0; i < root.getChildCount(); i++) {
				loadUnitType(root.getChild(i));
			}
		} catch (IOException e) {
			throw new GdxRuntimeException(String.format("Failed to parse unit types from '%s'", file.name()), e);
		}
	}
	
	private static void loadUnitType(Element element){
		String name = element.get("name");
		UnitType type = getUnitTypeByName(name);
		
		// Notify if we're trying to set attributes for a non-existing unit type
		if(type == null){ 
			Gdx.app.error("UnitType", String.format("Warning: Found attributes for non-existing unit type '%s'.", name));
			return;
		}
		
		// Read attribute values from either xml attributes or elements
		// Default values are used for missing values
		type.graphicsFolder = element.get("imageFolder", type.graphicsFolder);
		type.maxHP = element.getInt("maxHP", type.maxHP);
		type.HPReg = element.getInt("HPReg", type.HPReg);
		type.attack = element.getInt("attack", type.attack);
		type.minAttackRange = element.getInt("minAttackRange", type.minAttackRange);
		type.maxAttackRange = element.getInt("maxAttackRange", type.maxAttackRange);
		type.maxAP = element.getInt("maxAP", type.maxAP);
		type.APReg = element.getInt("APReg", type.APReg);
		type.APStart = element.getInt("APStart", type.APStart);
		type.APCostAttacking = element.getInt("APCostAttacking", type.APCostAttacking);
		type.movementSpeed = element.getFloat("movementSpeed", 2f);
		
		type.ability1Id = element.getInt("ability1", type.ability1Id);
		type.ability2Id = element.getInt("ability2", type.ability2Id);
		type.effect1Id = element.getInt("effect1", type.effect1Id);
		type.effect2Id = element.getInt("effect2", type.effect2Id);
		
		type.cost = element.getInt("cost", type.cost);
		type.description = element.get("description", type.description);
		type.idleImage = element.get("idleImage", type.idleImage);
		type.portrait = element.get("portrait", type.portrait);
		type.swingSfx = element.get("swingSfx", "swinging");
		type.impactSfx = element.get("impactSfx", "impacting");
		type.nameLabel = "name-"+name.toLowerCase().replaceAll("\\s","");
		
		// Terrain type penalties, attributes/elements starting with 'terrainPenalty', e.g 'terrainPenaltySnow'
		for(TerrainType t : TerrainType.values()){
			int i = t.ordinal();
			String typeName = t.toString().toLowerCase();
			typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
			String value = element.get("terrainPenalty" + typeName, "");
			
			if(value.length() > 0){
				try{
					int intValue = Integer.parseInt(value);
					type.terrainTypePenalties[i] = Integer.parseInt(value);
					Gdx.app.log(type.toString(), String.format("Loaded custom terrain penalty %d for %s.", intValue, t));
				}
				catch(NumberFormatException e){
					throw new RuntimeException(String.format("Can't set non integer value '%s' as penalty of %s for unit type %s.", value, t, type), e);
				}
			}
		}
		
		Gdx.app.log("UnitType", String.format("Loaded attributes for '%s'.", name));
	}
	
	public static UnitType getUnitTypeByName(String name){
		for(UnitType type : UnitType.values()){
			if(type.name.equals(name)) return type;
		}
		
		return null;
	}
	
	/*depracated crap?
	public static double getAttackModifier(UnitType attacker, UnitType defender) {
		double ret = 1;
		if (attacker.equals(AXE_THROWER)) {
			if (defender.equals(VALKYRIE) || defender.equals(HAWK)) 
				ret = 1.2;
		}
		else if (attacker.equals(HAWK)) {
			if (defender.equals(SHAMAN) || defender.equals(GOBLIN_SHAMAN))
				ret = 1.3;
		}
		else if (attacker.equals(SHAMAN)) {
			if (defender.equals(SWORD) || defender.equals(TROLL))
				ret = 1.5;
		}
		else if (attacker.equals(SWORD)) {
			if (defender.equals(BERSERKER) || defender.equals(JOTUN))
				ret = 1.3;
		}
		else if (attacker.equals(WOLF)) {
			if (defender.equals(AXE_THROWER) || defender.equals(DARK_ELF))
				ret = 1.3;
		} 
		
		return ret;
	}
	*/
}
