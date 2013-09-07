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
import com.anstrat.popup.HelpPopup;
import com.anstrat.popup.Popup;
import com.anstrat.popup.UnitInfoPopup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
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
	private ValueDisplay goldDisplay;
	private Label turnDisplay;
	
	private Table goldDisplayTable;
	
	//Top magic bar
	private MagicBart3 mbar;
	
	//Bottom panel
	public Table bottomPanel;
	private Button deselectButton;
	private Label nameLabel;
	private Image portraitFrame;
	private Image unitTypeImage;
	private APPieIcon apWheel;
	private NumberIcon hpValue;
	private Image unitNameLabel;
	private Image tempCaptureImage;
	private TextureRegion health_tens, health_ones;
	
	//Permanent panel
	public Table permanentPanel;
	private Button buyButton, helpButton, spellButton;
	
	//Unit version
	private Table unitTable;
	//private ValueDisplay hpDisplay;
	//private ValueDisplay apDisplay;
	private Button[] abilityButtons = new Button[MAX_ABILITIES];
	private Image[] effectImage = new Image[MAX_EFFECTS];
	//private Button captureButton;
	
	private BuyUnitPopup[] buyUnitPopups = new BuyUnitPopup[UnitType.TEAMS.length];
	private BuyUnitPopup openBuyUnitPopup;
	
	//private AbilityPopup[] abilityPopups = new AbilityPopup[PlayerAbilityType.GODS.length];
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
		//manaDisplay = new ValueDisplay(ValueDisplay.VALUE_MANA);
		turnDisplay = new Label("",Assets.SKIN);
		turntable = new ColorTable(Color.BLACK);
		turntable.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		turntable.add(turnDisplay).center();
        
        addActor(topPanel);
        
        /**
         * MAGICKA
         */
        mbar = new MagicBart3();
        
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
            	GEngine.getInstance().actionHandler.deselectPress();
            	showBuyUnitPopup();
            }
        } );
        helpButton = new Button(new Image(Assets.getTextureRegion("help-button")), Assets.SKIN.get("image", ButtonStyle.class));
        helpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	GEngine.getInstance().actionHandler.deselectPress();
            	new HelpPopup().show();
            }
        } );
        
        spellButton = new Button(new Image(Assets.getTextureRegion("magic-button")), Assets.SKIN.get("image", ButtonStyle.class));
        spellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	GEngine.getInstance().actionHandler.deselectPress();
            	showAbilityPopup();
            }
        } );
      
        /**
        * UNIT PANEL
        */
        //hpValue = ;
        //hpDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_HP);
		//apDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_AP);
        /*
        tempCaptureImage = new Image(Assets.getTextureRegion("capture-button-blue"));
        captureButton = new Button(tempCaptureImage, Assets.SKIN.get("image", ButtonStyle.class));
        captureButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	GEngine.getInstance().actionHandler.capturePress();
            }
        } );
        */
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
        unitTypeImage = new Image(Assets.getTextureRegion("grid"));
        unitTypeImage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                	if(GEngine.getInstance().selectionHandler.selectedUnit!=null)
                		unitInfoPopup.show(GEngine.getInstance().selectionHandler.selectedUnit);
                }
            } );
        unitNameLabel = new Image(Assets.getTextureRegion("grid"));
        portraitFrame = new Image(Assets.getTextureRegion("grid"));
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
		goldDisplayTable = new ColorTable(new Color(0.559f,0.385f,0.055f,1.0f));

		goldDisplayTable.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		Image image = new Image(Assets.getTextureRegion("gold"));
		goldDisplayTable.add(image);
		goldDisplayTable.add(goldDisplay).align(Align.right);

		topPanel.add(goldDisplayTable);
		
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
        bottomPanel.add(unitTable).width(width).height(bph).expand().fill();
        bottomPanel.add(permanentPanel).ignore().bottom().right().height(bph/2f);
        
        updateCurrentPlayer();
	}
	
	private void layoutUnitTable(){
		float bph = Main.percentWidth * 30f;
        
        unitTable.clear();
        unitTable.row().expand().top();
        
        // Left table, portrait, name, ap, health
        
        Table table1 = new Table();
        table1.defaults().top().left();
        Table table11 = new Table();
        
		//float pad = -Assets.NinePatchUtils.getTopLeft(Assets.getTextureRegion("portraitFrame"), 15, 15, 15, 15).getRegionHeight()/2f;
        table11.add(unitTypeImage).align(Align.left).size(bph/1.4f);
        table11.columnDefaults(1);
        table11.add(apWheel).top().size(bph/3f);
        table11.add(hpValue).bottom().size(bph/6f).left();
		//table1.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("portraitFrame"))));

        //Cell portraitCell = table1.add(portraitFrame);
        
        
		//portraitCell.setWidget(table11);

		table1.row();
		
		//Unit name should have been added here if properly done, now printed later on
		//table1.add(unitNameLabel).align(Align.left);
        
        
        // Right table, effects + abilities
        Table table2 = new Table();
        
		// Abilities
		for(Button b : abilityButtons){
			if(b.isVisible()) table2.add(b).size(bph/2f);
		}
		
		// Capture button *Legacy from 2013-08-17
		//if(captureButton.isVisible()) table2.add(captureButton).size(bph/2f);
		
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
		unitTypeImage.setDrawable(new TextureRegionDrawable( GUnit.getUnitPortrait(unit)));
		unitNameLabel.setDrawable(new TextureRegionDrawable(GUnit.getUnitName(unit)));
		portraitFrame.setDrawable(new TextureRegionDrawable(Assets.getTextureRegion("portraitFrame")));
		nameLabel.setText(unit.name);
		
		for(Image i : effectImage){
			i.setVisible(false);
		}
		for(Button b : abilityButtons){
			b.setVisible(false);
		}
		nrShownAbilities=0;
		//captureButton.setVisible(false);
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
		unitTypeImage.setDrawable(new TextureRegionDrawable( GUnit.getUnitPortrait(unit.getUnitType())));
		/*
		if(State.activeState.currentPlayerId%2==0){
			tempCaptureImage.setDrawable(new TextureRegionDrawable(Assets.getTextureRegion("capture-button-blue")));
		}
		else{
			tempCaptureImage.setDrawable(new TextureRegionDrawable(Assets.getTextureRegion("capture-button-red")));	
		}
		*/
		unitNameLabel.setDrawable(new TextureRegionDrawable(GUnit.getUnitName(unit.getUnitType())));
		portraitFrame.setDrawable(new TextureRegionDrawable(Assets.getTextureRegion("portraitFrame")));
		apWheel = new APPieIcon(unit.getMaxAP(),unit.currentAP);
		hpValue = new NumberIcon(unit.currentHP, 30f, Color.GREEN);
		nameLabel.setText(unit.getName());
		/*
		apDisplay.update(unit);
		hpDisplay.update(unit);
		*/
		for(Image i : effectImage){
			i.setVisible(false);
		}
		for(Button b : abilityButtons){
			b.setVisible(false);
		}
		nrShownAbilities=0;
		
		if(lastShownUnit.ownerId == State.activeState.currentPlayerId){
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
		}
		
		// if a on a building
		//Building building = State.activeState.map.getBuildingByTile(unit.tileCoordinate);
		/*
		 * Capture by spending ap is deprecated
		if(building != null && new CaptureCommand(building, unit).isAllowed()){
			captureButton.setVisible(true);
		}else{
			captureButton.setVisible(false);
		}
		*/
		resize(); // update positions of buttons etc.
	}
	
	/**
	 * Updates UI. (gold- & manadisplay + unit info)
	 */
	public void update(){
		goldDisplay.update(null);
		//manaDisplay.update(null);
		showUnit(lastShownUnit);
		
		int ma0n = State.activeState.players[State.activeState.currentPlayerId].mana;
		if(ma0n >= 6){
			spellButton = new Button(new Image(Assets.getTextureRegion("magic-button-three")), Assets.SKIN.get("image", ButtonStyle.class));
	        spellButton.addListener(new ClickListener() {
	            @Override
	            public void clicked(InputEvent event, float x, float y) {
	            	showAbilityPopup();
	            }
	        } );
		}
		else if(ma0n >= 4){
			spellButton = new Button(new Image(Assets.getTextureRegion("magic-button-two")), Assets.SKIN.get("image", ButtonStyle.class));
	        spellButton.addListener(new ClickListener() {
	            @Override
	            public void clicked(InputEvent event, float x, float y) {
	            	showAbilityPopup();
	            }
	        } );
		}
		else if(ma0n >= 2){
			spellButton = new Button(new Image(Assets.getTextureRegion("magic-button-one")), Assets.SKIN.get("image", ButtonStyle.class));
	        spellButton.addListener(new ClickListener() {
	            @Override
	            public void clicked(InputEvent event, float x, float y) {
	            	showAbilityPopup();
	            }
	        } );
		}
		else{
			spellButton = new Button(new Image(Assets.getTextureRegion("magic-button")), Assets.SKIN.get("image", ButtonStyle.class));
	        spellButton.addListener(new ClickListener() {
	            @Override
	            public void clicked(InputEvent event, float x, float y) {
	            	showAbilityPopup();
	            }
	        } );
		}
	}
	
	/**
	 * Updates label of current player, also enables/disables the End Turn button.
	 */
	public void updateCurrentPlayer(){
		if (State.activeState == null) {
			return;
		}
		
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
		
		goldDisplayTable.setVisible(userCurrentPlayer);
		mbar.setVisible(userCurrentPlayer);
	}
	
	public void showBuyUnitPopup(){
		int team = GameInstance.activeGame.getUserPlayer().team;
		
		if(buyUnitPopups[team] == null){
			buyUnitPopups[team] = new BuyUnitPopup(UnitType.TEAMS[team]);
		}
		
		(openBuyUnitPopup = buyUnitPopups[team]).show();
	}

	public void showAbilityPopup(){		
		//(new MagicPopup(UnitType.TEAMS[GameInstance.activeGame.getUserPlayer().team])).show();
		new AbilityPopup(PlayerAbilityType.ODINS_BLESSING,
				PlayerAbilityType.SWAP,
				PlayerAbilityType.COMETSTRIKE).show();
	}
	
	@Override
	public void draw(){
		if(Popup.getCurrentPopup() instanceof BuyUnitPopup /* u has problem? */){
			SpriteBatch batch = Main.getInstance().batch;
			batch.begin();
			batch.draw(Assets.getTextureRegion(State.activeState.currentPlayerId==0?"buy-blue":"buy-red"),
					0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			batch.end();
		}
		else if(Popup.getCurrentPopup() instanceof AbilityPopup){
			SpriteBatch batch = Main.getInstance().batch;
			batch.begin();
			batch.draw(Assets.getTextureRegion("magic-purple"),
					0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			batch.end();
		}
		super.draw();
		if(visible) mbar.draw();
		if(Popup.getCurrentPopup() instanceof BuyUnitPopup /* u has problem? */){
			if(!visible) mbar.draw();
			Main.getInstance().batch.begin();
			topPanel.draw(Main.getInstance().batch, 1.0f);
			Main.getInstance().batch.end();
		}
		/* Bugfix xD lol */
		if(visible && unitTable.isVisible() && lastShownUnit != null) {
			SpriteBatch batch = Main.getInstance().batch;
			batch.begin();
			float pw = Main.percentWidth;
			unitTypeImage.getDrawable().draw(batch, pw*6.11f, pw*8.7f, pw*18.2f, pw*18.2f);
			
			Color temp = batch.getColor();
			batch.setColor(Player.primaryColor[lastShownUnit.ownerId]);
			batch.disableBlending();
			TextureRegion tintThis = Assets.getTextureRegion("white-line-hard");
			batch.draw(tintThis, pw*25f, pw*8.7f, pw*9.4f, pw*18.2f);
			batch.enableBlending();
			batch.setColor(temp);
			
			Drawable lol = portraitFrame.getDrawable();
			lol.draw(batch, pw*3f, pw*6.2f, pw*33.83f, pw*23f);
			Drawable lol2 = unitNameLabel.getDrawable();
			float lolRatio = lol2.getMinWidth()/lol2.getMinHeight();
			float textHeight = Gdx.graphics.getHeight()*0.035f;
			lol2.draw(batch, 0, 0, textHeight*lolRatio, textHeight);
			
			APPieDisplay.draw(pw*26f, pw*18.23f, pw*7f, lastShownUnit.currentAP, lastShownUnit.getMaxAP(), 
					lastShownUnit.getAPReg(), 0, batch, true, 1f);
			
			Color oldColor = batch.getColor();
			batch.setColor(Color.GREEN);
			
			int healthTens = lastShownUnit.currentHP/10;
			int healthOnes = lastShownUnit.currentHP - healthTens*10;
			
			float TEXT_SCALE = 0.74326f;
			float TEXT_X_BUGFIX = 6f;
			float TEXT_X_OFFSET = pw*29.19f;
			float TEXT_Y_OFFSET = pw*11f;
			
			if(healthTens > 0)
			{
				health_tens = Assets.getTextureRegion("ap-"+healthTens);
				health_ones = Assets.getTextureRegion("ap-"+healthOnes);
				
				batch.draw(health_tens, TEXT_X_OFFSET-health_tens.getRegionWidth()*TEXT_SCALE+TEXT_X_BUGFIX*TEXT_SCALE, 
						TEXT_Y_OFFSET, 0, 0, health_tens.getRegionWidth(), health_tens.getRegionHeight(), TEXT_SCALE, TEXT_SCALE, 0f);
				batch.draw(health_ones, TEXT_X_OFFSET-TEXT_X_BUGFIX*TEXT_SCALE, TEXT_Y_OFFSET, 0, 0, 
						health_ones.getRegionWidth(), health_ones.getRegionHeight(), TEXT_SCALE, TEXT_SCALE, 0f);
			}
			else if(healthOnes > 0)
			{
				health_ones = Assets.getTextureRegion("ap-"+healthOnes);

				batch.draw(health_ones, TEXT_X_OFFSET-health_ones.getRegionWidth()*TEXT_SCALE/2f, TEXT_Y_OFFSET, 0, 0, 
						health_ones.getRegionWidth(), health_ones.getRegionHeight(), TEXT_SCALE, TEXT_SCALE, 0f);
			}
			batch.setColor(oldColor);
			
			if(lastShownUnit.ownerId != GameInstance.activeGame.getUserPlayer().playerId){
				TextureRegion enemyUnit = Assets.getTextureRegion("enemyunit");
				batch.draw(enemyUnit, pw*41.5f, pw*17f, pw*40f, pw*9f);
			}
			batch.end();
		}
	}
	

	public void setEndTurnButtonVisible(boolean visible) {
		Assets.SKIN.setEnabled(endTurnButton, visible);
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
