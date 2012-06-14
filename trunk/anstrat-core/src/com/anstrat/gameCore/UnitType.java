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
	HEAL("Shaman"), 
	AXE_THROWER("Axe Thrower"),
	SWORD("Swordsman"),
	WOLF("Wolf"),
	HAWK("Hawk");
	
	public static final int ATTACK_TYPE_RANGED = 0;
	public static final int ATTACK_TYPE_BLUNT = 1;
	public static final int ATTACK_TYPE_CUT = 2;
	
	public transient final String name;
	
	public transient String graphicsFolder;
	
	public String idleImage;
	
	// HP
	public transient int maxHP;
	public transient int HPReg;
	
	// Armor
	public transient int rangeArmor;
	public transient int bluntArmor;
	public transient int cutArmor;
		
	// Attack
	public int attack;
	public int minAttackRange;
	public int maxAttackRange;
	
	/**
	 * See {@link UnitType}.ATTACK_TYPE_*
	 */
	public transient int attackType;
	
	// AP
	public transient int maxAP;
	public transient int APReg;
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
		this.rangeArmor = 0;
		this.bluntArmor = 0;
		this.cutArmor = 0;
		this.attack = 6;
		this.attackType = ATTACK_TYPE_CUT;
		this.maxAP = 9;
		this.minAttackRange = 1;
		this.maxAttackRange = 1;
		this.APCostAttacking = 2;
		this.cost = 20;
		this.description = "This should not be here";
		
		// Default terrain type penalties
		// (if we forget to explicitly assign a penalty it will default to the 
		// default value of an int, zero)
		terrainTypePenalties = new int[TerrainType.values().length];
		terrainTypePenalties[TerrainType.DEEP_WATER.ordinal()] = Integer.MAX_VALUE;
		//terrainTypePenalties[TerrainType.SNOW.ordinal()] = 2;
		terrainTypePenalties[TerrainType.FIELD.ordinal()] = 1;
		terrainTypePenalties[TerrainType.MOUNTAIN.ordinal()] = Integer.MAX_VALUE;
		//terrainTypePenalties[TerrainType.VOLCANO.ordinal()] = Integer.MAX_VALUE;
		terrainTypePenalties[TerrainType.FOREST.ordinal()] = 2;
		//terrainTypePenalties[TerrainType.HILL.ordinal()] = 3;
		terrainTypePenalties[TerrainType.SHALLOW_WATER.ordinal()] = 2;
		
		terrainTypePenalties[TerrainType.VILLAGE.ordinal()] 
				= terrainTypePenalties[TerrainType.CASTLE.ordinal()] 
				= terrainTypePenalties[TerrainType.FIELD.ordinal()];
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
		type.rangeArmor = element.getInt("rangeArmor", type.rangeArmor);
		type.bluntArmor = element.getInt("bluntArmor", type.bluntArmor);
		type.cutArmor = element.getInt("cutArmor", type.cutArmor);
		type.attack = element.getInt("attack", type.attack);
		type.minAttackRange = element.getInt("minAttackRange", type.minAttackRange);
		type.maxAttackRange = element.getInt("maxAttackRange", type.maxAttackRange);
		type.maxAP = element.getInt("maxAP", type.maxAP);
		type.APReg = element.getInt("APReg", type.APReg);
		type.APCostAttacking = element.getInt("APCostAttacking", type.APCostAttacking);
		
		type.ability1Id = element.getInt("ability1", type.ability1Id);
		type.ability2Id = element.getInt("ability2", type.ability2Id);
		type.effect1Id = element.getInt("effect1", type.effect1Id);
		type.effect2Id = element.getInt("effect2", type.effect2Id);
		
		
		type.attackType = element.getInt("attackType", type.attackType);
		
		if(type.attackType != ATTACK_TYPE_BLUNT && type.attackType != ATTACK_TYPE_CUT &&type.attackType != ATTACK_TYPE_RANGED){
			throw new GdxRuntimeException(String.format("'%d' is not a valid attack type.", type.attackType));
		}
		
		type.cost = element.getInt("cost", type.cost);
		type.description = element.get("description", type.description);
		type.idleImage = element.get("idleImage", type.idleImage);
		
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
}
