package com.anstrat.popup;

import com.anstrat.command.ActivatePlayerAbilityCommand;
import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.core.Assets;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

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
		public void click(Actor actor, float x, float y) {
			AbilityPopup popup = (AbilityPopup) Popup.currentPopup;
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

			Popup.currentPopup.close();
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
			abilities[i] = new Button(tempImage, Assets.SKIN.getStyle("default", ButtonStyle.class));
			tempImage.setFillParent(true);
			tempImage.setAlign(Align.CENTER);
			abilities[i].setClickListener(new ClickListener() {
				@Override
			    public void click(Actor actor,float x,float y ){
			        selectButton(b);
			    }
			});
		}
		selectButton(0);
		
		this.setBackground(Assets.SKIN.getPatch("empty"));
		
		for(Button ib : abilities) {
			this.addActor(ib);
		}
		
		this.addActor(cast);
		this.addActor(cancel);
		this.addActor(card);
		
	}
	
	/**
	 * Check if the unit is buyable before showing popup.
	 */
	@Override public void show(){
		checkAbilityAffordable();
		super.show();
	}
	
	public void selectButton(int button) {
		if (selectedButton != null)
			selectedButton.setChecked(false);
		selectedButton = abilities[button];
		selectedButton.setChecked(true);
		card.setType(types[button]);
		card.setSize(card.width, card.height);
		checkAbilityAffordable();
	}
	
	/**
	 * Disables buy button if unit is too expensive.
	 * 
	 * NOTE: Do other things, like graying the unit portrait of all units that can't be bought.
	 */
	public void checkAbilityAffordable(){
		if(State.activeState==null)
			return;
		
		int mana = State.activeState.getCurrentPlayer().mana;
		boolean isPlayerTurn = State.activeState.isUserCurrentPlayer();
		
		//Disable cast button if current ability is not affordable
		boolean canCast = mana>=card.type.manaCost;
		Assets.SKIN.setEnabled(cast, canCast && isPlayerTurn);
		card.setDisabled(!canCast);
		
		//Mark other abilities that are too expensive.		TODO: Just gray unit portraits or something instead, as current method fucks with button presses.
		for(int i=0; i<types.length; i++){
			if(mana<types[i].manaCost || !isPlayerTurn)
				abilities[i].setStyle(Assets.SKIN.getStyle("default-disabled", ButtonStyle.class));
			else
				abilities[i].setStyle(Assets.SKIN.getStyle("default", ButtonStyle.class));
		}
	}
	
	public void resize(int width, int height){
		overlay.setSize(width, height);
		this.width = width;
		this.height = height;
		this.x = this.y = 0;
		
		float buttonHeight = height/8f;
		float buttonWidth = buttonHeight;//width/4f;

		float cardWidth = width-2*buttonWidth - 4*Main.percentWidth;//width/2f;
		float cardHeight = 3*width/4f;
		
		cast.x = (float) (0.25*buttonWidth);
		cast.y = (float) (buttonHeight);
		cast.width = buttonWidth*1.5f;
		cast.height = buttonHeight;
		
		cancel.x = (float) (width-1.75*buttonWidth);
		cancel.y = (float) (buttonHeight);
		cancel.width = buttonWidth*1.5f;
		cancel.height = buttonHeight;
		
		card.x = (width-cardWidth)/2 - 2*Main.percentWidth;//width/4-2*Main.percentWidth;
		card.y = (float) (2.25*buttonHeight)-Main.percentHeight;
		card.setSize(cardWidth+4*Main.percentWidth, cardHeight+2*Main.percentHeight);
		
		if(abilities.length > 0) {
			abilities[0].x = 0;
			abilities[0].y = (float) (3*buttonHeight);
		}
		if(abilities.length > 1) {
			abilities[1].x = 0;
			abilities[1].y = (float) (4.5*buttonHeight);
		}
		if(abilities.length > 2) {
			abilities[2].x = (float) (0.6*buttonWidth);
			abilities[2].y = (float) (card.y+card.height);//(float) (height-2*buttonHeight);
		}
		/*
		abilities[3].x = (float) (width-1.6*buttonWidth);
		abilities[3].y = (float) (card.y+card.height);//(float) (height-2*buttonHeight);
		
		
		abilities[4].x = width-buttonWidth;
		abilities[4].y = (float) (4.5*buttonHeight);
		
		abilities[5].x = width-buttonWidth;
		abilities[5].y = (float) (3*buttonHeight);
		*/
		for(Button ability : abilities){
			ability.width = buttonWidth;
			ability.height = buttonHeight;
		}
		this.layout();
	}
}
