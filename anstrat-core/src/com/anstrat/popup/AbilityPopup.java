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
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Skeleton class for ability popup.
 * @author kalper
 */
public class AbilityPopup extends Popup {
	
	public static String CAST_TEXT = "Cast";
	public static String CANCEL_TEXT = "Cancel";
	
	private Button cast, cancel;
	private AbilityTypeCard card;
	private Table[] abilities;
	private PlayerAbilityType[] types;
	private Table selectedButton;
	private Table toboexx;

	private static final ClickListener CAST_BUTTON_LISTENER = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			AbilityPopup popup = (AbilityPopup) Popup.getCurrentPopup();
			PlayerAbilityType type = popup.card.type;
			PlayerAbility ability = PlayerAbilityFactory.createAbility(type, State.activeState.getCurrentPlayer());
			Gdx.app.log("AbilityPopup", String.format("User wants to cast '%s'.", type.name));
			if (ability instanceof TargetedPlayerAbility) {
				GEngine.getInstance().selectionHandler.selectPlayerAbility((TargetedPlayerAbility) ability, "confirm-ability");
			}
			else if (ability instanceof DoubleTargetedPlayerAbility) {
				GEngine.getInstance().selectionHandler.selectPlayerAbility((DoubleTargetedPlayerAbility) ability, "confirm-ability");
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
		GEngine.getInstance().selectionHandler.deselect();
		cast = ComponentFactory.createButton(CAST_TEXT, CAST_BUTTON_LISTENER);
		cancel = ComponentFactory.createButton(CANCEL_TEXT, Popup.POPUP_CLOSE_BUTTON_HANDLER);
		abilities = new Table[types.length];
		card = new AbilityTypeCard(types[0]);
		Image tempImage; // used to instantiate images for buttons.
		int ma0n = State.activeState.players[State.activeState.currentPlayerId].mana;
		this.drawOverlay = false;
		for(int i=0; i<abilities.length; i++){
			final int b = i;
			//TODO 
			//if(i==0)
			//	tempImage = new Image(Assets.getTextureRegion("magic-blessing-button"));
			//else if(i==1)
			//	tempImage = new Image(Assets.getTextureRegion("magic-swap-button"));
			//else
			//	tempImage = new Image(Assets.getTextureRegion("magic-comet-button"));
			abilities[i] = new Table(Assets.SKIN);
			abilities[i].setTouchable(Touchable.enabled);
			//tempImage.setFillParent(false);
			//tempImage.setAlign(Align.center);
			//tempImage.setAlign(Align.top);
			abilities[i].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
			        selectButton(b);
			    }
			});
		}
		
		toboexx = new Table();
		
		selectButton(0);
		
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("empty")));
		
		cast = ComponentFactory.createButton(Assets.getTextureRegion("cast-button"), "image", CAST_BUTTON_LISTENER);
		cancel = ComponentFactory.createButton(Assets.getTextureRegion("cancel"), "image", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		// The buy and close buttons
		Table buttonTable = new Table(Assets.SKIN);
		buttonTable.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("BottomLargePanel"))));
				
		float buttonHeight = Main.percentHeight*15f;
				
		buttonTable.align(Align.right);
		buttonTable.defaults().size(buttonHeight);
		buttonTable.add(cast).width(buttonHeight*1.25f);
		buttonTable.add(cancel);
		
		float bh = Main.percentHeight*10f;
		
		Table glowTable = new Table();
		float glowh = bh*0.9f;
		glowTable.defaults().left().top().width(glowh).height(glowh);
		glowTable.add(new Image(Assets.getTextureRegion(ma0n>=2?"magic-one-glowing":"magic-one")));
		glowTable.add();
		glowTable.add(new Image(Assets.getTextureRegion(ma0n>=4?"magic-two-glowing":"magic-two")));
		glowTable.add();
		glowTable.add(new Image(Assets.getTextureRegion(ma0n>=6?"magic-three-glowing":"magic-three")));
		
		Table abilityTable = new Table();
		
		float abilh = Gdx.graphics.getWidth()*0.3f;
		abilityTable.defaults().left().top().width(abilh).height(abilh);
		abilityTable.add(abilities[0]);//.height(bh*2f).width(bh*2f);
		//abilityTable.add();
		abilityTable.add(abilities[1]);//.height(bh*2f).width(bh*2f);
		//abilityTable.add();
		abilityTable.add(abilities[2]);//.height(bh*2f).width(bh*2f);
		
		/*Table bottom = new Table();
		bottom.defaults().left().top().size(bw, bh);
		bottom.add(cast);
		bottom.add().fill();
		bottom.add(cancel);*/
		
		float space = Gdx.graphics.getHeight() - buttonHeight - glowh - abilh - Gdx.graphics.getWidth()/2f;
		
		this.defaults().top();
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("empty")));
		
		this.add(glowTable).width(Gdx.graphics.getWidth()).padTop(bh*1.5f);
		this.row();
		this.add(abilityTable).width(Gdx.graphics.getWidth());
		//this.add(card).height(Main.percentHeight * 60);
		this.row();
		this.add(toboexx).width(Gdx.graphics.getWidth()).height(Gdx.graphics.getWidth()/2f);
		this.row();
		this.add().height(space).uniform();
		this.row();
		this.add(buttonTable).height(buttonHeight).width(Gdx.graphics.getWidth()).align(Align.bottom).padTop(-bh*1.5f);
	}
	
	/**
	 * Check if the unit is buyable before showing popup.
	 */
	@Override public void show(){
		update();
		super.show();
	}
	
	public void selectButton(int button) {
		updateCast(button);
		
		abilities[0].setBackground(
				new TextureRegionDrawable(
						Assets.getTextureRegion("magic-blessing-button"+(button==0?"":"-fade"))));
		abilities[1].setBackground(
				new TextureRegionDrawable(
						Assets.getTextureRegion("magic-swap-button"+(button==1?"":"-fade"))));
		abilities[2].setBackground(
				new TextureRegionDrawable(
						Assets.getTextureRegion("magic-comet-button"+(button==2?"":"-fade"))));
		
		if(button==0)
			toboexx.setBackground(new TextureRegionDrawable(
					Assets.getTextureRegion("magic-blessing")));
		else if(button==1)
			toboexx.setBackground(new TextureRegionDrawable(
					Assets.getTextureRegion("magic-swap")));
		else if(button==2)
			toboexx.setBackground(new TextureRegionDrawable(
					Assets.getTextureRegion("magic-comet")));
		
		//if (selectedButton != null)
		//	selectedButton.setChecked(false);
		//selectedButton = abilities[button];
		//selectedButton.setChecked(true);
		card.setType(types[button]);
		card.setSize(card.getWidth(), card.getHeight());
		update();
	}
	
	public void updateCast(int index){
		int mana = GameInstance.activeGame.getUserPlayer().mana;
		if(mana<types[index].manaCost)
			cast.setVisible(false);
		else
			cast.setVisible(true);
	}
	
	public void update(){
		if(State.activeState==null)
			return;
		
		int mana = GameInstance.activeGame.getUserPlayer().mana;
		boolean isPlayerTurn = GameInstance.activeGame.isUserCurrentPlayer();
		
		//Disable cast button if current ability is not affordable
		boolean canCast = mana >= card.type.manaCost;
		//Assets.SKIN.setEnabled(cast, canCast && isPlayerTurn);
		//card.setDisabled(!canCast);
	}
	
	@Override
	public void resize(float width, float height){
		this.setSize(width, height);
		super.resize(width, height);
	}
}