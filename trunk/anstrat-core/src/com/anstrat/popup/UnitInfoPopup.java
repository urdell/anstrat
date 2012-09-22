package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.guiComponent.ColorTable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * @author Kalle
 */
public class UnitInfoPopup extends Popup {

	private Table effectsTable, abilityTable, descriptionTable;
	
	private UnitTypeCard card;
	private ColorTable bottomTable;
	
	private Label description, name, other;
	
	public UnitInfoPopup() {
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("black")));
		
		card = new UnitTypeCard(false);
		
		effectsTable = new Table(Assets.SKIN);
		abilityTable = new Table(Assets.SKIN);
		descriptionTable = new Table(Assets.SKIN);
		name = new Label("",Assets.SKIN);
		description = new Label("", new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE));
		other = new Label("", new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE));
		description.setWrap(true);
		descriptionTable.defaults().top().left();
		descriptionTable.add(name).height(Main.percentHeight*3);
		descriptionTable.row();
		descriptionTable.add(description).fillX().expandX();
		descriptionTable.row();
		descriptionTable.add(other).height(Main.percentHeight*3);
		descriptionTable.row();
		descriptionTable.add().fill().expand();
		
		bottomTable = new ColorTable(Color.WHITE);
		bottomTable.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("border-thick-updown")));
		bottomTable.defaults().padRight(Main.percentHeight).padLeft(Main.percentHeight);
		bottomTable.add("Abilities:").height(3*Main.percentHeight).expandX().fillX();
		bottomTable.add("Effects:").height(3*Main.percentHeight).expandX().fillX();
		bottomTable.row();
		bottomTable.add(abilityTable).fillX().expandX().uniform();
		bottomTable.add(effectsTable).fillX().expandX().uniform();
		bottomTable.row();
		bottomTable.add(descriptionTable).colspan(2).fill().expand();
		bottomTable.debug();
		
		this.defaults().top().left();
		this.padBottom(2*Main.percentHeight);
		this.add(card).height(65 * Main.percentHeight).width(Gdx.graphics.getWidth());
		this.row();
		this.add(bottomTable).width(Gdx.graphics.getWidth()).height(35 * Main.percentHeight);
	}
	
	/**
	 * Shows a popup with unit info
	 * @param unit The unit to show info for.
	 */
	public void show(Unit unit){
		card.setUnit(unit);
		bottomTable.setColor(card.getBackgroundColor());
		
		// Reset descriptions
		this.name.setText("");
		this.description.setText("Click on abilitiy / effect icons to show info. \nExit with back key / ESC / backspace.");
		this.other.setText("");
		
		// Effects
		effectsTable.clear();
		effectsTable.defaults().top().left();
		
		for(final Effect e : unit.effects){
			Image button = new Image(Assets.getTextureRegion(e.iconName));
			button.setTouchable(Touchable.enabled);
			effectsTable.add(button).size(10f*Main.percentHeight).padRight(Main.percentWidth);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					showInfo(false, e.name, e.description, "");
				}
			});
		}
		effectsTable.add().fill().expand();
		
		// Abilities
		abilityTable.clear();
		abilityTable.defaults().top().left();
		
		for(final Ability a : unit.abilities){
			Image button = new Image(Assets.getTextureRegion(a.getIconName(unit)));
			button.setTouchable(Touchable.enabled);
			abilityTable.add(button).size(10f*Main.percentHeight).padRight(Main.percentWidth);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					showInfo(true, a.name, a.description, "Cost: "+a.apCost);
				}
			});
		}
		
		abilityTable.add().fill().expand();
		
		this.show();
	}
	
	/**
	 * Shows info for the selected effect / ability.
	 * TODO: Mark selected icon somehow.
	 * @param isAbility
	 * @param name
	 * @param description
	 */
	private void showInfo(boolean isAbility, String name, String description, String other){
		this.name.setText( (isAbility?"Ability: ":"Effect: ") + name );
		this.description.setText(description);
		this.other.setText(other);
		descriptionTable.layout();
	}
}
