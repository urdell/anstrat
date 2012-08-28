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
	
	private static int MARGIN = 25;
	
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
		contents.top().left();
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

		layout.pad(MARGIN);
		layout.defaults().top().left().padBottom((int)(5*Main.percentHeight));
		
		Table outer = new Table();
		
		Table inner1 = new Table();
		inner1.defaults().top().left();
		inner1.add(name).center();
		inner1.row();
		inner1.add(portrait);
		inner1.row();
		inner1.add(hp);
		inner1.row();
		inner1.add(ap);
		
		outer.add(inner1).padRight((int)(8*Main.percentWidth));
		
		Table inner2 = new Table();
		inner2.defaults().top().left();
		inner2.add(attack);
		inner2.row();
		inner2.add(defence);
		inner2.row();
		inner2.add(apReg);
		
		outer.add(inner2).top();
		
		layout.add(outer);
		layout.row();
		layout.add(description).expandX().fillX();
		layout.row();
		layout.add(abilityTable);
		layout.row();
		layout.add(effectsTable);
		
		this.setClickListener(new ClickListener() {
	        @Override
	        public void click(Actor actor,float x,float y ){
	        	Popup.getCurrentPopup().close();
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
		
		// Effects
		effectsTable.clear();
		effectsTable.defaults().left();
		effectsTable.add("Effects:");
		effectsTable.row();
		
		for(int i = 0; i < unit.effects.size(); i++){
			Effect effect = unit.effects.get(i);
			
			Image icon = new Image(Assets.getTextureRegion(effect.iconName));
			Label name = new Label(effect.name, Assets.SKIN);
			Label description = new Label(" -" + effect.description, Assets.SKIN);
			
			Table inner = new Table();
			inner.add(icon).height((int)(3*Main.percentHeight)).width((int)(5*Main.percentWidth)).padRight((int)(Main.percentWidth));
			inner.add(name).expandX().fillX();

			effectsTable.add(inner);
			effectsTable.row();
			effectsTable.add(description);
			effectsTable.row();
		}
		
		// Abilities
		abilityTable.clear();
		abilityTable.defaults().left();
		abilityTable.add("Abilities:");
		abilityTable.row();
		
		for(int i = 0; i < unit.abilities.size(); i++){
			Ability ability = unit.abilities.get(i);
			
			Image icon = new Image(Assets.getTextureRegion(ability.getIconName(unit)));
			Label name = new Label(ability.name, Assets.SKIN);
			Label description = new Label(" -" + ability.description, Assets.SKIN);
			
			Table inner = new Table();
			inner.add(icon).height((int)(3*Main.percentHeight)).width((int)(5*Main.percentWidth)).padRight((int)(Main.percentWidth));
			inner.add(name).expandX().fillX();

			abilityTable.add(inner);
			abilityTable.row();
			abilityTable.add(description);
			abilityTable.row();
		}
		
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
