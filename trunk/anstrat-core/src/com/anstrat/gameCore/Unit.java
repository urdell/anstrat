package com.anstrat.gameCore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.abilities.AbilityFactory;
import com.anstrat.gameCore.effects.AffectsAttack;
import com.anstrat.gameCore.effects.DamageModifier;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.gameCore.effects.EffectFactory;
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
	 * Constructs a unit given the type and ownerID
	 * @param type the type of unit, see {@link Unit}.TYPE_* for types
	 * @param ownerId the owner player, 0 = player 1, 1 = player 2 etc
	 */
	public Unit(UnitType type, int ownerID)
	{
		this.type = type;
		currentHP = type.maxHP;
		currentAP = type.maxAP/2;
		
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
		this.id = State.activeState.nextUnitId++; //get the next free id, and increment next id.
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
				damageModification += ((DamageModifier) effectAttack).damageModification(this);
			}
		}
		
		return damageModification;
	}
	
	public int getMaxAP(){
		return this.type.maxAP;
	}
	
	public int getAPReg(){
		return this.type.APReg;
	}
	
	public int getAPCostAttack(){
		return this.type.APCostAttacking+attacksThisTurn;
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

}
