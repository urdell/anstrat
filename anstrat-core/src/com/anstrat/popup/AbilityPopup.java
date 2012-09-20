package com.anstrat.popup;

import com.anstrat.command.ActivatePlayerAbilityCommand;
import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.playerAbilities.DoubleTargetedPlayerAbility;
import com.anstrat.gameCore.playerAbilities.PlayerAbility;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityFactory;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.gameCore.playerAbilities.TargetedPlayerAbility;
import com.anstrat.gui.GEngine;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * Skeleton class for ability popup.
 * @author kalper
 */
public class AbilityPopup extends Popup {
	
	public static String CAST_TEXT = "Cast";
	public static String CANCEL_TEXT = "Cancel";
	
	private TextButton cast, cancel;
	private AbilityTypeCard card;
	private Button[] abilities;
	private PlayerAbilityType[] types;
	private Button selectedButton;

	private static final ClickListener CAST_BUTTON_LISTENER = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			AbilityPopup popup = (AbilityPopup) Popup.getCurrentPopup();
			PlayerAbilityType type = popup.card.type;
			PlayerAbility ability = PlayerAbilityFactory.createAbility(type, State.activeState.getCurrentPlayer());
			Gdx.app.log("AbilityPopup", String.format("User wants to cast '%s'.", type.name));
			if (ability instanceof TargetedPlayerAbility) {
				GEngine.getInstance().selectionHandler.selectPlayerAbility((TargetedPlayerAbility) ability);
			}
			else if (ability instanceof DoubleTargetedPlayerAbility) {
				GEngine.getInstance().selectionHandler.selectPlayerAbility((DoubleTargetedPlayerAbility) ability);
			}
			else {
				Command command = new ActivatePlayerAbilityCommand(ability.player, type);
				CommandHandler.execute(command);
			}

			Popup.getCurrentPopup().close();
		}
	};
	
	public AbilityPopup(PlayerAbilityType... types) {
		this.types = types;
		cast = ComponentFactory.createButton(CAST_TEXT, CAST_BUTTON_LISTENER);
		cancel = ComponentFactory.createButton(CANCEL_TEXT, Popup.POPUP_CLOSE_BUTTON_HANDLER);
		abilities = new Button[types.length];
		card = new AbilityTypeCard(types[0]);
		Image tempImage; // used to instantiate images for buttons.
		
		for(int i=0; i<abilities.length; i++){
			final int b = i;
			//TODO 
			tempImage = new Image();
			abilities[i] = new Button(tempImage, Assets.SKIN.get("default", ButtonStyle.class));
			tempImage.setFillParent(true);
			tempImage.setAlign(Align.center);
			abilities[i].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
			        selectButton(b);
			    }
			});
		}
		selectButton(0);
		
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("empty")));
		
		float bh = Main.percentHeight*10;
		float bw = bh * 2;
		
		Table abilityTable = new Table();
		abilityTable.defaults().left().top().width(bw).height(bh);
		abilityTable.add(abilities[0]);
		abilityTable.add(abilities[1]);
		abilityTable.add(abilities[2]);
		
		Table bottom = new Table();
		bottom.defaults().left().top().size(bw, bh);
		bottom.add(cast);
		bottom.add().fill();
		bottom.add(cancel);
		
		this.defaults().top();
		
		this.add(abilityTable).width(Gdx.graphics.getWidth());
		this.row();
		this.add(card).height(Main.percentHeight * 60);
		this.row();
//		this.add().expand().fill();
//		this.row();
		this.add(bottom);
		//this.add(cast).size(Main.percentWidth*20, Main.percentHeight*10).uniform();
		//this.add(cancel).uniform();
		
	}
	
	/**
	 * Check if the unit is buyable before showing popup.
	 */
	@Override public void show(){
		update();
		super.show();
	}
	
	public void selectButton(int button) {
		if (selectedButton != null)
			selectedButton.setChecked(false);
		selectedButton = abilities[button];
		selectedButton.setChecked(true);
		card.setType(types[button]);
		card.setSize(card.getWidth(), card.getHeight());
		update();
	}
	
	public void update(){
		if(State.activeState==null)
			return;
		
		int mana = GameInstance.activeGame.getUserPlayer().mana;
		boolean isPlayerTurn = GameInstance.activeGame.isUserCurrentPlayer();
		
		//Disable cast button if current ability is not affordable
		boolean canCast = mana >= card.type.manaCost;
		Assets.SKIN.setEnabled(cast, canCast && isPlayerTurn);
		card.setDisabled(!canCast);
		
		// Mark other abilities that are too expensive.
		for(int i=0; i<types.length; i++){
			if(mana<types[i].manaCost)
				abilities[i].setStyle(Assets.SKIN.get("default-disabled", ButtonStyle.class));
			else
				abilities[i].setStyle(Assets.SKIN.get("default", ButtonStyle.class));
		}
	}
	
	@Override
	public void resize(float width, float height){
		this.setSize(width, height);
		super.resize(width, height);
	}
}
