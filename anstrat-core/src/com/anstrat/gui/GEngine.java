package com.anstrat.gui;

import java.nio.IntBuffer;
import java.util.HashMap;

import com.anstrat.animation.Animation;
import com.anstrat.animation.AnimationHandler;
import com.anstrat.animation.MoveCameraAnimation;
import com.anstrat.animation.ZoomCameraAnimation;
import com.anstrat.core.Assets;
import com.anstrat.core.CameraController;
import com.anstrat.core.GestureMultiplexer;
import com.anstrat.core.Main;
import com.anstrat.core.Options;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.StateUtils;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.confirmDialog.ConfirmDialog;
import com.anstrat.gui.confirmDialog.ConfirmOverlay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;

/**
 * 
 * @author Anton
 *
 *A singleton graphics engine used for handling the ingame screen.
 *
 */
public class GEngine implements Screen{
	
	/**If true prints coordinates for all tiles*/
	private boolean showCoordinates = false;
	
	/**
	 * The zoom level at the start of the game, in number of tile widths.
	 */
	public static final float DEFAULT_ZOOM_LEVEL = 4f;
	public boolean USE_SMOOTH_LINES = true;
	
	/** Whether to render the hexagon tiles in a flat or pointy orientation */
	public static boolean FLAT_TILE_ORIENTATION = false;
	
	private static GEngine me;
	public GestureMultiplexer multiplexer;
	private InputHandler inputHandler;
	public ActionHandler actionHandler;
	public Highlighter highlighter;
	public GameUI userInterface;
	public ConfirmDialog confirmDialog = new ConfirmDialog(0);
	public ConfirmOverlay confirmOverlay = new ConfirmOverlay();
	public AnimationHandler animationHandler = new AnimationHandler();
	public SelectionHandler selectionHandler = new SelectionHandler();
	public FloatingBackground floatingBackground;
	private SpriteBatch batch;
	
	public HashMap<Integer,GUnit> gUnits;
	public HashMap<Integer,GBuilding> gBuildings;
	
	public final OrthographicCamera camera;
	public final OrthographicCamera uiCamera;
	public CameraController cameraController;
	
	public GMap map;
	public ActionMap actionMap;
	public State state;
	
	public static float elapsedTime = 0f;

	private Vector2 FPS_POSITION = new Vector2();
	
	public static synchronized GEngine getInstance(){
		if(me == null){
			me = new GEngine();
		}
		return me;
	}

	/** Will initiate graphical components. MUST NOT have anything to do with state. */
	private GEngine(){
		Gdx.app.log("GEngine", "Running constructor");
		
		batch = Main.getInstance().batch;
		camera = new OrthographicCamera();
		camera.setToOrtho(true);
		
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false);
		
		userInterface = new GameUI(uiCamera);
		
		inputHandler = new InputHandler();
		actionHandler = new ActionHandler();
		highlighter = new Highlighter(this);
		
		gUnits = new HashMap<Integer,GUnit>();
		gBuildings = new HashMap<Integer,GBuilding>();
		decideSmoothLines();
		
		cameraController = new CameraController(camera);
		Main.getInstance().gestureMultiplexer.addProcessor(cameraController);
		Main.getInstance().addProcessor(cameraController);
	}
	
	
	/**
	 * A proper initialization depending on the actual gamecore. Must clean up the current GEngine
	 * @param state
	 */
	public void init(State state, boolean startZoom){
		Gdx.app.log("GEngine", "init");
		
		this.state = state;
		animationHandler.clearAnimations();
		userInterface.updateCurrentPlayer();
		map = new GMap(state.map, camera);
		actionMap = new ActionMap();
		
		
		cameraController.setBounds(map.getWidth(), map.getHeight());
		cameraController.setZoomLimits(map.getWidth(), map.getHeight(), map.TILE_WIDTH * 2.5f, map.TILE_HEIGHT * 2.5f);
		cameraController.setOffsets(userInterface.topPanel.height+Options.mapBorderOffset, userInterface.bottomPanel.height+Options.mapBorderOffset, Options.mapBorderOffset, Options.mapBorderOffset);
		
		if (!startZoom)
			camera.zoom = (map.TILE_WIDTH * DEFAULT_ZOOM_LEVEL) / Gdx.graphics.getWidth();
		else
			camera.zoom = (Math.min(map.getWidth(), map.getHeight())) / Gdx.graphics.getWidth();
		cameraController.checkBounds();
		camera.position.x = map.getWidth()/2;
		camera.position.y = map.getHeight()/2;
		floatingBackground = new FloatingBackground(camera);
		
		gUnits.clear();
		gBuildings.clear();
		for(Unit u : state.unitList.values()){
			gUnits.put(u.id, new GUnit(u));
		}	
		for(Building b : state.map.buildingList.values()){
			gBuildings.put(b.id,new GBuilding(b,map));
		}
		
		//camera.lookAt(camera.position.x+100,camera.position.y, camera.position.z);
		
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		selectionHandler.deselect();
		
		//TODO
		if (startZoom) {
			Animation zoom = new ZoomCameraAnimation((map.TILE_WIDTH * DEFAULT_ZOOM_LEVEL) / Gdx.graphics.getWidth(), 2f);
			Animation move = new MoveCameraAnimation(map.getTile(StateUtils.getCurrentPlayerCastle().tileCoordinate).getCenter(), 2f);
			
			animationHandler.runParalell(zoom);
			animationHandler.runParalell(move);
		}
	}
	
	/**
	 * Instantly mimics the gamestate by placing all GUnits at their proper tiles, update all graphics etc.
	 * Early versions might call this after every executed Command to verify effects.
	 * Final version of the game 'should' never need to use it, but it's needed to fix things if the graphic state mess things up.
	 */
	public void syncToState(){
		animationHandler.clearAnimations();
		for(GUnit gu : gUnits.values()){
			gu.setPosition(map.getTile(gu.unit.tileCoordinate).getCenter());
		}
		// TODO find more things that must be synced.
		
	}
	
	public void updateUI(){
		if(state.players[state.currentPlayerId].getAI() != null) return;
		userInterface.update();
		
		if(selectionHandler.selectionType == SelectionHandler.SELECTION_UNIT){
			actionHandler.refreshHighlight(selectionHandler.selectedUnit);
		}
	}
	
	@Override
	public void dispose(){
		me = null;
		userInterface.dispose();
	}
	
	public GUnit getUnit(Unit unit)
	{
		return gUnits.get(unit.id);
	}
	
	public GBuilding getBuilding(Building building)
	{
		return gBuildings.get(building.id);
	}
	
	/**
	 * Draws all units and animations, and the user interface
	 */
	@Override
	public void render(float delta) {
		delta = delta*Options.speedFactor;
		elapsedTime += delta;
		animationHandler.runAll(delta);
		
		GL10 gl = Gdx.graphics.getGL10();
		cameraController.update(delta);
		camera.update();
		camera.apply(gl);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		floatingBackground.draw(batch);
		batch.flush();
		
		map.render(gl);

		//batch.begin();
		
		highlighter.renderBackground(batch);
		
		for(GBuilding b : gBuildings.values()){			
			// Only render unit if visible
			if(camera.frustum.boundsInFrustum(b.getBoundingBox())){
				b.render(batch, delta);
			}
		}
		
		if(actionHandler.showingConfirmDialog)
			confirmOverlay.drawBottomLayer(batch);
		
		for(GUnit u : gUnits.values()){			
			// Only render unit if visible
			if(camera.frustum.boundsInFrustum(u.getBoundingBox())){
				u.render(batch, delta);
			}
		}
		
		highlighter.render(batch);
		if(actionHandler.showingConfirmDialog)
			confirmOverlay.drawTopLayer(batch);
		if(showCoordinates){
			drawCoordinates();
		}
		
		animationHandler.drawAll(delta, batch);
		
		GFog.drawFog(batch);
		
		batch.end();
		
		animationHandler.drawAllFixed(delta, batch);
		
		userInterface.draw();
		if(actionHandler.showingConfirmDialog)
			confirmDialog.draw(batch);
		
		if(Options.showFps){
			batch.setProjectionMatrix(uiCamera.combined);
			batch.begin();
			Assets.UI_FONT_BIG.draw(batch, String.valueOf(Gdx.graphics.getFramesPerSecond()), FPS_POSITION.x, FPS_POSITION.y);
			batch.end();
		}
	}
	
	@Override
	public void resize(int width, int height) {
		CameraUtil.resizeCamera(camera, width, height);
		CameraUtil.resizeCamera(uiCamera, width, height);
		userInterface.resize(width, height);
		cameraController.resize();
		
		// Update offsets since ui has changed size
		cameraController.setOffsets(userInterface.topPanel.height+Options.mapBorderOffset, userInterface.bottomPanel.height+Options.mapBorderOffset, Options.mapBorderOffset, Options.mapBorderOffset);

		// Draw fps at a fixed position
		FPS_POSITION.x = 20f;
		FPS_POSITION.y = height - 70f;
	}
	
	@Override
	public void show() {
		Main m = Main.getInstance();
		m.addProcessor(cameraController);
		m.gestureMultiplexer.addProcessor(inputHandler);
		m.gestureMultiplexer.addProcessor(cameraController);
		m.addProcessor(userInterface);
		updateUI();
	}
	@Override
	public void hide() {
		Main m = Main.getInstance();
		m.removeProcessor(cameraController);
		m.gestureMultiplexer.removeProcessor(cameraController);
		m.gestureMultiplexer.removeProcessor(inputHandler);
		m.removeProcessor(userInterface);
	}
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	public GMap getMap(){
		return this.map;
	}
	
	public void setPosition(Vector3 position) {
		camera.position.set(position);
	}
	
	public Vector3 getPosition() {
		return camera.position;
	}
	
	public boolean tileIsOnScreen(GTile t) {
		Vector2 pos = t.getCenter();
		float x1 = camera.position.x-camera.viewportWidth*camera.zoom/2;
		float y1 = camera.position.y-camera.viewportHeight*camera.zoom/2;
		y1 += userInterface.topPanel.height*camera.zoom;
		float x2 = x1+camera.viewportWidth*camera.zoom;
		float y2 = y1+camera.viewportHeight*camera.zoom;
		y2 -= (userInterface.bottomPanel.height+userInterface.permanentPanel.height)*camera.zoom;
		
		if (pos.x < x1 || pos.x > x2 || pos.y < y1 || pos.y > y2)
			return false;
		else 
			return true;
	}

	private void drawCoordinates(){
		Assets.STANDARD_FONT.setColor(Color.WHITE);
		for(GTile[] tileRow : map.tiles){
			for(GTile tile : tileRow){
				Assets.STANDARD_FONT.getLineHeight();
				Assets.STANDARD_FONT.setScale(0.3f, 0.3f);
				Assets.STANDARD_FONT.draw(batch, " "+tile.tile.coordinates.x+":"+tile.tile.coordinates.y, tile.getCenter().x, tile.getCenter().y-30);
			}
		}
	
	}

	private void decideSmoothLines(){
		IntBuffer ib = BufferUtils.newIntBuffer(16);
		Gdx.gl.glGetIntegerv(GL10.GL_SMOOTH_LINE_WIDTH_RANGE, ib);
		if(ib.get(1) < 4){
			USE_SMOOTH_LINES = false;
		}else
			USE_SMOOTH_LINES = true;
	}
	
}
