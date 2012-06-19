package com.anstrat.mapEditor;


import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Building;
import com.anstrat.geography.Map;
import com.anstrat.geography.TerrainType;
import com.anstrat.gui.GBuilding;
import com.anstrat.gui.GTile;
import com.anstrat.gui.UI;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.Popup;
import com.anstrat.popup.PopupListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.FlickScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class MapEditorUI extends UI {

	private Button terrainToggle, buildingsToggle;
	private Image ttImg, btImg;
	
	public Popup popupLoadMap,	popupSaveMap, popupChangeOwner, popupNewMap, popupClearMap;

	private Table permanentTable;
	private int size;
	public Table tblChangeOwner, panelTable;
	
	public MapEditorUI(MapEditor editor, SpriteBatch batch, OrthographicCamera camera) {
		super(batch, camera);
		
		/**
		 * COMMON STUFF
		 */
		TerrainType[] terrainTypes = TerrainType.values();
		size = (int)(Gdx.graphics.getWidth() / (float)(terrainTypes.length-1));
		int bsize = (int)(size/1.2);
		int padding = (int)(Main.percentWidth*4);
		ClickListener clHidePanel = new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	showPanel(null,false);
            }
        };
		
		
		/**
		 * BUILDING BUTTONS
		 */
		final Table buildingsTable = new Table();
		buildingsTable.parse("center paddingTop:"+(int)((size-bsize)/2)+"* spacing:"+padding);
		Button village = ComponentFactory.createButton(Assets.getTextureRegion("village"), "image", new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	MapEditor.getInstance().actionHandler.select(Building.TYPE_VILLAGE);
            }
        });
        buildingsTable.add(village).size(bsize);

        Button castle = ComponentFactory.createButton(Assets.getTextureRegion("castle"), "image", new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	MapEditor.getInstance().actionHandler.select(Building.TYPE_CASTLE);
            }
        });
        buildingsTable.add(castle).size(bsize);
        buildingsTable.height(size);
		
		
		/**
		 * TERRAIN BUTTONS
		 */
		final Table terrainTable = new Table();
		terrainTable.parse("center paddingTop:"+(int)((size-bsize)/2)+"* spacing:"+padding);
		for(final TerrainType t : terrainTypes){
			if(t==TerrainType.CASTLE || t==TerrainType.VILLAGE)
				continue;
			Button button = ComponentFactory.createButton(GTile.getTextures(t)[0], "image", new ClickListener() {
		        @Override
		        public void click(Actor actor,float x,float y ){
		        	MapEditor.getInstance().actionHandler.select(t);
		        }
		    });
			terrainTable.add(button).size(bsize);
		}
		terrainTable.height(size);
		
		/**
		 * Tables for bottom extra panel
		 */
		// Clear map table
		final Table tblClearMap = new Table(Assets.SKIN);
		ClickListener clClearMap = new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	MapEditor.getInstance().actionHandler.clearMap();
            	showPanel(null, false);
            }
        };
		tblClearMap.register("ok",ComponentFactory.createButton("Ok", null, clClearMap));
		tblClearMap.register("cancel",ComponentFactory.createButton("Cancel", null, clHidePanel));
		tblClearMap.parse(
				"'Clear map?'" +
				"---" +
				"{* min:1 height:"+bsize+" [ok][cancel]} fill:80,100 ");
		
		
		//Change owner table
		tblChangeOwner    = new Table(Assets.SKIN);
		ClickListener showChange = new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	MapEditor.getInstance().actionHandler.changeOwner(actor.name);
            	showPanel(null,false);
            }
        };
		tblChangeOwner.register("none",ComponentFactory.createButton("none", "none", showChange));
		tblChangeOwner.register("0",ComponentFactory.createButton("0", "0", showChange));
		tblChangeOwner.register("1",ComponentFactory.createButton("1", "1", showChange));
		tblChangeOwner.register("cancel",ComponentFactory.createButton("Cancel", null, clHidePanel));
		tblChangeOwner.parse(
				"'Select new owner'" +
				"---" +
				"{* min:1 height:"+bsize+" [0][1][none][cancel]}");
		
		/**
		 * PERMANENT BUTTONS
		 */
		permanentTable = new Table(Assets.SKIN);
		permanentTable.setBackground(new NinePatch(Assets.getTextureRegion("BottomLargePanel")));
		
        ttImg = new Image(Assets.getTextureRegion("terrain-button"));
		terrainToggle = new Button(ttImg, Assets.SKIN.getStyle("image", ButtonStyle.class));
		terrainToggle.setClickListener( new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	showPanel(terrainTable,terrainTable.parent==null);
            }
        } );
		permanentTable.register("terrain", terrainToggle);
		btImg = new Image(Assets.getTextureRegion("building-button"));
		buildingsToggle = new Button(btImg, Assets.SKIN.getStyle("image", ButtonStyle.class));
		buildingsToggle.setClickListener( new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	showPanel(buildingsTable,buildingsTable.parent==null);
            }
        } );
		permanentTable.register("buildings", buildingsToggle);
		permanentTable.register("clear", ComponentFactory.createButton(Assets.getTextureRegion("cancel"),
				new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	showPanel(tblClearMap,tblClearMap.parent==null);
            }
        } ));
		permanentTable.register("load", ComponentFactory.createButton(Assets.getTextureRegion("open-button"), "image",
				new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
    			popupLoadMap = getMapsPopup();
    			if (popupLoadMap != null) {
    				popupLoadMap.show();
    			}
    			else {
    				Popup.showGenericPopup("Error", "No maps found");
    			}
            }
        } ));
		permanentTable.register("save", ComponentFactory.createButton(Assets.getTextureRegion("save-button"), "image",
				new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	popupSaveMap.show();
            }
        } ));
		TextButton newMap = ComponentFactory.createButton("NEW", null, new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	popupNewMap.show();
            }
        } );
		permanentTable.register("new", newMap);

		addActor(permanentTable);
		
		
		/**
		 * The "extra" panel that appears on top of the permanent one
		 */
		panelTable = new Table();
		panelTable.align("center");
		panelTable.setBackground(new NinePatch(Assets.getTextureRegion("BottomLargePanel")));
		panelTable.visible = false;
		addActor(panelTable);
		
		
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		/**
		 * POPUPS
		 */
		popupSaveMap = new Popup(new PopupListener(true) {
		            @Override
		            public void handle(String text){
		            	String name = ComponentFactory.getTextFieldValue(popupSaveMap,"name");
						if(name.length()>0){
							MapEditor.getInstance().saveMap(name);
							Popup.currentPopup.clearInputs();
							Popup.currentPopup.close();
						}
		            }
		        }, "Save map", 
				ComponentFactory.createTextField("Map name","name",false),
				new Row(ComponentFactory.createButton("Ok",Popup.OK),ComponentFactory.createButton("Cancel",Popup.CANCEL)));
		
		Table table = new Table();
		
		FlickScrollPane scroll1 = new FlickScrollPane(table);
		FlickScrollPane scroll2 = new FlickScrollPane(table);
		
		table.parse("pad:10 * expand:x space:4");
		for (int i = Map.MIN_SIZE; i <= Map.MAX_SIZE; i++) {
		    table.row();
		    table.add(new Label(new Integer(i).toString(), new LabelStyle(Assets.MENU_FONT, Color.RED))).expandX().fillX();
		}
		
		popupNewMap = new Popup(new PopupListener() {
		            @Override
		            public void handle(String text){
		        		try {
		        			int width = Integer.parseInt(ComponentFactory.getTextFieldValue(MapEditor.getInstance().userInterface.popupNewMap,"width"));
		        			int height = Integer.parseInt(ComponentFactory.getTextFieldValue(MapEditor.getInstance().userInterface.popupNewMap,"height"));
		        			MapEditor.getInstance().actionHandler.createNewMap(width, height);
		        		} catch(Exception e){
		        			System.err.println("New map size is not an int.");
		        			return;
		        		}
		        		Popup.currentPopup.clearInputs();
		            }
		        }, "New map", 
				//new Row(ComponentFactory.createTextField("Map width","width",false), ComponentFactory.createTextField("Map height","height",false)),
				new Row(new Label("Width", new LabelStyle(Assets.MENU_FONT, Color.WHITE)),
						new Label("Height", new LabelStyle(Assets.MENU_FONT, Color.WHITE))),
		        new Row(scroll1, scroll2),
		        new Row(ComponentFactory.createButton("Ok",Popup.OK),ComponentFactory.createButton("Cancel",Popup.CANCEL)));
	}
	
	public void showChangeOwner(){
		showPanel(tblChangeOwner,true);
	}
	
	/**
	 * Shows the additional panel at the bottom
	 * @param contents A table of elements to be added to the panel.
	 * @param show Whether to show the panel or hide it.
	 */
	public void showPanel(Table contents, boolean show){ //TODO Add animation sliding panel up or down.
		panelTable.clear();
		panelTable.visible = show;
		if(show && contents!=null){
			panelTable.add(contents).fill().pad((int)Main.percentHeight);
			panelTable.height = contents.getPrefHeight();
		}
	}
	
	/**
	 * @return The height of the UI
	 */
	public int getHeight(){
		return (int)( (panelTable.visible) ? (panelTable.y+panelTable.height) : permanentTable.height );
	}
	
 	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		this.size = (int)(width / (float)(TerrainType.values().length-1));
		
		permanentTable.parse("height:"+(int)(size*1.05)+" align:left,bottom * size:"+size+" [clear][terrain][buildings][load][save][new] height:"+(int)(size/1.2));
		permanentTable.pack();
		
		permanentTable.y = permanentTable.x = 0;
		permanentTable.width = width;
		permanentTable.height = size*1.05f;
		
		panelTable.y = permanentTable.height - 0.05f*size; 
		panelTable.x = 0;
		panelTable.width  = width;
		panelTable.height = permanentTable.height;
	}
 	
 	/**
 	 * Sets image of selected terrain/building on respective toggle, resets the other and closes menu.
 	 * @param isTerrain Did the user click on a terrain button?
 	 */
 	public void changeSelectionType(boolean isTerrain){
 		int bsize = (int)(size / 1.2);
 		if(isTerrain){
	   		ttImg.setRegion(GTile.getTextures((TerrainType)MapEditor.getInstance().actionHandler.selected)[0]);
	   		btImg.setRegion(Assets.getTextureRegion("building-button"));
	   		terrainToggle.size(bsize, bsize);
	   		buildingsToggle.size(size, size);
 		}
 		else{
       		btImg.setRegion(GBuilding.getTextureRegion((Integer)MapEditor.getInstance().actionHandler.selected));
       		ttImg.setRegion(Assets.getTextureRegion("terrain-button"));
	   		terrainToggle.size(size, size);
	   		buildingsToggle.size(bsize, bsize);
 		}
 		showPanel(null,false);
 		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
 	}
	
	/**
	 * Did the tap hit the UI? 
	 */
	public boolean tap(int x, int y, int count){
		if(y >= Gdx.graphics.getHeight()-this.getHeight())
			return true;
		return false;
	}
	
	/**
	 * Get popup containing all maps. 
	 */
	public Popup getMapsPopup() {
		String[] mapStrings = Assets.getMapList(true, false);
		if (mapStrings.length > 0) {
			return new MapsPopup(new PopupListener() {
	            @Override
	            public void handle(String text){
	            	MapEditor.getInstance().initMap(Assets.loadMap(text, false));
	            }
	        },false,"Load map",mapStrings);
		}
		return null;
	}
}
