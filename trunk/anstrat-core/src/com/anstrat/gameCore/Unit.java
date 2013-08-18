package com.anstrat.gameCore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.abilities.AbilityFactory;
import com.anstrat.gameCore.effects.APRegenerationModifier;
import com.anstrat.gameCore.effects.AffectsAttack;
import com.anstrat.gameCore.effects.DamageModifier;
import com.anstrat.gameCore.effects.DamageTakenModifier;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.gameCore.effects.EffectFactory;
import com.anstrat.geography.Map;
import com.anstrat.geography.TileCoordinate;

/**
 * The class that stores information about a single unit
 * @author Ekis
 *
 */
public class Unit implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public TileCoordinate tileCoordinate;
	public final int id;
	public int ownerId;
	
	private UnitType type;
	public List<Ability> abilities;
	public List<Effect> effects;
	
	public int currentHP;
	public int currentAP;
	public int attacksThisTurn = 0;
	public boolean isAlive = true;
	
	/**
	 * for testing purposes
	 */
	public Unit(UnitType type, int ownerID, int customID)
	{
		this.type = type;
		currentHP = type.maxHP;
		currentAP = type.APStart;
		
		// Abilities
		abilities = new ArrayList<Ability>();
		if(type.ability1Id != 0) abilities.add(AbilityFactory.createAbility(type.ability1Id));
		if(type.ability2Id != 0) abilities.add(AbilityFactory.createAbility(type.ability2Id));
		
		// Permanent effects
		effects = new ArrayList<Effect>();
		if(type.effect1Id != 0) effects.add(EffectFactory.createEffect(type.effect1Id));
		if(type.effect2Id != 0) effects.add(EffectFactory.createEffect(type.effect2Id));
		
		attacksThisTurn = 0;		
		tileCoordinate = null;
		this.ownerId = ownerID;
		this.id = customID; 
	}
	public Unit(UnitType type, int ownerID)
	{
		this(type, ownerID, State.activeState.nextUnitId++);//get the next free id, and increment next id.
	}
	
	// TODO: Added getters for unit attributes, since they might be modified in the future by individual units
	// that have experience or something else. This shouldn't be a problem since a unit's attributes doesn't have
	// to be accessed that often, far from every render or something like that.
	
	public int getAttack(){
		int addedAttack = 0;
		for(Effect effectAttack : effects){
			
			if(effectAttack instanceof AffectsAttack){
				addedAttack += ((AffectsAttack) effectAttack).attackIncrease(this);
			}
		}
		
		return this.type.attack + addedAttack;
	}
	
	public float getDamageModification(){
		float damageModification = 1;
		for(Effect effectAttack : effects){
			
			if(effectAttack instanceof DamageModifier){
				damageModification*=((DamageModifier) effectAttack).damageModification(this);
			}
		}
		
		return damageModification;
	}
	public float getDamageTakenModification(){
		float damageModification = 1;
		for(Effect effectAttack : effects){
			
			if(effectAttack instanceof DamageTakenModifier){
				damageModification *= ((DamageTakenModifier) effectAttack).damageTakenModification(this);
			}
		}
		
		return damageModification;
	}
	
	public int getMaxAP(){
		return this.type.maxAP;
	}
	
	public int getAPReg(){
		int APReg = this.type.APReg;
		for(Effect effectAttack : effects){
			
			if(effectAttack instanceof APRegenerationModifier){
				APReg += ((APRegenerationModifier) effectAttack).APRegModication();
			}
		}
		
		return APReg;
	}
	
	public int getAPCostAttack(){
		return this.type.APCostAttacking+attacksThisTurn;
	}
	
	public int getAPCostToActivateAbility(){
		for (Ability a : this.getAbilities()){
			return a.apCost;
		}
		return Integer.MAX_VALUE;
	}
	
	public int getHPReg(){
		return this.type.HPReg;
	}
	
	public int getMaxHP(){
		return this.type.maxHP;
	}
	
	public String getName(){
		return this.type.name;
	}
	
	public int getMinAttackRange(){
		return this.type.minAttackRange;
	}
	
	public int getMaxAttackRange(){
		return this.type.maxAttackRange;
	}
	
	public UnitType getUnitType(){
		return this.type;
	}

	public List<Effect> getEffects() {
		return effects;
	}

	public List<Ability> getAbilities() {
		return abilities;
	}
	
	/**
	 * Removes units from the game if HP<=0
	 */
	public void resolveDeath(){
		if(this.currentHP<=0){
			State.activeState.unitList.remove(this.id);
			isAlive = false;
		}
	}
	
	public boolean isVisible(int playerId){
		Map map = State.activeState.map;
		if(!map.fogEnabled)
			return true;
		return map.getTile(tileCoordinate).visible[playerId] >= 1;
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Unit)) return false;
		Unit un0t = (Unit) o;
		return this.id == un0t.id;
	}

}
