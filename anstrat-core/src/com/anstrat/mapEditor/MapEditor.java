package com.anstrat.mapEditor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import com.anstrat.core.CameraController;
import com.anstrat.core.Main;
import com.anstrat.core.Options;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;
import com.anstrat.gui.CameraUtil;
import com.anstrat.gui.FloatingBackground;
import com.anstrat.gui.GBuilding;
import com.anstrat.gui.GMap;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MapEditor implements Screen {
	
	public static final int STANDARD_ROWS    = 10;
	public static final int STANDARD_COLUMNS = 10;
	
	private static MapEditor me;
	
	private OrthographicCamera camera;
	private CameraController cameraController;
	private SpriteBatch batch;
	private FloatingBackground floatingBackground;
	private MapEditorInputHandler inputHandler;
	
	public GMap gMap;
	public Map map;
	public MapEditorActionHandler actionHandler;
	public HashMap<Integer,GBuilding> gBuildings;
	
	public MapEditorUI userInterface;
	
	public int nextPlayerToRecieveCastle = 0;
	
	private MapEditor() {
		batch = Main.getInstance().batch;
		camera = new OrthographicCamera();
		camera.setToOrtho(true);
		cameraController = new CameraController(camera);
		userInterface = new MapEditorUI(this, batch, new OrthographicCamera());
		actionHandler = new MapEditorActionHandler();
		inputHandler  = new MapEditorInputHandler();
		gBuildings = new HashMap<Integer, GBuilding>();
		floatingBackground = new FloatingBackground(camera);

		initMap(new Map(STANDARD_COLUMNS, STANDARD_ROWS));
	}
	
	public static synchronized MapEditor getInstance() {
		if (me == null) {
			me = new MapEditor();
		}
		return me;
	}
	
	/**
	 * Resets everything and initializes a new {@link map}.
	 * @param map Map to initialize.
	 */
	public void initMap(Map map){
		this.map = map;
		gMap = new GMap(map, camera);
		
		cameraController.setBounds(gMap.getWidth(), gMap.getHeight());
		cameraController.setZoomLimits(gMap.getWidth(), gMap.getHeight(), gMap.TILE_WIDTH * 2.5f, gMap.TILE_HEIGHT * 2.5f);
		
		gBuildings.clear();
		for(Building b: map.buildingList.values()) {
			gBuildings.put(b.id, new GBuilding(b,gMap,0));
			if (b.type == Building.TYPE_CASTLE) {
				if (b.controllerId == Player.PLAYER_1_ID)
					nextPlayerToRecieveCastle = Player.PLAYER_2_ID;
				else
					nextPlayerToRecieveCastle = Player.PLAYER_1_ID;
			}
		}
	}
	
	/**
	 * Saves the map in the map folder.
	 * @param filename The name of the file to save it in.
	 */
	public void saveMap(String filename)
	{
		if(map.getCastleCount() < 2)
		{
			Popup.showGenericPopup("Error", "You must have two capital towns placed on the map.");
		}
		else
		{
			map.name = filename;
			FileHandle fh = Gdx.files.external("soimaps/"+filename+".smap");
			System.out.println(fh.file().getAbsolutePath());
			try {
				ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(fh.write(false)));
				oos.writeObject(MapEditor.getInstance().map);
				oos.flush();
				oos.close();
			} catch (IOException e) {
				Popup.showGenericPopup("Error", "Could not save map.");
				e.printStackTrace();
			}
			catch (GdxRuntimeException e) {
				Popup.showGenericPopup("Could not save map", "You probably don't have external memory on your phone.");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void render(float delta) {
		GL10 gl = Gdx.graphics.getGL10();
		cameraController.update(delta);
		camera.update();
		camera.apply(gl);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		floatingBackground.draw(batch);
		batch.flush();
		
		gMap.render(gl);

		
		
		for(GBuilding b : gBuildings.values()){			
			// Only render unit if visible
			if(camera.frustum.boundsInFrustum(b.getBoundingBox())){
				b.render(batch, delta);
			}
		}
		batch.end();
		userInterface.draw();
	}

	@Override
	public void resize(int width, int height) {		
		CameraUtil.resizeCamera(camera, width, height);
		userInterface.resize(width, height);
		cameraController.resize();
		cameraController.setOffsets(Options.mapBorderOffset, userInterface.getHeight()+Options.mapBorderOffset, Options.mapBorderOffset, Options.mapBorderOffset);
	}

	@Override
	public void show() {
		Main m = Main.getInstance();
		m.gestureMultiplexer.addProcessor(inputHandler);
		m.gestureMultiplexer.addProcessor(cameraController);
		m.addProcessor(cameraController);
		m.addProcessor(userInterface);
	}

	@Override
	public void hide() {
		Main m = Main.getInstance();
		m.gestureMultiplexer.removeProcessor(inputHandler);
		m.gestureMultiplexer.removeProcessor(cameraController);
		m.removeProcessor(cameraController);
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

	@Override
	public void dispose() {
		userInterface.dispose();
		me = null;
	}
}
