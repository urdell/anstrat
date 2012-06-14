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
import com.anstrat.popup.Popup;
import com.anstrat.popup.PopupListener;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
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
	
	public static TextField usernameInput = ComponentFactory.createTextField("Login",null,false);
	public static TextField passwordInput = ComponentFactory.createTextField("Password", null, true);
	
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

        contents.register("newGameButton", ComponentFactory.createMenuButton("New Game",new ClickListener() {
        	@Override
        	public void click(Actor actor, float x, float y) {
        		Main.getInstance().setScreen(NewGameMenu.getInstance());
        	}
        }));

        contents.register("mapEditorButton", ComponentFactory.createMenuButton("Map Editor",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(MapEditor.getInstance());
            }
        }));
        
        contents.register("login", ComponentFactory.createLoginLabel());
        contents.register("version", new Label(" "+Main.version, new LabelStyle(Assets.UI_FONT,Color.WHITE)));

        gamesList = new Table(Assets.SKIN);
        updateGamesList();

		scroll = new FlickScrollPane(gamesList);
		scroll.setScrollingDisabled(true, false);
		scrollTable = new Table();
		scrollTable.align("top");
		scrollTable.add(scroll).fill().expand();
		contents.register( "games", scrollTable );
		
		contents.register("mute", muteButton );
		contents.register("logo", new Image(Assets.getTextureRegion("logo")));
		Image empty = new Image(Assets.SKIN.getPatch("empty"));
		contents.register("empty", empty);
		
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
        
        contents.parse(
        		"debug * spacing:"+(int) Main.percentHeight+" align:top,center" +
        		"{" +
        			"[version] uniform align:top,left" +
        			"[logo] height:"+(int)(logoWidth/2.8f)+" width:"+logoWidth+" align:center paddingTop:"+(int)(Main.percentHeight*7) +
        			"[mute] align:top,right uniform width:"+(int)((Main.percentWidth*100-logoWidth)/2)+" paddingTop:"+(int)(Main.percentHeight*3) +
        		"} align:top,left fill:x " +
        		"---" +
    			"[newGameButton] height:"+buttonHeight+" width:"+buttonWidth +
    			"---" +
    			"[mapEditorButton] height:"+buttonHeight+" width:"+buttonWidth +
    			"---" +
    			"[empty] fill:90,100 expand:y paddingBottom:"+(int)(Main.percentHeight*10)+" paddingTop:"+(int)(Main.percentHeight*5) +
    			"---" +
    			"{[login] align:center}");
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
		gamesList.align("top");
		gamesList.setFillParent(true);
		
		String topLayout = "height:"+(int)(Main.percentHeight*4)+" paddingTop:"+(int)(Main.percentHeight*4) +
				"---";
		
		Table current = new Table(Assets.SKIN);
		current.parse("'Your turn:'" + topLayout);
		Table waiting = new Table(Assets.SKIN);
		waiting.parse("'Waiting for other players:'" + topLayout);
		Table requests = new Table(Assets.SKIN);
		requests.parse("'Game requests:'" + topLayout);
		
		NinePatch gameTableBackground = Assets.SKIN.getPatch("line-border-thin");
		
    	//Add GameInstances
        for(final GameInstance gi : GameInstance.getActiveGames()){
        	if(!(gi instanceof NetworkGameInstance) || gi.isInGame(User.globalUserID))
        	{
            	boolean isPlayerTurn = gi.getCurrentPlayer().userID == User.globalUserID;
            	Table t = new Table(Assets.SKIN);
            	Label timeleftLabel = null;
            	
            	t.setBackground(gameTableBackground);
            	t.register("turn", new Label("Turn "+gi.getTurnNumber(),Assets.SKIN));
            	t.register("type", new Label("("+(gi.isAiGame() ? "AI" : 
            		(gi instanceof NetworkGameInstance ? "Network" : "Hotseat"))+")", Assets.SKIN));
            	t.register("map", new Label("'"+gi.state.map.name+"'",Assets.SKIN));
            	t.register("mapSize", new Label(gi.state.map.getXSize()+"x"+gi.state.map.getYSize(),Assets.SKIN));
            	t.register("numPlayers", new Label(" "+gi.state.players.length, Assets.SKIN));
            	t.register("timeleft", timeleftLabel = new Label("", Assets.SKIN));
            	
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
            	t.register("cancel",cancel);
            	
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
            	t.register("players", new Label(opponent, Assets.SKIN));
            	
            	t.parse("align:left padding:"+(int)(2*Main.percentWidth) +
            			"* align:left fill:x expand:x "+
            			"{ * align:left height:"+(int)(4*Main.percentHeight) +
            				"[players]" +
            				"---" +
            				"{[map] fill:x expand:x [mapSize]} expand:x fill:x" +
            				"---" +
            				"{[type] [turn] expand:x [timeleft]} expand:x fill:x" +
            			"} expand:x fill:x paddingleft:"+(int)(Main.percentHeight) +
            			"[cancel] padding:"+(int)(3+Main.percentWidth)+" height:"+(int)(7*Main.percentHeight)+" width:"+(int)(7*Main.percentHeight)+" align:bottom,right");
            	
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
        	
        	t.register("gameName", new Label(gr.gameName, Assets.SKIN));
        	t.register("limit", new Label("Limit: "+gr.timeLimit, Assets.SKIN));
        	
        	Map map = gr.map;
        	t.register("mapName", new Label(map==null?"null":map.name, Assets.SKIN));
        	t.register("mapSize", new Label(map==null?"null":(map.getXSize()+"x"+map.getYSize()), Assets.SKIN));
        	
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
        	t.register("status", new Label(status, Assets.SKIN));
        	
        	Button cancel = new Button(new Image(Assets.getTextureRegion("cancel")), Assets.SKIN.getStyle("image-toggle", ButtonStyle.class));
        	final long nonce = gr.nonce;
        	cancel.setClickListener(new ClickListener() {
    	        @Override
    	        public void click(Actor actor,float x,float y ){
    	        	Main.getInstance().cancelRequest(nonce);
    	        }
    		});
        	t.register("cancel",cancel);
        	
        	t.parse("align:left padding:"+(int)(2*Main.percentWidth) +
        			"* align:left fill:x expand:x "+
        			"{ * align:left height:"+(int)(4*Main.percentHeight) +
        				"{[gameName] fill:x expand:x [limit]} expand:x fill:x" +
        				"---" +
        				"{[mapName] fill:x expand:x [mapSize]} expand:x fill:x" +
        				"---" +
        				"[status]" +
        			"} expand:x fill:x paddingleft:"+(int)(Main.percentHeight) +
        			"[cancel] padding:"+(int)(3+Main.percentWidth)+" height:"+(int)(7*Main.percentHeight)+" width:"+(int)(7*Main.percentHeight)+" align:bottom,right");
        	
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
			mapsPopup = new MapsPopup(new PopupListener() {
	            @Override
	            public void handle(String text){
	            	Map map = null;
					
					if(text.equalsIgnoreCase("RANDOM")){
						MapsPopup popup = (MapsPopup)Popup.currentPopup;
						map = new Map(popup.randWidth,popup.randHeight,new Random());
					}
					else{
						map = Assets.loadMap(text);
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


