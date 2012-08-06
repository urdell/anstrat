package com.anstrat.popup;

import java.util.Arrays;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ColorTable;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class BuyUnitPopup extends Popup{
	
	public static final String BUY_TEXT = "Buy";
	public static final String CANCEL_TEXT = "Cancel";
	public static final Color  COLOR_UNAVAILABLE = Color.DARK_GRAY;
	
	private Button buyButton;
	private UnitTypeCard card;
	private Button[] units;
	private NinePatch[] unitSilhouettes;
	private UnitType[] types;
	private Player opener;
	private ColorTable unitTable;
	
	private static final ClickListener BUY_BUTTON_LISTENER = new ClickListener() {
		@Override
		public void click(Actor actor, float x, float y) {
			BuyUnitPopup popup = (BuyUnitPopup) Popup.currentPopup;
			UnitType type = popup.card.type;
			Gdx.app.log("BuyUnitPopup", String.format("User wants to buy '%s'.", type.name));
			GEngine.getInstance().selectionHandler.selectSpawn(type);
			popup.close();
		}
	};
	
	public BuyUnitPopup(UnitType... types) {
		this.types = types;
		this.drawOverlay = false;
		
		buyButton = ComponentFactory.createButton(Assets.getTextureRegion("buy"), "image", BUY_BUTTON_LISTENER);
		Button buttonCancel = ComponentFactory.createButton(Assets.getTextureRegion("cancel"), "image", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		units = new Button[6];
		unitSilhouettes = new NinePatch[6];
		card  = new UnitTypeCard(types[0]);
		
		for(int i=0; i<units.length; i++){
			unitSilhouettes[i] = new NinePatch(GUnit.getTextureRegion(types[i]));
			units[i] = new Button(new Image(unitSilhouettes[i]), Assets.SKIN.getStyle("image",ButtonStyle.class));
			units[i].setClickListener(new ClickListener() {
				@Override
			    public void click(Actor actor,float x,float y ){
			        selectButton((Button)actor);
			    }
			});
		}
		
		int unitWidth = (int)(Main.percentWidth*100/6*1.3);
		int pad = (int)(-unitWidth*0.15);
		
		// The silhouettes of the purchasable units
		unitTable = new ColorTable(Color.BLUE);
		NinePatch unitTableBackgroundPatch = Assets.SKIN.getPatch("border-thick-updown");
		unitTable.setBackground(unitTableBackgroundPatch);
		unitTable.defaults().size(unitWidth).padLeft(pad).padRight(pad);
		
		// Don't ask me why it has to be this order...
		unitTable.add(units[0]);
		unitTable.add(units[3]);
		unitTable.add(units[1]);
		unitTable.add(units[2]);
		unitTable.add(units[5]);
		unitTable.add(units[4]);
		
		// The buy and close buttons
		Table buttonTable = new Table(Assets.SKIN);
		buttonTable.setBackground(new NinePatch(Assets.getTextureRegion("BottomLargePanel")));
		
		int buttonHeight = (int)(Main.percentHeight*15);
		
		buttonTable.right();
		buttonTable.defaults().size(buttonHeight);
		buttonTable.add(buyButton);
		buttonTable.add(buttonCancel);
		
		// Put all components together into the main table
		int cardW = (int)(Main.percentWidth*85);
		int cardH = (int)(Main.percentHeight*60);
		
		this.setBackground(Assets.SKIN.getPatch("empty")); // Overrides the default background with an empty one
		this.add(unitTable).width(Gdx.graphics.getWidth()).padTop((int)(-unitTableBackgroundPatch.getTopHeight()/3));
		this.row();
		this.add().expand().uniform();
		this.row();
		this.add(card).height(cardH).width(cardW);
		this.row();
		this.add().expand().uniform();
		this.row();
		this.add(buttonTable).height(buttonHeight).width(Gdx.graphics.getWidth()).expandY().bottom();
		
		selectButton(units[0]);
	}
	
	@Override
	public void resize(int width, int height){
		overlay.setSize(width, height);
		this.width = width;
		this.height = height;
		this.x = this.y = 0;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		super.draw(batch, parentAlpha);
	}
	
	/**
	 * Check if the unit is afforded before showing popup.
	 */
	@Override public void show(){
		opener = GameInstance.activeGame.getUserPlayer();
		checkUnitAffordable();
		
		unitTable.setColor(opener.getColor());
		card.setColor(opener.getColor());
		
		super.show();
	}
	
	/**
	 * Set specified button as selected. 
	 */
	public void selectButton(Button button) {
		card.setType(types[Arrays.asList(units).indexOf(button)]);
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
		boolean isPlayerTurn = State.activeState.getCurrentPlayer() == opener;
		
		//Disable buy button if current unit is not affordable
		boolean canBuy = gold>=card.type.cost;
		Assets.SKIN.setEnabled(buyButton, canBuy && isPlayerTurn);
		card.setDisabled(!canBuy);
		buyButton.visible = canBuy;
		
		//Mark units that are too expensive.
		for(int i=0; i<types.length; i++){
			if(gold<types[i].cost)
				unitSilhouettes[i].setColor(COLOR_UNAVAILABLE);	//Not enough money
			else
				unitSilhouettes[i].setColor(Color.WHITE);			//Enough money
		}
	}
}
