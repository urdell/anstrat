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
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class BuyUnitPopup extends Popup{
	
	public static String BUY_TEXT = "Buy";
	public static String CANCEL_TEXT = "Cancel";
	
	private TextButton buy;
	private UnitTypeCard card;
	private Button[] units;
	private UnitType[] types;
	private Player opener;
	
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
		
		buy   = ComponentFactory.createButton(BUY_TEXT, Popup.OK, cl);
		units = new Button[6];
		card  = new UnitTypeCard(types[0]);
		this.register("buy",buy);
		this.register("cancel",ComponentFactory.createButton(CANCEL_TEXT, Popup.CANCEL, cl));
		this.register("card",card);
		
		for(int i=0; i<units.length; i++){
			Table tbl = new Table(Assets.SKIN);
			tbl.register("im",new Image(GUnit.getUnitPortrait(types[i])));
			tbl.setBackground(new NinePatch(Assets.getTextureRegion("empty-button")));
			tbl.parse("[im] center fill:60,60 paddingTop:"+(int)(-Main.percentHeight*0.5));
			units[i] = new Button(tbl, Assets.SKIN.getStyle("image",ButtonStyle.class));
			units[i].setClickListener(new ClickListener() {
				@Override
			    public void click(Actor actor,float x,float y ){
			        selectButton((Button)actor);
			    }
			});
			this.register("unit"+i, units[i]);
		}
		selectButton(units[0]);
		
		this.setBackground(Assets.SKIN.getPatch("empty"));

		int cardW = (int)(Main.percentWidth*60);
		int cardH = (int)(Main.percentHeight*50);
		int buttSize = (int)(((Main.percentWidth*100-cardW)/2));
		String buttOptions = "*size:"+buttSize+" padding:"+(int)(-buttSize*0.1);
		
		this.parse("align:center *expand:x fill:x "+
				"{"+buttOptions+" [unit2] [] [unit3] }" +
				"---" +
				"{" +
					"{"+buttOptions+" [unit0] --- [] --- [unit1] }" +
					"[card] height:"+cardH+" width:"+cardW +
					"{"+buttOptions+" [unit4] --- [] --- [unit5] }" +
				"}" +
				"---" +
				"{*min:1 height:"+(int)(Main.percentHeight*10)+" width:"+(int)(Main.percentHeight*10*2)+" [buy] paddingRight:"+(int)(Main.percentHeight*5)+" [cancel] }" +
						"paddingTop:"+(int)(Main.percentHeight*5));
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
	
	public void resize(int width, int height){
		overlay.setSize(width, height);
		this.width = width;
		this.height = height;
		this.x = this.y = 0;
	}
}
