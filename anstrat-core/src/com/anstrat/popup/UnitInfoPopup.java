package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ValueDisplay;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.TableLayout;

/**
 * @author Kalle
 */
public class UnitInfoPopup extends Popup {
	
	private static int MARIGIN = 25;
	
	private Image portrait;
	private Label description, name;
	private ValueDisplay hp, ap, attack, defence, apReg;
	private Table contents, effectsTable, abilityTable;

	public UnitInfoPopup() {
		this.setBackground(Assets.SKIN.getPatch("black"));
		
		contents = new Table();
		TableLayout layout = contents.getTableLayout();
		contents.x = contents.y = 0;
		contents.width = width;
		contents.height = height;
		contents.align("top left");
		this.addActor(contents);
		
		portrait = new Image();
		description = new Label("", Assets.SKIN);
		description.setWrap(true);
		ap     = new ValueDisplay(ValueDisplay.VALUE_UNIT_AP);
		hp     = new ValueDisplay(ValueDisplay.VALUE_UNIT_HP);
		name   = new Label("", Assets.SKIN);
		
		attack  = new ValueDisplay(ValueDisplay.VALUE_UNIT_ATTACK);
		defence = new ValueDisplay(ValueDisplay.VALUE_UNIT_DEFENCE);
		apReg   = new ValueDisplay(ValueDisplay.VALUE_UNIT_AP_REG);
		
		effectsTable = new Table(Assets.SKIN);
		abilityTable = new Table(Assets.SKIN);

		layout.register("name", name);
		layout.register("portrait", portrait);
		layout.register("health", hp);
		layout.register("ap", ap);
		layout.register("attack", attack);
		layout.register("defence", defence);
		layout.register("range", apReg);
		layout.register("description", description);
		layout.register("effects", effectsTable);
		layout.register("abilities", abilityTable);
		
		layout.parse("padding:"+MARIGIN+"\n"+
				"* align:top,left paddingBottom:"+(int)(5*Main.percentHeight)+""+
				"{{* align:top,left"+
					"[name] align:center"+
					"---"+
					"[portrait]"+
					"---"+
					"[health]"+
					"---"+
					"[ap]"+
				"} paddingRight:"+(int)(8*Main.percentWidth)+""+
				"{ * align:top,left"+
					"[attack]"+
					"---"+
					"[defence]"+
					"---"+
					"[range]"+
				"} align:top}"+
				"---"+
				"[description] expand:x fill:x" +
				"---" +
				"[abilities]" +
				"---" +
				"[effects]");
		
		this.setClickListener(new ClickListener() {
	        @Override
	        public void click(Actor actor,float x,float y ){
	        	Popup.currentPopup.close();
	        }
		});
	}
	
	/**
	 * Shows a popup with unit info
	 * @param unit The unit to show info for.
	 */
	public void show(Unit unit){
		name.setText(unit.getName());
		description.setText(unit.getUnitType().description);
		portrait.setRegion(GUnit.getTextureRegion(unit.getUnitType()));
		
		hp.update(unit);
		ap.update(unit);
		attack.update(unit);
		defence.update(unit);
		apReg.update(unit);
		
		effectsTable.clear();
		TableLayout tl = effectsTable.getTableLayout();
		int count = 0;
		String layout = "*align:left 'Effects:'---";
		for(Effect e : unit.effects){
			tl.register("icon"+count, new Image(Assets.getTextureRegion(e.iconName)));
			tl.register("name"+count, new Label(e.name,Assets.SKIN));
			Label temp = new Label(" -"+e.description,Assets.SKIN);
			temp.pack();
			tl.register("description"+count, temp);
			layout += "{[icon"+count+"] height:"+(int)(3*Main.percentHeight)+" width:"+(int)(5*Main.percentWidth)+" paddingRight:"+(int)(Main.percentHeight)+
					" [name"+count+"] expand:x fill:x} --- " +
					" [description"+count+"] ---";
			count++;
		}
		tl.parse(layout);
		
		abilityTable.clear();
		layout = "*align: left 'Abilities:'---";
		count = 0;
		tl = abilityTable.getTableLayout();
		for(Ability a : unit.abilities){
			tl.register("icon"+count, new Image(Assets.getTextureRegion(a.getIconName(unit))));
			tl.register("name"+count, new Label(a.name,Assets.SKIN));
			Label temp = new Label(" -"+a.description,Assets.SKIN);
			temp.pack();
			tl.register("description"+count, temp);
			layout += "{[icon"+count+"] height:"+(int)(3*Main.percentHeight)+" width:"+(int)(5*Main.percentWidth)+" paddingRight:"+(int)(Main.percentWidth)+
					" [name"+count+"] expand:x fill:x} --- " +
					" [description"+count+"] ---";
			count++;
		}
		tl.parse(layout);
		
		this.show();
	}
	
	/**
	 * Resizes the popup
	 */
	@Override
	public void resize(int width, int height) {
		overlay.setSize(width, height);
		this.width = width;
		this.height = height;
		contents.width = this.width;
		contents.height = this.height;
	}
}
