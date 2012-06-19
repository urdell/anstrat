package com.anstrat.popup;

import java.util.Arrays;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.core.User;
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
	
	public static String BUY_TEXT = "Buy";
	public static String CANCEL_TEXT = "Cancel";
	
	private Button buy;
	private UnitTypeCard card;
	private Button[] units;
	private UnitType[] types;
	private Player opener;
	private ColorTable unitTable;
	
	private ClickListener cl = new ClickListener() {
        @Override
        public void click(Actor actor,float x,float y ){
        	handler.handlePopupAction(actor.equals(buy)?Popup.OK:Popup.CANCEL);
        }
    };
	
	public BuyUnitPopup(UnitType... types) {
		super(new PopupListener() {
			@Override
			public void handle(String text) {
				BuyUnitPopup popup = (BuyUnitPopup) Popup.currentPopup;
				UnitType type = popup.card.type;
				Gdx.app.log("BuyUnitPopup", String.format("User wants to buy '%s'.", type.name));
				GEngine.getInstance().selectionHandler.selectSpawn(type);
			}
		}, "");
		this.types = types;
		
		buy   = ComponentFactory.createButton(Assets.getTextureRegion("buy"), "image", cl);
		units = new Button[6];
		card  = new UnitTypeCard(types[0]);
		this.register("card",card);
		this.drawOverlay = false;
		
		unitTable = new ColorTable(Color.BLUE);
		
		for(int i=0; i<units.length; i++){
			units[i] = new Button(new Image(GUnit.getTextureRegion(types[i])), Assets.SKIN.getStyle("image",ButtonStyle.class));
			units[i].setClickListener(new ClickListener() {
				@Override
			    public void click(Actor actor,float x,float y ){
			        selectButton((Button)actor);
			    }
			});
			unitTable.register("unit"+i, units[i]);
		}
		int unitWidth = (int)(Main.percentWidth*100/6*1.3);
		int pad = (int)(-unitWidth*0.15);
		unitTable.parse("align:center" +
				"*size:"+unitWidth+" paddingLeft:"+pad+" paddingRight:"+pad +
				"[unit0][unit3][unit1][unit2][unit5][unit4]");
		unitTable.setBackground(Assets.SKIN.getPatch("border-thick-updown"));
		this.register("units", unitTable);
		
		
		Table buttonTable = new Table(Assets.SKIN);
		buttonTable.register("buy", buy);
		buttonTable.register("cancel",ComponentFactory.createButton(Assets.getTextureRegion("cancel"), "image", cl));
		int buttonHeight = (int)(Main.percentHeight*15);
		buttonTable.parse("align:right" +
				"*min:1 size:"+buttonHeight+" [cancel] [buy]");
		buttonTable.setBackground(new NinePatch(Assets.getTextureRegion("BottomLargePanel")));
		this.register("buttons", buttonTable);


		int cardW = (int)(Main.percentWidth*85);
		int cardH = (int)(Main.percentHeight*60);
		this.parse("align:center *expand:x fill:x "+
				"[units] expand:x fill:x paddingTop:"+(int)(-unitTable.getBackgroundPatch().getTopHeight()/3) +
				"--- [] fill expand uniform ---" +
				"[card] height:"+cardH+" width:"+cardW +
				"--- [] fill expand uniform ---" +
				"[buttons] expand:x fill:x height:"+buttonHeight);
		
		
		selectButton(units[0]);
		this.setBackground(Assets.SKIN.getPatch("empty"));
	}
	
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
		State state = State.activeState;
		if(state.gameInstance.isAiGame())
			opener = state.players[0].ai!=null?state.players[1]:state.players[0];
		else if(state.gameInstance instanceof NetworkGameInstance)
			opener = state.players[0].userID==User.globalUserID?state.players[0]:state.players[1];
		else
			opener = State.activeState.getCurrentPlayer();
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
		/*for(int i=0; i<types.length; i++){
			if(gold<types[i].cost)
				units[i].setStyle(Assets.SKIN.getStyle("image-disabled", ButtonStyle.class));
			else
				units[i].setStyle(Assets.SKIN.getStyle("image", ButtonStyle.class));
		}*/
	}
}
