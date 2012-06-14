package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.core.User;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class BuyUnitPopup extends Popup{
	
	public static String BUY_TEXT = "Buy";
	public static String CANCEL_TEXT = "Cancel";
	
	private TextButton buy, cancel;
	private UnitTypeCard card;
	private Button[] units;
	private UnitType[] types;
	private Button selectedButton;
	private Player opener;
	
	public BuyUnitPopup(UnitType... types) {
		super(new PopupHandler() {
			@Override
			public void handlePopupAction(String text) {
				BuyUnitPopup popup = (BuyUnitPopup) Popup.currentPopup;
				if (text.equals(BUY_TEXT)) {
					UnitType type = popup.card.type;
					Gdx.app.log("BuyUnitPopup", String.format("User wants to buy '%s'.", type.name));
					// TODO: Display somehow that buying unit failed
					GEngine.getInstance().selectionHandler.selectSpawn(type);
					popup.close();
				}
				else if (text.equals(CANCEL_TEXT)) {
					Popup.currentPopup.close();
				}
			}
		}, "");
		this.types = types;
		buy    = ComponentFactory.createButton(BUY_TEXT, null, cl);
		cancel = ComponentFactory.createButton(CANCEL_TEXT, null, cl);
		units = new Button[6];
		card = new UnitTypeCard(types[0]);
		
		for(int i=0; i<units.length; i++){
			final int b = i;
			units[i] = ComponentFactory.createButton(GUnit.getUnitPortrait(types[i]), "default",
					new ClickListener() {
				@Override
			    public void click(Actor actor,float x,float y ){
			        selectButton(b);
			    }
			});
		}
		selectButton(0);
		
		this.setBackground(Assets.SKIN.getPatch("empty"));
		
		for(Button ib : units) {
			this.addActor(ib);
		}
		
		this.addActor(buy);
		this.addActor(cancel);
		this.addActor(card);
	}
	
	/**
	 * Check if the ability is afforded before showing popup.
	 */
	@Override public void show(){
		State state = State.activeState;
		if(state.gameInstance.isAiGame())
			opener = state.players[0].ai!=null?state.players[1]:state.players[0];
		else if(state.gameInstance instanceof NetworkGameInstance)
			opener = state.players[0].userID==User.globalUserID?state.players[0]:state.players[1];
		else
			opener = State.activeState.getCurrentPlayer();
		checkUnitAffordable();
		super.show();
	}
	
	public void selectButton(int button) {
		if (selectedButton != null)
			selectedButton.setChecked(false);
		selectedButton = units[button];
		selectedButton.setChecked(true);
		card.setType(types[button]);
		card.setSize(card.width, card.height);
		checkUnitAffordable();
	}
	
	/**
	 * Disables buy button if unit is too expensive.
	 * 
	 * NOTE: Do other things, like graying the unit portrait of all units that can't be bought.
	 */
	public void checkUnitAffordable(){
		if(State.activeState==null || opener==null)
			return;
		
		int gold = opener.gold;
		boolean isPlayerTurn = State.activeState.getCurrentPlayer().userID == opener.userID;
		
		//Disable buy button if current unit is not affordable
		boolean canBuy = gold>=card.type.cost;
		Assets.SKIN.setEnabled(buy, canBuy && isPlayerTurn);
		card.setDisabled(!canBuy);
		
		//Mark other units that are too expensive.		TODO: Just gray unit portraits or something instead, as current method fucks with button presses.
		for(int i=0; i<types.length; i++){
			if(gold<types[i].cost)
				units[i].setStyle(Assets.SKIN.getStyle("default-disabled", ButtonStyle.class));
			else
				units[i].setStyle(Assets.SKIN.getStyle("default", ButtonStyle.class));
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
		
		buy.x = (float) (0.25*buttonWidth);
		buy.y = (float) (buttonHeight);
		buy.width = buttonWidth*1.5f;
		buy.height = buttonHeight;
		
		cancel.x = (float) (width-1.75*buttonWidth);
		cancel.y = (float) (buttonHeight);
		cancel.width = buttonWidth*1.5f;
		cancel.height = buttonHeight;
		
		card.x = (width-cardWidth)/2 - 2*Main.percentWidth;//width/4-2*Main.percentWidth;
		card.y = (float) (2.25*buttonHeight)-Main.percentHeight;
		card.setSize(cardWidth+4*Main.percentWidth, cardHeight+2*Main.percentHeight);
		
		units[0].x = 0;
		units[0].y = (float) (3*buttonHeight);
		
		units[1].x = 0;
		units[1].y = (float) (4.5*buttonHeight);
		
		units[2].x = (float) (0.6*buttonWidth);
		units[2].y = (float) (card.y+card.height);//(float) (height-2*buttonHeight);
		
		units[3].x = (float) (width-1.6*buttonWidth);
		units[3].y = (float) (card.y+card.height);//(float) (height-2*buttonHeight);
		
		units[4].x = width-buttonWidth;
		units[4].y = (float) (4.5*buttonHeight);
		
		units[5].x = width-buttonWidth;
		units[5].y = (float) (3*buttonHeight);
		
		for(Button unit : units){
			unit.width = buttonWidth;
			unit.height = buttonHeight;
		}
		this.layout();
	}
}
