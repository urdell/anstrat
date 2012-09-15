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
		overlay.setSize(width, height);
		this.setBounds(0, 0, width, height);
		
		float buttonHeight = height/8f;
		float buttonWidth = buttonHeight;//width/4f;

		float cardWidth = width-2f*buttonWidth - 4f*Main.percentWidth;//width/2f;
		float cardHeight = 3f*width/4f;
		
		cast.setBounds(0.25f*buttonWidth, buttonHeight, buttonWidth*1.5f, buttonHeight);
		cancel.setBounds(width-1.75f*buttonWidth, buttonHeight, buttonWidth*1.5f, buttonHeight);
		card.setBounds((width-cardWidth)/2f - 2f*Main.percentWidth, 2.25f*buttonHeight-Main.percentHeight, cardWidth+4f*Main.percentWidth, cardHeight+2f*Main.percentHeight);
		
		if(abilities.length > 0)
			abilities[0].setPosition(0, 3f*buttonHeight);
		if(abilities.length > 1)
			abilities[1].setPosition(0, 4.5f*buttonHeight);
		if(abilities.length > 2)
			abilities[2].setPosition(0.6f*buttonWidth, card.getY()+card.getHeight());
		/*
		abilities[3].x = (float) (width-1.6*buttonWidth);
		abilities[3].y = (float) (card.y+card.height);//(float) (height-2*buttonHeight);
		
		
		abilities[4].x = width-buttonWidth;
		abilities[4].y = (float) (4.5*buttonHeight);
		
		abilities[5].x = width-buttonWidth;
		abilities[5].y = (float) (3*buttonHeight);
		*/
		for(Button ability : abilities){
			ability.setSize(buttonWidth, buttonHeight);
		}
		this.layout();
	}
}
