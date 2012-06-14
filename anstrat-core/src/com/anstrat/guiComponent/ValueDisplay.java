package com.anstrat.guiComponent;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GBar;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GTile;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.TableLayout;

public class ValueDisplay extends Table {
	public int valueType;
	
	public static final int VALUE_GOLD         = 0;
	public static final int VALUE_MANA         = 1;
	public static final int VALUE_UNIT_NAME    = 2;
	public static final int VALUE_UNIT_HP      = 3;
	public static final int VALUE_UNIT_AP      = 4;
	public static final int VALUE_UNIT_DEFENCE = 5;
	public static final int VALUE_UNIT_ATTACK  = 6;
	public static final int VALUE_UNIT_RANGE   = 7;
	public static final int VALUE_UNIT_COST    = 8;
	public static final int VALUE_TERRAIN_NAME = 9;
	public static final int VALUE_UNIT_AP_REG   = 10;
	
	private Label label;
	private GBar bar;
	private boolean usesBar;
	private Image icon;
	
	public ValueDisplay(int valueType){
		super();
		this.valueType = valueType;
		TableLayout layout = this.getTableLayout();
		
		icon = new Image(getValueTexture(valueType));
		layout.register("icon", icon);
		
		usesBar = valueType==VALUE_UNIT_HP;
		
		if(!usesBar){
			label = new Label("", Assets.SKIN);
			layout.register("info", label);
		}
		else{
			bar = new GBar(100f, 16f, 1f);
			bar.setColors(Color.GREEN, new Color(0f, 0.3f, 0f, 1f), Color.GRAY);
			layout.register("info", bar);
		}		
		layout.parse("[icon] size:"+(int)(3*Main.percentHeight)+" paddingRight:"+(int)(2*Main.percentWidth)+
				" [info] expand:x fill:x height:"+(int)(3*Main.percentHeight));
	}
	
	// Returns null if the value isn't currently applicable (for example the name of a unit if no unit is selected)
	private static int[] tempIncome = new int[2];
	
	// Returns the value as a String of the given valueType 
	
	/**
	 * Returns the value as a String of the given valueType for the given Unit.
	 * Returns an empty string if we value isn't currently applicable, such as the name of a unit if it isn't selected.
	 * @param unit the unit whose value we're after
	 * @param valueType the type of value to retrieve, see {@link ValueDisplay}.VALUE_* for valid types.
	 * @return
	 */
	private static String getValueString(Unit unit, int valueType){
		
		Player player = State.activeState.getCurrentPlayer();
		GTile gTile = GEngine.getInstance().selectionHandler.gTile;

		switch(valueType){
			case VALUE_GOLD: {
				State.activeState.getIncome(player.playerId, tempIncome);
				return String.format("%d (%s%d)", player.gold, tempIncome[0] >= 0 ? "+" : "-" , tempIncome[0]);
			}
			case VALUE_MANA: {
				State.activeState.getIncome(player.playerId, tempIncome);
				return String.format("%d (%s%d)", player.mana, tempIncome[1] >= 0 ? "+" : "-" , tempIncome[1]);
			}
			case VALUE_UNIT_HP:      return unit.currentHP+"/"+unit.getMaxHP()+" (+"+unit.getHPReg()+")";
			case VALUE_UNIT_AP:      return unit.currentAP+" / "+unit.getMaxAP();
			case VALUE_UNIT_AP_REG:  return ""+unit.getAPReg();
			case VALUE_UNIT_ATTACK:  return String.valueOf(unit.getAttack());
			case VALUE_UNIT_RANGE:   return String.valueOf(unit.getUnitType().maxAttackRange);
			case VALUE_UNIT_COST:    return String.valueOf(unit.getUnitType().cost);
			case VALUE_TERRAIN_NAME: return gTile != null ? gTile.tile.terrain.name : "";
			default: return "";
		}
	}
	
	/**
	 * Gets texture for specific value type.
	 * @param valueType The value type.
	 * @return
	 */
	private static TextureRegion getValueTexture(int valueType){
		switch(valueType){
			case VALUE_GOLD:         return Assets.getTextureRegion("gold");
			case VALUE_MANA:         return Assets.getTextureRegion("mana");
			case VALUE_UNIT_HP:      return Assets.getTextureRegion("hp");
			case VALUE_UNIT_AP:      return Assets.getTextureRegion("ap");
			case VALUE_UNIT_DEFENCE: return Assets.getTextureRegion("shield");
			case VALUE_UNIT_ATTACK:  return Assets.getTextureRegion("sword");
			case VALUE_UNIT_RANGE:   return Assets.getTextureRegion("range");
			case VALUE_UNIT_AP_REG:   return Assets.getTextureRegion("speed");
			case VALUE_UNIT_COST:    return Assets.getTextureRegion("gold");
			default: return null;
		}
	}
	
	private void setValue(Unit unit, int valueType){
		switch(valueType){
		case VALUE_UNIT_HP:
			 bar.setValue((float)unit.currentHP/unit.getMaxHP());
			 break;
		
		default:
			String valueString = getValueString(unit, valueType);
			if(valueString.length() != 0)
				label.setText(getValueString(unit, valueType));
			break;
		}
		if(valueType == VALUE_UNIT_ATTACK){  // The attack value display has different icons depending on unit range.
			 if(unit.getMaxAttackRange() == 1)
				 icon.setRegion(Assets.getTextureRegion("sword"));
			 else
				 icon.setRegion(Assets.getTextureRegion("bow"));
		}
	}
	
	public void setColor(Color color){
		if(label!=null)
			label.setColor(color);
	}
	
	public void update(Unit unit){
		setValue(unit, valueType);
	}
	
	/**
	 * Clears the text
	 */
	public void clear(){
		if(label!=null)
			label.setText("");
	}
}
