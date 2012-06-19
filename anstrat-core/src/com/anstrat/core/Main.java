package com.anstrat.core;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import com.anstrat.ai.AIRunner;
import com.anstrat.command.Command;
import com.anstrat.command.CommandHandler;
import com.anstrat.gameCore.GameType;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.geography.Map;
import com.anstrat.gui.GEngine;
import com.anstrat.guiComponent.TransitionEffect;
import com.anstrat.mapEditor.MapEditor;
import com.anstrat.menu.AccountMenu;
import com.anstrat.menu.MainMenu;
import com.anstrat.menu.NetworkDependentTracker;
import com.anstrat.menu.SplashScreen;
import com.anstrat.network.GameRequest;
import com.anstrat.network.INetworkListener;
import com.anstrat.network.Network;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Main extends Game implements ApplicationListener, INetworkListener {
	
	public static final String version = "Beta 1";
	
	public static float percentWidth;
	public static float percentHeight;
	
	// Network
	public Network network;
	
	public Music menuMusic;
	public User user;
	
	// Input handlers
	private final InputMultiplexer inputMultiplexer;
	private LinkedList<InputProcessor> inputProcessorsToBeRemoved;
	
	public final GestureMultiplexer gestureMultiplexer;
	private final CustomGestureDetector gestureDetector;
	
	public SpriteBatch batch;
	private Stage overlayStage;	//for drawing transition effects and popups.
	private static Main me;
	
	private Random random;
	
	public final AssetManager manager;
	
	public HashMap<Long, GameRequest> gameRequests = new HashMap<Long, GameRequest>();
	
	public static synchronized Main getInstance(){
		if(me == null){
			me = new Main();
		}
		return me;
	}

	private Main(){
		manager = new AssetManager();
		inputMultiplexer = new InputMultiplexer();
		inputProcessorsToBeRemoved = new LinkedList<InputProcessor>();
		
		gestureMultiplexer = new GestureMultiplexer();
		
		// Custom gesture detector (handles long press correctly)
		gestureDetector = new CustomGestureDetector(gestureMultiplexer);
		inputMultiplexer.addProcessor(gestureDetector);
		random = new Random();
	}
	
	@Override
	public void create() {
		
		// Print max texture size
		int[] maxTextureSize = new int[1];
		Gdx.gl10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
		Gdx.app.log("glinfo", "Max texture size = " + maxTextureSize[0]);
		
		Gdx.app.log("Game.create()", String.format("Display surface: %dx%d.", Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		System.out.println("PPIX = " + Gdx.graphics.getPpiX());
		System.out.println("PPIY = " + Gdx.graphics.getPpiY());
		
		percentWidth = ((float)Gdx.graphics.getWidth())/100f;
		percentHeight = ((float)Gdx.graphics.getHeight())/100f;
		
		// Network
		// TODO enable again when new server is obtained (preferably at Erik's)
		// network = new Network(this, "vengefulvikings.servegame.com"/*"129.16.21.61"*/, 25406);
		//network = new Network(this, "localhost", 25406);
		
		// Music
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/vikingstitle.mp3"));
		
		// Load from file
		Assets.load();													// Textures, fonts
		UnitType.loadAttributesFromFile(
				Gdx.files.internal("data/unitAttributes.xml"));			// Attributes (name, stats etc) for all units
		PlayerAbilityType.loadAttributesFromFile(
				Gdx.files.internal("data/playerAbilityAttributes.xml"));
		Options.loadPreferences();										// Settings, sound on/off etc
		GameInstance.loadGameInstances(Gdx.files.local("games.bin"));	// Loads all saved game instances
		
		// Create the single instance of sprite batch
		batch = new SpriteBatch();
		overlayStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, batch);
		
		Popup.initPopups(overlayStage);
		
		// Setup input and gesture processing
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputMultiplexer);
		inputMultiplexer.addProcessor(new CustomInputProcessor());
		
		// Init menu, show splash
		MainMenu.getInstance();
		Gdx.graphics.setTitle("Vengeful Vikings (Beta)");
		setScreen(SplashScreen.getInstance());
		
		// TODO enable again when new server is obtained (preferably at Erik's)
		/* network.start();
		
		// Try to load saved login info
		user = User.fromFile(Gdx.files.local("login.bin"));
		if(user != null){
			network.login(user.username, user.password);
		}
		else{
			user = new User();
			network.quickLogin();
		}
		*/
		
		// Set the desktop application icon
		FileHandle iconFile = Gdx.files.internal("icon.png");
		
		if(iconFile.exists()){
			Gdx.graphics.setIcon(new Pixmap[]{new Pixmap(iconFile)});
		}
		else{
			Gdx.app.log("Main", String.format("Warning: Could not find app icon '%s'.", iconFile));
		}
	}

	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		overlayStage.setViewport(width, height, false);
	}
	
	@Override
	public void render() {
		// For processor concurrency.
		while(!inputProcessorsToBeRemoved.isEmpty())
			inputMultiplexer.removeProcessor(inputProcessorsToBeRemoved.poll());

		gestureDetector.update(Gdx.graphics.getDeltaTime());
		
		//Don't run AI if using map editor
		if(!(super.getScreen() instanceof MapEditor))
			AIRunner.run(Gdx.graphics.getDeltaTime());
		
		GL10 gl = Gdx.graphics.getGL10();
		
		// Render
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glClearColor(0.25f, 0.25f, 0.25f, 1f);
		
		// Renders the current screen
		super.render();
		
		//Draw overlays and popups
		overlayStage.act(Gdx.graphics.getDeltaTime());
		overlayStage.draw();
	}
	
	@Override
	public void pause() {
		Gdx.app.log("Main", "Paused");
		if(network!=null)
			network.pause();
		Assets.onApplicationPause();
	}

	@Override
	public void resume() {
		Gdx.app.log("Main", "Resumed");
		if(network!=null)
			network.resume();
		Assets.onApplicationResume();
	}

	@Override
	public void dispose() {
		Gdx.app.log("", "Main.dispose()");
		
		if(network!=null)
			network.stop();
		GameInstance.saveGameInstances(Gdx.files.local("games.bin"));
		Options.savePreferences();
		
		batch.dispose();
		
		// Dispose all screens that have been initialized
		for(Screen screen : screens){
			screen.dispose();
		}
		
		Assets.dispose();
		menuMusic.dispose();
		NetworkDependentTracker.dispose();
		Popup.disposePopups();
		overlayStage.dispose();
		
		me = null;
		State.activeState = null;
	}
	
	private HashSet<Screen> screens  = new HashSet<Screen>(); 
	public Stack<Screen> screenStack = new Stack<Screen>();
	
	@Override
	public void setScreen(Screen screen){
		
		if(super.getScreen() != null && !(super.getScreen() instanceof GEngine))
			screenStack.add(super.getScreen());
		
		super.setScreen(screen);
		
		screens.add(screen);
		
		if(screen instanceof GEngine){
			overlayStage.addActor(new TransitionEffect());
			
			// do not stop menu music, menu music even ingame
			// menuMusic.setLooping(false);
		}
		else if(screen instanceof MainMenu){
			menuMusic.setLooping(true);
			
			if(!menuMusic.isPlaying() && Options.soundOn){
				menuMusic.play();
			}
		}
	}
	
	public void popScreen(){
		if(screenStack.size()>0){
			super.setScreen(screenStack.pop());
		}
		else
			Gdx.app.error("Main", "Tried to pop with an empty screen stack.");
	}
	
	public void addProcessor(InputProcessor ip){
		inputMultiplexer.addProcessor(ip);
	}
	
	public void removeProcessor(InputProcessor ip){
		inputProcessorsToBeRemoved.add(ip);
	}
	
	public void login(String username, String password){
		user.username = username;
		user.password = password;
		network.login(username, password);
	}

	@Override
	public void randomGameStarted(long gameID, final long seed, long timelimit, final Player[] participants) {
		new NetworkGameInstance(gameID, participants, seed, timelimit).showGame(true);
		if(Popup.currentPopup != null) Popup.currentPopup.close();
	}
	
	@Override
	public void loginAccepted(long userID, String displayName) {
		Gdx.app.log("Main", String.format("Login accepted, logged in with display name '%s.'", displayName));
		
		user.displayName = displayName;
		
		// Scrap old gameRequests if user changes
		if(userID != User.globalUserID) {
			gameRequests.clear();
			MainMenu.getInstance().updateGamesList();
		}

		User.globalUserID = userID;
		user.toFile(Gdx.files.local("login.bin"));
		
		if(Popup.currentPopup == AccountMenu.getInstance().connectingPopup) Popup.currentPopup.close();
		
		NetworkDependentTracker.changeLogin(displayName);
		if(getScreen() instanceof AccountMenu)
			popScreen();
	}

	@Override
	public void loginDenied(String reason) {
		Gdx.app.log("Main", String.format("Login denied, reason: '%s'.", reason));
		Popup.showGenericPopup("Could not log in", reason);
		if(Popup.currentPopup == AccountMenu.getInstance().connectingPopup) Popup.currentPopup.close();
	}
	
	@Override
	public void quickLoginAccepted(long userID, String username, String password){
		Gdx.app.log("Main", String.format("Quick login successful, logged in with username / display name: '%s.'", username));
		
		if(user==null)
			user = new User();
		user.username = username;
		user.password = password;
		user.displayName = username;
		User.globalUserID = userID;
		user.toFile(Gdx.files.local("login.bin"));
		
		if(Popup.currentPopup == AccountMenu.getInstance().connectingPopup) Popup.currentPopup.close();
		
		NetworkDependentTracker.changeLogin(username);
		if(getScreen() instanceof AccountMenu)
			popScreen();
	}
	
	@Override
	public void quickLoginRejected(String reason){
		String message = String.format("Quick login rejected, reason: '%s'.", reason);
		Gdx.app.log("Main", message);
		Popup.showGenericPopup("Failed to login", message);
		if(Popup.currentPopup == AccountMenu.getInstance().connectingPopup) Popup.currentPopup.close();
		this.user = null;
	}

	@Override
	public void waitOpponentsRandom(long nonce) {
		gameRequests.get(nonce).status = GameRequest.STATUS_WAIT_OPPONENT;
		MainMenu.getInstance().updateGamesList();
	}

	@Override
	public void turns(long gameID, int turnStart, Date timestamp, int stateChecksum, Queue<Queue<Command>> turns) {
		long diff = new Date().getTime() - timestamp.getTime();
		Gdx.app.log("Main", String.format("Received %d turns (turn %d to turn %d) for gameID %d. (Last turn submitted: %.1f seconds ago)", turns.size(), turnStart, turnStart + turns.size(), gameID, diff / 1000f));
		
		NetworkGameInstance game = NetworkGameInstance.getGame(gameID);
		
		// Shouldn't happen unless something goes wrong, games are never removed (only overwritten) from the NetworkGameInstance list
		if(game == null) throw new IllegalStateException("Received turns from server for a game that doesn't exist!");
		
		// Check if state is corrupted
		int newStateChecksum = game.lastStateChecksum + turns.hashCode();
		
		if(newStateChecksum != stateChecksum){
			Gdx.app.log("Main", String.format("State checksum did not match! %d (client) != %d (server) Game state is corrupted.", stateChecksum, newStateChecksum));
		}
		else{
			Gdx.app.log("Main", String.format("State checksum matches. (%d)", newStateChecksum));
		}	
		
		game.lastStateChecksum = stateChecksum;
		
		// If this is the currently active game, execute commands immediately
		if(getScreen() instanceof GEngine && State.activeState == game.state){
			game.endTurns(turns.size(), timestamp);
			Gdx.app.log("Main", "Game is active, executing turns immediately.");
			
			for(Queue<Command> turn : turns){
				CommandHandler.execute(turn);
			}
		}
		else{
			Gdx.app.log("Main", "Game is NOT active, adding turns to queue.");
			game.queueTurns(turns, turnStart, timestamp);
			MainMenu.getInstance().updateGamesList();
		}
	}
	
	@Override
	public void loginOverrided(String reason) {
		Gdx.app.log("Main", String.format("Login overrided, user has been logged out. Reason: '%s'.", reason));
		NetworkDependentTracker.changeLogin(null);
	}
	
	@Override
	public void connectionLost(Throwable cause) {
		NetworkDependentTracker.changeLogin(null);
		Gdx.app.log("Main", String.format("Network connection lost due to '%s'.", cause.getMessage()));
	}

	//private Label waitingForRandomGameLabel;
	//private Popup waitingForRandomGamePopup;
	
	public void startRandomGameSearch(){
		if(!Main.getInstance().network.isLoggedIn()){
    		if(Popup.currentPopup != null) Popup.currentPopup.close();
    		Popup.showGenericPopup("Please log in", "You need to be logged in to join a random game.");
    		return;
    	}
		
		/*
		if(waitingForRandomGamePopup == null){
			waitingForRandomGamePopup = new Popup(new PopupHandler() {
				@Override
				public void handlePopupAction(String text) {
					if("Cancel".equals(text)){
						network.cancelRandomGameSearch();
						waitingForRandomGamePopup.close();
					}
				}
			}, "Quick play", true,
					waitingForRandomGameLabel = new Label("", Assets.SKIN),
					new Row(null, new TextButton("Cancel", Assets.SKIN)));
		}
		*/
		
		// 2 player game per default
		long nonce = new Random().nextLong();
		GameRequest temp = new GameRequest(nonce, "Random game");
		gameRequests.put(nonce, temp);
		
		//TODO insert correct times
		network.findRandomGame(nonce, 604800000l, 604800000l, GameType.TYPE_ALL);
		
		//waitingForRandomGameLabel.setText("Connecting to server...");
		//waitingForRandomGamePopup.show();
		
		MainMenu.getInstance().updateGamesList();
		setScreen(MainMenu.getInstance());
	}
	
	public void hostCustomGame(long timeLimit, String gameName, String password, Map map){
		long nonce = random.nextLong();
		GameRequest temp = new GameRequest(nonce, timeLimit, gameName, password, map);
		gameRequests.put(nonce, temp);
		
		MainMenu.getInstance().updateGamesList();
		setScreen(MainMenu.getInstance());
		network.hostCustomGame(nonce, timeLimit, gameName, password, map);
	}
	
	public void hostGameRandom(int width, int height, long timeLimit, String gameName, String password) {
		long nonce = random.nextLong();
		GameRequest temp = new GameRequest(nonce, timeLimit, gameName, password);
		gameRequests.put(nonce, temp);
		
		network.hostRandomGame(nonce, timeLimit, gameName, password, width, height);
		
		MainMenu.getInstance().updateGamesList();
		setScreen(MainMenu.getInstance());
	}
	
	public void joinGame(String gameName, String password){
		long nonce = random.nextLong();
		GameRequest temp = new GameRequest(nonce);
		gameRequests.put(nonce, temp);
		MainMenu.getInstance().updateGamesList();
		
		network.joinGame(nonce, gameName, password);
	}

	@Override
	public void joinGameFailed(long nonce, String reason) {
		
		GameRequest gameRequest = gameRequests.remove(nonce);
		
		if(gameRequest != null){
			MainMenu.getInstance().updateGamesList();
			Popup.showGenericPopup(String.format("Error joining game '%s'.", gameRequest.gameName), reason);
		}
		
		Gdx.app.log("Main", "Game join failure");
	}

	@Override
	public void joinGameSuccess(long nonce, long id, long seed, long limit,
			String name, Map map, int type, long opponentId, String opponentName) {
		
		GameRequest temp = gameRequests.remove(nonce);
		if (temp == null) {
			Gdx.app.log("Main", "Cannot find gamerequest");
		}
		else  {
			GameInstance.createOnlineGame(id, seed, limit, name, map, opponentId, opponentName, false);
			Popup.showGenericPopup("Game joined", "Joined game '"+name+"' against '"+opponentName+"'");
			MainMenu.getInstance().updateGamesList();
			
		}
	}
	
	public void cancelRequest(long nonce)
	{
		gameRequests.remove(nonce);
		MainMenu.getInstance().updateGamesList();
		network.cancelRequest(nonce);
	}

	@Override
	public void hostGameFailed(long nonce, String reason) {
		gameRequests.remove(nonce);
		MainMenu.getInstance().updateGamesList();
		Popup.showGenericPopup("Error hosting game", reason);
	}

	@Override
	public void hostWaitOpponent(long nonce, String gameName) {
		gameRequests.get(nonce).status = GameRequest.STATUS_WAIT_OPPONENT;
		gameRequests.get(nonce).gameName = gameName;
		
		MainMenu.getInstance().updateGamesList();
	}

	@Override
	public void hostGameStart(long nonce, long gameID, long seed, String gameName, long opponentId, String opponentName) {		
		GameRequest temp = gameRequests.remove(nonce);
		
		if (temp == null) {
			Gdx.app.log("Network", "Error: GAME_HOST_START: No gamerequest was found");
		}
		else {
			// public void hostGameStart(long nonce, long id, long seed, String gameName, long opponentId, String opponentName) {
			GameInstance.createOnlineGame(gameID, seed, temp.timeLimit, gameName, temp.map, opponentId, opponentName, true);
			MainMenu.getInstance().updateGamesList();
			Gdx.app.log("Network", "GAME_HOST_START: "+opponentName+" has joined game");
			Popup.showGenericPopup("Game started", opponentName+" has joined your game");
		}
	}

	@Override
	public void hostGameRandom(long nonce, long gameID, long seed, long limit, String gameName, Map map, int type, long opponentId, String opponentName) {
		GameRequest temp = gameRequests.remove(nonce);
		
		if (temp == null) {
			Gdx.app.log("Network", "Error: GAME_RANDOM_HOST: No gamerequest was found");
		}
		else {
			GameInstance.createOnlineGame(gameID, seed, limit, gameName, map, opponentId, opponentName, true);
			MainMenu.getInstance().updateGamesList();
			Gdx.app.log("Network", "GAME_RANDOM_HOST: "+opponentName+" has joined game");
			Popup.showGenericPopup("Game started", opponentName+" has joined your game");
		}
	}
	
	public void commandRefused(final String cause)
	{
		Gdx.app.postRunnable(new Runnable(){
			@Override
			public void run()
			{
				Popup.showGenericPopup("Command refused", cause);
			}
		});
	}

	@Override
	public void receivedServerMaps(HashMap<Long, Map> maps) {
		//TODO: do something with maps later
	}

	@Override
	public void generateMap(long nonce, String name, int width, int height, long seed) {
		Map map = new Map(width,height,new Random(seed));
		gameRequests.get(nonce).map = map;
		MainMenu.getInstance().updateGamesList();
		network.mapGenerated(name, map);
	}
	
}
