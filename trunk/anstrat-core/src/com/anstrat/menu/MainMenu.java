package com.anstrat.menu;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.core.NetworkGameInstance;
import com.anstrat.core.Options;
import com.anstrat.core.User;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.mapEditor.MapEditor;
import com.anstrat.network.GameRequest;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

/**
 * The class handling the first menu that shows up when the user starts the application.
 * @author Ekis
 *
 */
public class MainMenu extends MenuScreen {
	
	private static MainMenu me;
	
	public static TextField usernameInput = ComponentFactory.createTextField("Login", false);
	public static TextField passwordInput = ComponentFactory.createTextField("Password", true);
	
	public static MapsPopup mapsPopup;
	public static String HOTSEAT = "Hotseat", INTERNET = "Internet";
	
	public static boolean versusAI = false;
	
	private Table scrollTable, gamesList;
	private FlickScrollPane scroll;
	
	private MainMenu() {
		super();
		
		//Change to classic sound on/off icon later.
		CheckBoxStyle cbst = new CheckBoxStyle(Assets.getTextureRegion("sound-off"),Assets.getTextureRegion("sound-on"),Assets.MENU_FONT,Color.WHITE);
		CheckBox muteButton = new CheckBox("", cbst);
		muteButton.setChecked(Options.soundOn);
		muteButton.setClickListener(new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				if(Options.soundOn = !Options.soundOn)
					Main.getInstance().menuMusic.play();
				else
					Main.getInstance().menuMusic.pause();
			}
			
		});

		Button newGameButton = ComponentFactory.createMenuButton("New Game",new ClickListener() {
        	@Override
        	public void click(Actor actor, float x, float y) {
        		Main.getInstance().setScreen(NewGameMenu.getInstance());
        	}
        });

        Button mapEditorButton = ComponentFactory.createMenuButton("Map Editor",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(MapEditor.getInstance());
            }
        });
        
        NinePatch emp = Assets.SKIN.getPatch("empty");
        TextButton ver = new TextButton(" "+Main.version,
        		new TextButtonStyle(emp,emp,emp,0f,0f,0f,0f,Assets.UI_FONT,Color.WHITE,Color.LIGHT_GRAY,Color.WHITE));
        ver.setClickListener(new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(DebugMenu.getInstance());
            }
        });

        gamesList = new Table(Assets.SKIN);
        updateGamesList();

		scroll = new FlickScrollPane(gamesList);
		scroll.setScrollingDisabled(true, false);
		scrollTable = new Table();
		scrollTable.top();
		scrollTable.add(scroll).fill().expand();
		
		Image empty = new Image(Assets.SKIN.getPatch("empty"));
		
		int logoWidth = (int)(Main.percentWidth*55);
		
        int buttonWidth  = BUTTON_WIDTH;
        int buttonHeight = BUTTON_HEIGHT;
        contents.addActor(scrollTable);

        // Background with hole
        Image transBack = new Image(Assets.getTextureRegion("MenuBackground-transparent"));
        contents.addActor(transBack);
        transBack.x = transBack.y = 0;
        transBack.height = Gdx.graphics.getHeight();
        transBack.width = Gdx.graphics.getWidth();
        
        contents.defaults().space((int)Main.percentHeight).top().center();
        
        Table inner1 = new Table();
        inner1.add(ver).uniform().top().left();
        inner1.add(new Image(Assets.getTextureRegion("logo"))).height((int)(logoWidth/2.8f)).width(logoWidth).center().padTop((int)(Main.percentHeight*7));
        inner1.add(muteButton).top().right().uniform().width((int)((Main.percentWidth*100-logoWidth)/2)).padTop((int)(Main.percentHeight*3));
        
        contents.add(inner1);
        contents.row();
        contents.add(newGameButton).height(buttonHeight).width(buttonWidth);
        
        contents.row();
        contents.add(mapEditorButton).height(buttonHeight).width(buttonWidth);
        contents.row();
        contents.add(empty).fill().expandY().padBottom((int)(Main.percentHeight*10)).padTop((int)(Main.percentHeight*5));
        
        Table inner2 = new Table();
        inner2.add(ComponentFactory.createLoginLabel()).center();
        contents.row();
        contents.add(inner2);
        
        contents.layout();
        Vector2 gameListPos = new Vector2();
        Widget.toScreenCoordinates(empty, gameListPos);
        scrollTable.x = gameListPos.x;
        scrollTable.y = gameListPos.y;
        scrollTable.width = empty.getImageWidth();
        scrollTable.height = empty.getImageHeight();
        
        
	}
	
	public static synchronized MainMenu getInstance() {
		if(me == null){
			me = new MainMenu();
		}
		return me;
	}
	
	private float timeSinceLastUpdate = 0f;
	//private float flagAnimationStateTime = 0f;
	
	@Override
	public void render(float delta){
		super.render(delta);
		
		if(timeSinceLastUpdate > 0.5f){
			updateTimeleftLabels();
			
			timeSinceLastUpdate = 0f;
		}
	}
	
	private void updateTimeleftLabels(){
		// Update time labels
		long now = new Date().getTime();
		
		for(java.util.Map.Entry<NetworkGameInstance, Label> e : timeleftLabels.entrySet()){
			long diff = e.getKey().getTurnEndTime().getTime() - now; 
			
			if(diff < 0){
				e.getValue().setText("TURN ENDED");
			}
			else{
				e.getValue().setText(formatTime(diff));
			}
		}
	}
	
	@Override
	public void hide() {
		super.hide();
	}
	
	@Override
	public void show() {
		super.show();
		updateGamesList();
	}

	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
	
	private java.util.Map<NetworkGameInstance, Label> timeleftLabels = new HashMap<NetworkGameInstance, Label>();
	
	public void updateGamesList(){
		// Only update if MainMenu is the active screen
		if(Main.getInstance().getScreen() != this) return;
		
		timeleftLabels.clear();
		gamesList.clear();
		gamesList.top();
		gamesList.setFillParent(true);
		
		int height = (int)(Main.percentHeight*4);
		int paddingTop = (int)(Main.percentHeight*4);
		
		Table current = new Table(Assets.SKIN);
		current.add("Your turn:").height(height).padTop(paddingTop);
		current.row();
		
		Table waiting = new Table(Assets.SKIN);
		waiting.add("Waiting for other players:").height(height).padTop(paddingTop);
		waiting.row();
		
		Table requests = new Table(Assets.SKIN);
		requests.add("Game requests:").height(height).padTop(paddingTop);
		requests.row();
		
		NinePatch gameTableBackground = Assets.SKIN.getPatch("line-border-thin");
		
    	//Add GameInstances
        for(final GameInstance gi : GameInstance.getActiveGames()){
        	if(!(gi instanceof NetworkGameInstance) || gi.isInGame(User.globalUserID))
        	{
            	boolean isPlayerTurn = gi.getCurrentPlayer().userID == User.globalUserID;
            	Table t = new Table(Assets.SKIN);
            	Label timeleftLabel = null;
            	Label turn = new Label("Turn "+gi.getTurnNumber(),Assets.SKIN);
            	Label type = new Label("("+(gi.isAiGame() ? "AI" : 
            		(gi instanceof NetworkGameInstance ? "Network" : "Hotseat"))+")", Assets.SKIN);
            	Label map = new Label("'"+gi.state.map.name+"'",Assets.SKIN);
            	Label mapSize = new Label(gi.state.map.getXSize()+"x"+gi.state.map.getYSize(),Assets.SKIN);
            	
            	t.setBackground(gameTableBackground);
            	
            	if(gi instanceof NetworkGameInstance){
            		timeleftLabels.put((NetworkGameInstance) gi, timeleftLabel);
            	}
            	
            	Button cancel = new Button(new Image(Assets.getTextureRegion("cancel")), Assets.SKIN.getStyle("image-toggle", ButtonStyle.class));
            	
            	cancel.setClickListener(new ClickListener() {
        	        @Override
        	        public void click(Actor actor,float x,float y ){
        	        	gi.resign();
        	        	updateGamesList();
        	        }
        		});
            	
            	String opponent = "vs. ";
            	if(gi.isAiGame() || gi instanceof NetworkGameInstance)
    	        	for(Player p : gi.state.players){
    	        		if(p.userID != User.globalUserID){
    	        			opponent += p.displayedName;
    	        			break;
    	        		}
    	        	}
            	else{
            		opponent = gi.state.players[0].displayedName + " vs. " +gi.state.players[1].displayedName;
            	}
            	Label players = new Label(opponent, Assets.SKIN);
            	
            	t.left().pad((int)(2*Main.percentWidth));
            	t.defaults().left().fillX().expandX();
            	
            	Table outer = new Table();
            	outer.defaults().left().height((int)(4*Main.percentHeight));
            	outer.add(players);
            	outer.row();
            	
            	Table inner1 = new Table();
            	inner1.add(map).fillX().expandX();
            	inner1.add(mapSize);
            	
            	Table inner2 = new Table();
            	inner2.add(type);
            	inner2.add(turn).expandX();
            	inner2.add(timeleftLabel);
            	
            	outer.add(inner1).expandX().fillX();
            	outer.row();
            	outer.add(inner2).expandX().fillX();
            	
            	t.add(outer).expandX().fillX().padLeft((int)(Main.percentHeight));
            	t.add(cancel).pad((int)(3+Main.percentWidth)).height((int)(7*Main.percentHeight)).width((int)(7*Main.percentHeight)).bottom().right();
            	
            	Table tab = (isPlayerTurn || !(gi instanceof NetworkGameInstance))?current:waiting;
            	tab.add(t).fillX().expandX().height((int)(17*Main.percentHeight));
            	tab.row();
            	t.setClickListener(new ClickListener() {
        	        @Override
        	        public void click(Actor actor,float x,float y ){
        	        	gi.showGame(false);
        	        }
        		});
            }
        }
       
        //Add game requests
        for(GameRequest gr : Main.getInstance().gameRequests.values()){
        	
        	Table t = new Table(Assets.SKIN);
        	t.setBackground(gameTableBackground);
        	
        	Map map = gr.map;
        	Label gameName = new Label(gr.gameName, Assets.SKIN);
        	Label limit = new Label("Limit: "+gr.timeLimit, Assets.SKIN);
        	Label mapSize = new Label(map==null?"null":(map.getXSize()+"x"+map.getYSize()), Assets.SKIN);
        	Label mapName = new Label(map==null?"null":map.name, Assets.SKIN);
        	
        	String status = "";
        	switch(gr.status){
        	case GameRequest.STATUS_SEARCH_GAME:
        		status="Searching for game";
        		break;
        	case GameRequest.STATUS_UNKNOWN:
        		status="Status unknown";
        		break;
        	case GameRequest.STATUS_WAIT_OPPONENT:
        		status="Waiting for opponent";
        		break;
        	}
        	
        	Button cancel = new Button(new Image(Assets.getTextureRegion("cancel")), Assets.SKIN.getStyle("image-toggle", ButtonStyle.class));
        	final long nonce = gr.nonce;
        	cancel.setClickListener(new ClickListener() {
    	        @Override
    	        public void click(Actor actor,float x,float y ){
    	        	Main.getInstance().cancelRequest(nonce);
    	        }
    		});
        	
        	t.left().pad((int)(2*Main.percentWidth));
        	t.defaults().left().fillX().expandX();
        	
        	Table outer = new Table(Assets.SKIN);
        	outer.defaults().left().height((int)(4*Main.percentHeight));
        	
        	Table inner1 = new Table();
        	inner1.add(gameName).fillX().expandX();
        	inner1.add(limit);
        	
        	Table inner2 = new Table();
        	inner2.add(mapName).fillX().expandX();
        	inner2.add(mapSize);
        	
        	outer.add(inner1).expandX().fillX();
        	outer.row();
        	outer.add(inner2).expandX().fillX();
        	outer.row();
        	outer.add(status);
        	
        	t.add(outer).expandX().fillX().padLeft((int)(Main.percentHeight));
        	t.add(cancel).pad((int)(3+Main.percentWidth)).height((int)(7*Main.percentHeight)).width((int)(7*Main.percentHeight)).bottom().right();
        	
        	requests.add(t).fillX().expandX().height((int)(17*Main.percentHeight));
        	requests.row();
        }
        
        if(current.getActors().size() > 1){
     		gamesList.add(current).fillX().expandX();
         	gamesList.row();
        }
        if(waiting.getActors().size() > 1){
         	gamesList.add(waiting).fillX().expandX();
         	gamesList.row();
        }
        if(requests.getActors().size() > 1){
        	gamesList.add(requests).fillX().expandX();
        }
        gamesList.padBottom((int)(Main.percentHeight*5));
        
        updateTimeleftLabels();
	}
	
	public Popup getMapsPopup() {
		String[] mapNames = Assets.getMapList(true, true);
		
		if (mapNames.length > 0) {
			mapsPopup = new MapsPopup(new MapsPopupHandler() {
				@Override
				public void mapSelected(String mapName) {
					Map map = null;
					
					if(mapName.equalsIgnoreCase("RANDOM")){
						MapsPopup popup = (MapsPopup)Popup.currentPopup;
						map = new Map(popup.randWidth,popup.randHeight,new Random());
					}
					else{
						map = Assets.loadMap(mapName);
					}
					
			        if(versusAI == true){
			        	GameInstance.createAIGame(map, 1).showGame(true);
			        }
			        else{
			        	GameInstance.createHotseatGame(map).showGame(true);
			        }
				}
			}, true, "Choose map", mapNames);
			return mapsPopup;
		}
		else 
			return null;
	}
	
	/**
	 * Sets logInButton text and also updates games list.
	 * @param displayName
	 */
	public void setDisplayName(String displayName){	//TODO: Move update elsewhere, remove method.
		updateGamesList();
	}
	
	// Formats the given time to a HH:MM:SS format, timeSpan is given in milliseconds
	private static String formatTime(long milliseconds){
		long hours = milliseconds / 3600000l;
		milliseconds %= 3600000l;
		long minutes = milliseconds / 60000l;
		milliseconds %= 60000l;
		long seconds = milliseconds / 1000;
		
		StringBuilder builder = new StringBuilder(10);
		
		if(hours > 0){
			if(hours < 10) builder.append('0');
			builder.append(hours);
			builder.append(':');
		}

		if(minutes < 10) builder.append('0');
		builder.append(minutes);
		builder.append(':');
		
		if(seconds < 10) builder.append('0');
		builder.append(seconds);
		
		return builder.toString();
	}
}


