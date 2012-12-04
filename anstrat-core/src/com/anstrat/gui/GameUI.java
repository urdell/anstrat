package com.anstrat.gui;

import com.anstrat.command.CaptureCommand;
import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.GameInstanceType;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.guiComponent.ColorTable;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.ValueDisplay;
import com.anstrat.menu.MainMenu;
import com.anstrat.popup.AbilityPopup;
import com.anstrat.popup.BuyUnitPopup;
import com.anstrat.popup.Popup;
import com.anstrat.popup.TutorialPopup;
import com.anstrat.popup.UnitInfoPopup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class GameUI extends UI {
	
	public final static int MAX_ABILITIES = 2;
	public final static int MAX_EFFECTS = 5;
	
	public int nrShownAbilities=2;
	public int nrShownEffects=3;
	
	private float lastWidth, lastHeight;
	private Unit lastShownUnit;
	
	//Top panel
	public Table topPanel;
	private ColorTable turntable;
	private Button endTurnButton;
	private ValueDisplay goldDisplay, manaDisplay;
	private Label turnDisplay;
	
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
	private ValueDisplay hpDisplay;
	private ValueDisplay apDisplay;
	private Button[] abilityButtons = new Button[MAX_ABILITIES];
	private Image[] effectImage = new Image[MAX_EFFECTS];
	private Button captureButton;
	
	private BuyUnitPopup[] buyUnitPopups = new BuyUnitPopup[UnitType.TEAMS.length];
	private BuyUnitPopup openBuyUnitPopup;
	
	private AbilityPopup[] abilityPopups = new AbilityPopup[PlayerAbilityType.GODS.length];
	private AbilityPopup openAbilityPopup;
	
	private UnitInfoPopup unitInfoPopup;

	public GameUI(OrthographicCamera camera){
		super(Main.getInstance().batch, camera);
		
        /**
         * TOP PANEL
         */
		topPanel = new Table();
        
		topPanel.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("TopPanel"))));
		
        endTurnButton = new TextButton("End Turn", Assets.SKIN);
        endTurnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	GEngine.getInstance().actionHandler.endTurnPress();
            }
        });
        goldDisplay = new ValueDisplay(ValueDisplay.VALUE_GOLD);
		manaDisplay = new ValueDisplay(ValueDisplay.VALUE_MANA);
		turnDisplay = new Label("",Assets.SKIN);
		turntable = new ColorTable(Color.BLACK);
		turntable.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		turntable.add(turnDisplay).center();
        
        addActor(topPanel);
        
        
        /**
         * BOTTOM PANEL
         */
        bottomPanel = new Table();
        unitTable = new Table();
        unitTable.setVisible(false);
        permanentPanel = new Table();
		unitTable.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("BottomLargePanel"))));
		permanentPanel.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("BottomSmallPanel"))));
        
        /**
         * PERMANENT
         */
        buyButton = new Button(new Image(Assets.getTextureRegion("buy")), Assets.SKIN.get("image", ButtonStyle.class));
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	showBuyUnitPopup();
            }
        } );
        helpButton = new Button(new Image(Assets.getTextureRegion("help-button")), Assets.SKIN.get("image", ButtonStyle.class));
        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	new TutorialPopup().show();
            }
        } );
        
        spellButton = new Button(new Image(Assets.getTextureRegion("magic-button")), Assets.SKIN.get("image", ButtonStyle.class));
        spellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	showAbilityPopup();
            }
        } );
      
        /**
        * UNIT PANEL
        */
        hpDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_HP);
		apDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_AP);
        tempCaptureImage = new Image(Assets.getTextureRegion("capture-button-blue"));
        captureButton = new Button(tempCaptureImage, Assets.SKIN.get("image", ButtonStyle.class));
        captureButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	GEngine.getInstance().actionHandler.capturePress();
            }
        } );
        deselectButton = new Button(new Image(Assets.getTextureRegion("cancel")), Assets.SKIN.get("image", ButtonStyle.class));
        deselectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	GEngine.getInstance().actionHandler.deselectPress();
            }
        } );
        for(int i=0; i<MAX_ABILITIES; i++){
        	//abilityButtons[i] = new Button(new Image(Assets.getTextureRegion("Ok-button")), Assets.SKIN.getStyle("image", ButtonStyle.class));
        	abilityButtons[i] = new Button(new AbilityButton(), Assets.SKIN.get("image", ButtonStyle.class));
        	
        	final int abilityIndex = i;
        	abilityButtons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                	GEngine.getInstance().actionHandler.abilityPress(abilityIndex);
                }
            } );
        	unitTable.addActor(abilityButtons[i]);
        }
        for(int i=0; i<MAX_EFFECTS; i++){
        	effectImage[i] = new Image(Assets.getTextureRegion("grid"));
        	effectImage[i].setVisible(false);
        	unitTable.addActor(effectImage[i]);
        }
        nameLabel = new Label("name",Assets.SKIN);
        selectedImage = new Image(Assets.getTextureRegion("grid"));
        selectedImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                	if(GEngine.getInstance().selectionHandler.selectedUnit!=null)
                		unitInfoPopup.show(GEngine.getInstance().selectionHandler.selectedUnit);
                }
            } );
        
        addActor(bottomPanel);
        
        unitInfoPopup = new UnitInfoPopup();
        
        //Set sizes and positions initially so that others can access that info.
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public static void showVictoryPopup(String player){
		(new Popup("Game over", 
		new Label(player+" has won the game!",Assets.SKIN), 
		ComponentFactory.createButton("Ok", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Main.getInstance().setScreen(MainMenu.getInstance());
				Popup.getCurrentPopup().close();
			}
		}))).show();
	}
	
	public void resize(){
		resize(lastWidth, lastHeight);
	}
	
	@Override
	public void resize(float width, float height){
		lastWidth = width;
		lastHeight = height;
		
		float bph = Main.percentWidth*30f;  // Bottom panel height is based on screen width to maintain aspect ratio
		float pad = 2f;
		float tph = Main.percentHeight*9f;  // Top panel height is based on screen height because there is no need for strict width
		
		//Top panel
		topPanel.setBounds(0, height - tph, width, tph);
		float padh = tph*0.1f;
		float padv = -Assets.NinePatchUtils.getTopLeft(Assets.getTextureRegion("border-thin"), 15, 15, 15, 15).getRegionHeight()/2f;
		topPanel.clear();

		// Top panel non-json
		topPanel.row().left().pad(pad).fill().height(tph*0.8f);
		topPanel.add(endTurnButton).width(tph*2f);
		topPanel.add(turntable).pad(padv, padh, padv, padh).expand().fill();
		Table displays = new Table();
		displays.add(goldDisplay).align(Align.left);
		displays.row();
		displays.add(manaDisplay).align(Align.left);
		topPanel.add(displays);
		
		// Permanent Panel
		float pwidth = bph*1.8f;
		permanentPanel.setBounds(width-pwidth, 0, pwidth, bph/2f);
		permanentPanel.clear();
		
        permanentPanel.row().right().bottom().fill().size(bph/2f);
        permanentPanel.add(helpButton);
        permanentPanel.add(spellButton);
        permanentPanel.add(buyButton);
        
        // Unit table
        layoutUnitTable();
        
		// Bottom panel
        bottomPanel.setBounds(0,0 ,width,bph);
        bottomPanel.clear();
        bottomPanel.add(unitTable);
        bottomPanel.add(permanentPanel).ignore().bottom().right().height(bph/2f);
	}
	
	private void layoutUnitTable(){
		float bph = Main.percentWidth * 30;
        
        unitTable.clear();
        unitTable.row().expand().pad(2f).top();
        
        // Left table, portrait, name, ap, health
        Table table1 = new Table();
        table1.defaults().top().left();
        table1.row();
        table1.add(nameLabel).height(bph/6f);
        table1.row();
        table1.add(selectedImage).size(bph/2f);
        table1.row();
        table1.add(hpDisplay);
        table1.row();
        table1.add(apDisplay);
        
        // Right table, effects + abilities
        Table table2 = new Table();
        
		// Abilities
		for(Button b : abilityButtons){
			if(b.isVisible()) table2.add(b).size(bph/2f);
		}
		
		// Capture button
		if(captureButton.isVisible()) table2.add(captureButton).size(bph/2f);
		
		// Deselect button
		table2.add(deselectButton).size(bph/2f).right();
		
		unitTable.add(table1).left();
		unitTable.add(table2).expand().right();
	}
	
	/**
	 * Used for unit spawn
	 * @param unit
	 */
	public void showUnitType(UnitType unit){
		unitTable.setVisible(true);
		selectedImage.setDrawable(new TextureRegionDrawable( GUnit.getUnitPortrait(unit)));
		nameLabel.setText(unit.name);
		
		for(Image i : effectImage){
			i.setVisible(false);
		}
		for(Button b : abilityButtons){
			b.setVisible(false);
		}
		nrShownAbilities=0;
		captureButton.setVisible(false);
	}
	
	/**
	 * If showing null, removes the display.
	 * @param unit
	 */
	public void showUnit(Unit unit){
		lastShownUnit = unit;
		if(unit == null){
			unitTable.setVisible(false);
			return;
		}
		unitTable.setVisible(true);
		selectedImage.setDrawable(new TextureRegionDrawable( GUnit.getUnitPortrait(unit.getUnitType())));
		if(State.activeState.currentPlayerId%2==0){
			tempCaptureImage.setDrawable(new TextureRegionDrawable(Assets.getTextureRegion("capture-button-blue")));
		}
		else{
			tempCaptureImage.setDrawable(new TextureRegionDrawable(Assets.getTextureRegion("capture-button-red")));	
		}
		nameLabel.setText(unit.getName());
		apDisplay.update(unit);
		hpDisplay.update(unit);
		for(Image i : effectImage){
			i.setVisible(false);
		}
		for(Button b : abilityButtons){
			b.setVisible(false);
		}
		nrShownAbilities=0;
		
		//if(){
		for(Ability a : unit.getAbilities()){
			Actor actor = (abilityButtons[nrShownAbilities].getChildren().get(0));	//getactors
			if(actor instanceof Image){  // to be removed
				((com.badlogic.gdx.scenes.scene2d.ui.Image) actor).setDrawable(new TextureRegionDrawable(Assets.getTextureRegion(a.getIconName(unit))));
			}
			if(actor instanceof AbilityButton){
				((AbilityButton) actor).setAbility(unit, a);
				((AbilityButton) actor).updateIsAllowed();
			}
			
			//abilityButtons[nrShownAbilities].clear();
			//abilityButtons[nrShownAbilities].add(new AbilityButton(unit, a));
			
			abilityButtons[nrShownAbilities].setVisible(true);
			nrShownAbilities++;
		}
		
		// if a on a building
		Building building = State.activeState.map.getBuildingByTile(unit.tileCoordinate);
		if(building != null && new CaptureCommand(building, unit).isAllowed()){
			captureButton.setVisible(true);
		}else{
			captureButton.setVisible(false);
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
		GameInstance game = GameInstance.activeGame;
		
		boolean userCurrentPlayer = GameInstance.activeGame.isUserCurrentPlayer();
		Assets.SKIN.setEnabled(endTurnButton, userCurrentPlayer);
		//Assets.SKIN.setEnabled(buyButton, userCurrentPlayer);

		boolean playerControlsAllPlayers = game.getGameType() == GameInstanceType.HOTSEAT;//!(game instanceof NetworkGameInstance) && !game.isAiGame();
		String text = (!playerControlsAllPlayers && userCurrentPlayer) ? "Your turn" : player.getDisplayName();
		
		if(openBuyUnitPopup != null) openBuyUnitPopup.update();
		if(openAbilityPopup != null) openAbilityPopup.update();
		
		turnDisplay.setText(text);
		turntable.setColor(player.getColor());
	}
	
	public void showBuyUnitPopup(){
		int team = GameInstance.activeGame.getUserPlayer().team;
		
		if(buyUnitPopups[team] == null){
			buyUnitPopups[team] = new BuyUnitPopup(UnitType.TEAMS[team]);
		}
		
		(openBuyUnitPopup = buyUnitPopups[team]).show();
	}

	public void showAbilityPopup(){
		int god = GameInstance.activeGame.getUserPlayer().god;
		
		if(abilityPopups[god] == null){
			abilityPopups[god] = new AbilityPopup(PlayerAbilityType.GODS[god]);
		}
		
		(openAbilityPopup = abilityPopups[god]).show();
	}
	
	/**
	 * TODO solve in another way.
	 * @param x the x window coordinate
	 * @param y the y window coordinate
	 * @param count
	 * @return If it hit any interface element
	 */
	public boolean tap(float x, float y, int count, int button){
		//y reversed compared to ui
		if(unitTable.isVisible()){
			return (y <= topPanel.getHeight() || y>= Gdx.graphics.getHeight()-unitTable.getHeight());
		}
		else
			return (y <= topPanel.getHeight() || y>= Gdx.graphics.getHeight()-permanentPanel.getHeight());
	}
}
