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
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class BuyUnitPopup extends Popup{
	
	public static final String BUY_TEXT = "Buy";
	public static final String CANCEL_TEXT = "Cancel";
	public static final Color  COLOR_UNAVAILABLE = Color.BLACK;
	
	private Button buyButton;
	private UnitTypeCard card;
	private Button[] units;
	private NinePatch[] unitSilhouettes;
	private UnitType[] types;
	private Table unitTable, asdf;
	
	private static final ClickListener BUY_BUTTON_LISTENER = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			BuyUnitPopup popup = (BuyUnitPopup) Popup.getCurrentPopup();
			UnitType type = popup.card.type;
			Gdx.app.log("BuyUnitPopup", String.format("User wants to buy '%s'.", type.name));
			GEngine.getInstance().selectionHandler.selectSpawn(type);
			popup.close();
		}
	};
	
	public BuyUnitPopup(UnitType... types) {
		this.setMovable(false);
		this.types = types;
		this.drawOverlay = false;
		
		buyButton = ComponentFactory.createButton(Assets.getTextureRegion("hire-button"), "image", BUY_BUTTON_LISTENER);
		Button buttonCancel = ComponentFactory.createButton(Assets.getTextureRegion("cancel"), "image", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		//buyButton = new TextButton("Buybrrarggaggalghlll",Assets.SKIN);
		
		units = new Button[6];
		unitSilhouettes = new NinePatch[6];
		card  = new UnitTypeCard(true);
		
		for(int i=0; i<units.length; i++){
			unitSilhouettes[i] = new NinePatch(GUnit.getTextureRegion(types[i]));
			units[i] = new Button(new Image(unitSilhouettes[i]), Assets.SKIN.get("image",ButtonStyle.class));
			units[i].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
			        selectButton((Button)event.getListenerActor());
			    }
			});
		}
		
		float unitWidth = Main.percentWidth*100f/6f*1.3f;
		float unitPad   = -unitWidth*0.15f;
		
		// The silhouettes of the purchasable units
		unitTable = new Table(Assets.SKIN);
		NinePatch unitTableBackgroundPatch = Assets.SKIN.getPatch("border-thick-updown");
		//unitTable.setBackground(new NinePatchDrawable(unitTableBackgroundPatch));
		unitTable.defaults().size(unitWidth).padLeft(unitPad).padRight(unitPad);
		
		for(Button unit : units)
			unitTable.add(unit);
	
		// The buy and close buttons
		Table buttonTable = new Table(Assets.SKIN);
		buttonTable.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("BottomLargePanel"))));
		
		float buttonHeight = Main.percentHeight*15f;
		
		buttonTable.align(Align.right);
		buttonTable.defaults().size(buttonHeight);
		buttonTable.add(buyButton).width(buttonHeight*2f);
		buttonTable.add(buttonCancel);
		
		// Put all components together into the main table
		float cardH = Main.percentHeight*50f;
		
		float space = (Gdx.graphics.getHeight() - cardH - unitWidth - buttonHeight
				//- unitTableBackgroundPatch.getBottomHeight()
				//- unitTableBackgroundPatch.getTopHeight()/4f
				) / 2f;
		
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("empty"))); // Overrides the default background with an empty one
		this.add(unitTable).width(Gdx.graphics.getWidth()).padTop(Main.percentHeight*10f);
		this.row();
		this.add().height(space).uniform();	//space
		this.row();
		asdf = new Table();
		this.add(asdf).height(cardH).width(Gdx.graphics.getWidth()).padTop(-Main.percentHeight*10f);
		//this.add(card).height(cardH).width(Gdx.graphics.getWidth());
		this.row();
		this.add().uniform();				//space
		this.row();
		this.add(buttonTable).height(buttonHeight).width(Gdx.graphics.getWidth()).align(Align.bottom);
		
		selectButton(units[0]);
	}
	
	@Override
	public void resize(float width, float height){
		// Force popup to take up the whole window
		this.setSize(width, height);
		super.resize(width, height);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		super.draw(batch, parentAlpha);
	}
	
	/**
	 * Check if the unit is afforded before showing popup.
	 */
	@Override 
	public void show(){
		Player userPlayer = GameInstance.activeGame.getUserPlayer();
		update();
		
		unitTable.setColor(userPlayer.getColor());
		card.setColor(userPlayer.getColor());
		GEngine.getInstance().userInterface.setVisible(false);
		
		super.show();
	}
	
	@Override public void close(){
		GEngine.getInstance().userInterface.setVisible(true);
		super.close();
	}
	
	/**
	 * Set specified button as selected. 
	 */
	public void selectButton(Button button) {
		card.setType(types[Arrays.asList(units).indexOf(button)]);
		
		UnitType ut = types[Arrays.asList(units).indexOf(button)];
		String stuf = "";
		
		switch(ut){
			case AXE_THROWER:
				stuf = "axethrower";
				break;
			case BERSERKER:
				stuf = "berserker";
				break;
			case DARK_ELF:
				stuf = "elf";
				break;
			case FALLEN_WARRIOR:
				stuf = "fallen";
				break;
			case GOBLIN_SHAMAN:
				stuf = "goblin";
				break;
			case HAWK:
				stuf = "hawk";
				break;
			case JOTUN:
				stuf = "jotun";
				break;
			case SHAMAN:
				stuf = "shaman";
				break;
			case SWORD:
				stuf = "swordsman";
				break;
			case TROLL:
				stuf = "troll";
				break;
			case VALKYRIE:
				stuf = "valkyrie";
				break;
			case WOLF:
				stuf = "wolf";
				break;
		}
		
		asdf.setBackground(new TextureRegionDrawable(Assets.getTextureRegion("buy-"+stuf)));
		
		update();
	}
	
	public void update(){
		Player userPlayer = GameInstance.activeGame.getUserPlayer();
		
		int gold = userPlayer.gold;
		boolean isPlayerTurn = State.activeState.getCurrentPlayer().equals(userPlayer);
		
		// Disable buy button if current unit is not affordable
		boolean canBuy = gold >= card.type.cost;
		Assets.SKIN.setEnabled(buyButton, canBuy && isPlayerTurn);
		card.setDisabled(!canBuy);
		buyButton.setVisible(isPlayerTurn && canBuy);
		
		// Mark units that are too expensive.
		for(int i=0; i<types.length; i++){
			if(gold<types[i].cost)
				unitSilhouettes[i].setColor(COLOR_UNAVAILABLE);	    //Not enough money
			else
				unitSilhouettes[i].setColor(Color.WHITE);			//Enough money
		}
	}
}
