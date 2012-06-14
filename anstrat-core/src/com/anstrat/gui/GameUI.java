package com.anstrat.gui;

import com.anstrat.command.CaptureCommand;
import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.core.User;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.guiComponent.ValueDisplay;
import com.anstrat.menu.MainMenu;
import com.anstrat.popup.Popup;
import com.anstrat.popup.PopupHandler;
import com.anstrat.popup.TutorialPopup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class GameUI extends UI {
	
	public final static int MAX_ABILITIES = 2;
	public final static int MAX_EFFECTS = 5;
	
	public int nrShownAbilities=2;
	public int nrShownEffects=3;
	
	private int lastWidth, lastHeight;
	private Unit lastShownUnit;
	
	//Top panel
	public Table topPanel, turntable;
	private Button endTurnButton;
	private ValueDisplay goldDisplay, manaDisplay;
	private Label turnDisplay;
	public Label fpsLabel;
	
	//Bottom panel
	public Table bottomPanel;
	private Button deselectButton;
	private Label nameLabel;
	private Image selectedImage;
	private Image tempCaptureImage;
	
	//Permanent panel
	public Table permanentPanel;
	private Button buyButton, helpButton, spellButton;
	
	//Unit version
	private Table unitTable;
	private Label apLabel;
	private ValueDisplay hpDisplay;
	private ValueDisplay apDisplay;
	private Button[] abilityButton = new Button[MAX_ABILITIES];
	private Image[] effectImage = new Image[MAX_EFFECTS];
	private Button captureButton;

	public GameUI(OrthographicCamera camera){
		super(Main.getInstance().batch, camera);
		
        /**
         * TOP PANEL
         */
		topPanel = new Table();
        
		topPanel.setBackground(new NinePatch(Assets.getTextureRegion("TopPanel")));
		
        endTurnButton = new TextButton("End Turn", Assets.SKIN);
        endTurnButton.setClickListener(new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	GEngine.getInstance().actionHandler.endTurnPress();
            }
        });
        goldDisplay = new ValueDisplay(ValueDisplay.VALUE_GOLD);
		manaDisplay = new ValueDisplay(ValueDisplay.VALUE_MANA);
		turnDisplay = new Label("",Assets.SKIN);
		turntable = new Table();
		NinePatch npBack = new NinePatch(Assets.WHITE);
		npBack.setColor(Color.LIGHT_GRAY);											// COLOR FOR NAME LABEL BACKGROUND
		turntable.setBackground(npBack);
		Table temp = new Table();
		NinePatch np = Assets.SKIN.getPatch("single-border");
		temp.setBackground(np);
		temp.add(turnDisplay).align("center");
		turntable.register("cont", temp);
		turntable.parse("align:center padding:"+-(int)(np.getPatches()[0].getRegionHeight()/2)+" [cont] fill expand");
		fpsLabel = new Label("",Assets.SKIN);
		fpsLabel.setColor(Color.LIGHT_GRAY);
		
		topPanel.addActor(fpsLabel);
        
        addActor(topPanel);
        
        
        /**
         * BOTTOM PANEL
         */
        bottomPanel = new Table();
        unitTable = new Table();
        unitTable.visible = false;
        permanentPanel = new Table();
		unitTable.setBackground(new NinePatch(Assets.getTextureRegion("BottomLargePanel")));
		permanentPanel.setBackground(new NinePatch(Assets.getTextureRegion("BottomSmallPanel")));
        
        /**
         * PERMANENT
         */
        buyButton = new Button(new Image(Assets.getTextureRegion("buy")), Assets.SKIN.getStyle("image", ButtonStyle.class));
        buyButton.setClickListener(new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Popup.buyUnitPopup.show();
            }
        } );
        helpButton = new Button(new Image(Assets.getTextureRegion("help-button")), Assets.SKIN.getStyle("image", ButtonStyle.class));
        helpButton.setClickListener(new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	new TutorialPopup().show();
            }
        } );
        
        spellButton = new Button(new Image(Assets.getTextureRegion("mana")), Assets.SKIN.getStyle("image", ButtonStyle.class));
        spellButton.setClickListener(new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Popup.abilityPopup.show();
            }
        } );
      
        /**
        * UNIT PANEL
        */
        hpDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_HP);
		apDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_AP);
        tempCaptureImage = new Image(Assets.getTextureRegion("capture-button-blue"));
        captureButton = new Button(tempCaptureImage, Assets.SKIN.getStyle("image", ButtonStyle.class));
        captureButton.setClickListener(new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	GEngine.getInstance().actionHandler.capturePress();
            }
        } );
        deselectButton = new Button(new Image(Assets.getTextureRegion("cancel")), Assets.SKIN.getStyle("image", ButtonStyle.class));
        deselectButton.setClickListener(new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	GEngine.getInstance().actionHandler.deselectPress();
            }
        } );
        for(int i=0; i<MAX_ABILITIES; i++){
        	abilityButton[i] = new Button(new Image(Assets.getTextureRegion("Ok-button")), Assets.SKIN.getStyle("image", ButtonStyle.class));
        	final int abilityId = i;
        	abilityButton[i].setClickListener(new ClickListener() {
                @Override
                public void click(Actor actor,float x,float y ){
                	GEngine.getInstance().actionHandler.abilityPress(abilityId);
                }
            } );
        	unitTable.addActor(abilityButton[i]);
        }
        for(int i=0; i<MAX_EFFECTS; i++){
        	effectImage[i] = new Image(Assets.getTextureRegion("grid"));
        	effectImage[i].visible = false;
        	unitTable.addActor(effectImage[i]);
        }
        nameLabel = new Label("name",Assets.SKIN);
        selectedImage = new Image(Assets.getTextureRegion("grid"));
        selectedImage.setClickListener(new ClickListener() {
                @Override
                public void click(Actor actor,float x,float y ){
                	if(GEngine.getInstance().selectionHandler.selectedUnit!=null)
                		Popup.unitInfoPopup.show(GEngine.getInstance().selectionHandler.selectedUnit);
                }
            } );
        apLabel = new Label("AP: XXXX", Assets.SKIN);
        
        addActor(bottomPanel);
        //Set sizes and positions initially so that others can access that info.
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public static void showVictoryPopup(String player){
		(new Popup(new PopupHandler(){
			@Override
			public void handlePopupAction(String popupButton)	{
				Main.getInstance().setScreen(MainMenu.getInstance());
				Popup.currentPopup.close();
				return;
			}
		}, "Game over", new Label(player+" has won the game!",Assets.SKIN), new TextButton("Ok",Assets.SKIN))).show();
	}
	
	public void resize(){
		resize(lastWidth, lastHeight);
	}
	
	public void resize(int width, int height){
		lastWidth = width;
		lastHeight = height;
		
		int bph = (int)Main.percentHeight*20;//hp*4;
		
		int pad = 2;
		int tph = (int)Main.percentHeight*10;//hp*2;
		
		//Top panel
		setBounds(topPanel, 0, height - tph, width, tph);
		topPanel.clear();
		topPanel.register("end", endTurnButton);
		topPanel.register("gold", goldDisplay);
		topPanel.register("mana", manaDisplay);
		topPanel.register("name", turntable);	//TODO: make sure name gets cut off if too long.
		topPanel.register("fps", fpsLabel);
		int padd = (int)(tph*0.1);
		topPanel.parse("align:left padding:"+pad +
				"* height:"+(int)(tph*0.8) +
				"[end] width:"+(int)(tph*2) +
				"[name] expand fill padb:"+padd*2+" padt:"+padd*2+" padl:"+padd+" padr:"+padd+
				"{*align:left [gold]} maxwidth:"+(int)(tph*1.5));	//---[mana]
		
		fpsLabel.x = width - Assets.UI_FONT.getBounds("00").width;
		fpsLabel.y = goldDisplay.y;
		
		// Permanent Panel
		float pwidth = bph*1.8f;
		setBounds(permanentPanel, width-pwidth, 0, pwidth, bph/2);
		permanentPanel.clear();
        permanentPanel.register("buy", buyButton);
        permanentPanel.register("help", helpButton);
        permanentPanel.register("spell", spellButton);
        permanentPanel.parse("align:right,bottom *size:"+(int)(bph/2)+" [][help][spell][buy]");
        
        // Unit table
        unitTable.clear();
        unitTable.register("hp", hpDisplay);
        unitTable.register("ap", apDisplay);
        unitTable.register("name", nameLabel);
        unitTable.register("portrait", selectedImage);
        unitTable.register("deselect", deselectButton);

        // Action + capture placement
        String buttonLayout = "";
 		if(true){
 			
 			int iconSize = (int)(bph/4);
 			for(int i=0; i<MAX_EFFECTS; i++){
 				if(effectImage[i].visible){
 					unitTable.register("ef"+i,effectImage[i]);
 	 				buttonLayout += "[ef"+i+"] size:"+iconSize;
 					effectImage[i].visible = true;
 				}
 			}
 			
 			if(abilityButton[0].visible){
 				unitTable.register("ab0",abilityButton[0]);
 				buttonLayout += "[ab0]";
 			}
 			if(abilityButton[1].visible){
 				unitTable.register("ab1",abilityButton[1]);
 				buttonLayout += "[ab1]";
 			}
 			if(captureButton.visible){
 		        unitTable.register("capture", captureButton);
 		        buttonLayout += "[capture]";
 			}
 		}
        
        unitTable.parse("align:top,left " +
        		"{debug * align:top,left" +
        			"[name] height:"+(int)(bph/6) +
        			"---" +
        			"[portrait] size:"+(int)(bph/2) +
        			"---" +
        			"[hp]" +
        			"---" +
        			"[ap]" +
        		"} paddingLeft:"+(int)(Main.percentWidth/2)+" paddingTop:"+(int)(Main.percentWidth) +
        		"{ * align:left size:"+(int)(bph/2) +
        			buttonLayout +
        		"} align:top,left expand:x fill:x" +
        		"[deselect] align:top,right size:"+(int)(bph/2));

		// Bottom panel
        setBounds(bottomPanel, 0,0 ,width,bph);
        bottomPanel.clear();
        bottomPanel.register("unit", unitTable);
        bottomPanel.register("permanent", permanentPanel);
        bottomPanel.parse(
        		"[unit] fill expand" +
        		"[permanent] ignore align:bottom,right height:"+(int)(bph/2));
	}
	
	/**
	 * If showing null, removes the display.
	 * @param unit
	 */
	public void showUnit(Unit unit){
		lastShownUnit = unit;
		if(unit == null){
			unitTable.visible = false;
			return;
		}
		unitTable.visible = true;
		selectedImage.setRegion( GUnit.getTextureRegion(unit.getUnitType()));
		if(State.activeState.currentPlayerId%2==0){
			tempCaptureImage.setRegion(Assets.getTextureRegion("capture-button-blue"));
		}
		else{
			tempCaptureImage.setRegion(Assets.getTextureRegion("capture-button-red"));	
		}
		nameLabel.setText(unit.getName());
		apDisplay.update(unit);
		hpDisplay.update(unit);
		for(Image i : effectImage){
			i.visible = false;
		}
		for(Button b : abilityButton){
			b.visible = false;
		}
		/*
		nrShownEffects=0;
		for(Effect e : unit.getEffects()){
			effectImage[nrShownEffects].setRegion(Assets.getTextureRegion(e.iconName));
			effectImage[nrShownEffects].visible = true;
			nrShownEffects++;
		}
		*/
		nrShownAbilities=0;
		for(Ability a : unit.getAbilities()){
			Actor actor = (abilityButton[nrShownAbilities].getActors().get(0));
			if(actor instanceof Image){
				((com.badlogic.gdx.scenes.scene2d.ui.Image) actor).setRegion(Assets.getTextureRegion(a.getIconName(unit)));
			}
			abilityButton[nrShownAbilities].visible = true;
			nrShownAbilities++;
		}
		
		// if a on a building
		Building building = State.activeState.map.getBuildingByTile(unit.tileCoordinate);
		if(building != null && new CaptureCommand(building, unit, State.activeState.getCurrentPlayer()).isAllowed()){
			captureButton.visible = true;
		}else{
			captureButton.visible = false;
		}
		
		resize(); // update positions of buttons etc.
	}
	
	/**
	 * Updates UI. (gold- & manadisplay + unit info)
	 */
	public void update(){
		goldDisplay.update(null);
		manaDisplay.update(null);
		showUnit(lastShownUnit);
	}
	
	/**
	 * Updates label of current player, also enables/disables the End Turn button.
	 */
	public void updateCurrentPlayer(){
		Player player = State.activeState.getCurrentPlayer();
		GameInstance game = State.activeState.gameInstance;
		String text;
		if(player.userID == User.globalUserID)
		{
			Assets.SKIN.setEnabled(endTurnButton, true);
			Assets.SKIN.setEnabled(buyButton, true);
			if(game instanceof NetworkGameInstance || game.isAiGame())	{
				text = "Your turn";		
			}
			else
				text = player.displayedName;
		} 
		else
		{
			text = player.displayedName;
			if(game instanceof NetworkGameInstance  || game.isAiGame()){
				Assets.SKIN.setEnabled(buyButton, false);
				Assets.SKIN.setEnabled(endTurnButton, false);
			}
		}
		Popup.buyUnitPopup.checkUnitAffordable();
		turnDisplay.setText(text);
		turnDisplay.setColor(player.getColor());
	}

	/**
	 * TODO solve in another way.
	 * @param x the x window coordinate
	 * @param y the y window coordinate
	 * @param count
	 * @return If it hit any interface element
	 */
	public boolean tap(int x, int y, int count){
		//y reversed compared to ui
		if(unitTable.visible){
			return (y <= topPanel.height || y>= Gdx.graphics.getHeight()-unitTable.height);
		}
		else
			return (y <= topPanel.height || y>= Gdx.graphics.getHeight()-permanentPanel.height);

	}
}
