package com.anstrat.mapEditor;


import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Building;
import com.anstrat.geography.Map;
import com.anstrat.geography.TerrainType;
import com.anstrat.gui.GBuilding;
import com.anstrat.gui.GTile;
import com.anstrat.gui.SnapScrollPane;
import com.anstrat.gui.UI;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class MapEditorUI extends UI {

	private Button terrainToggle, buildingsToggle, clearButton, newButton, saveButton, loadButton;
	private Image ttImg, btImg;
	
	public Popup popupLoadMap,	popupSaveMap, popupChangeOwner, popupNewMap, popupClearMap;

	private Table permanentTable;
	private int size;
	public Table tblChangeOwner, panelTable;
	public TextButton changeOwner0, changeOwner1, changeOwnerNone;
	
	public MapEditorUI(MapEditor editor, SpriteBatch batch, OrthographicCamera camera) {
		super(batch, camera);
		
		/**
		 * COMMON STUFF
		 */
		TerrainType[] terrainTypes = TerrainType.values();
		updateSize(Gdx.graphics.getWidth());
		int bsize = (int)(size/1.2);
		int padding = (int)(size-bsize);
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
		buildingsTable.padTop((int)((size-bsize)/2));
		buildingsTable.defaults().center().space(padding);
		
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
		
		terrainTable.padTop((int)((size-bsize)/2)).padBottom((int)((size-bsize)/2)).center();
		terrainTable.defaults().space(padding);
		int cnt = 0;
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
			if(++cnt % 6 == 0)
				terrainTable.row();
		}
		
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
        
		Table tblClearMapInner = new Table();
		tblClearMapInner.defaults().minSize(1).height(bsize);
		tblClearMapInner.add(ComponentFactory.createButton("Ok", clClearMap));
		tblClearMapInner.add(ComponentFactory.createButton("Cancel", clHidePanel));
		
		tblClearMap.add("Clear map?");
		tblClearMap.row();
		tblClearMap.add(tblClearMapInner).fill();
		
		//Change owner table
		tblChangeOwner    = new Table(Assets.SKIN);

		Table tblChangeOwnerInner = new Table();
		tblChangeOwnerInner.defaults().minSize(1).height(bsize);
		tblChangeOwnerInner.add(changeOwner0 = ComponentFactory.createButton("0", getChangeOwnerClickListener("0")));
		tblChangeOwnerInner.add(changeOwner1 = ComponentFactory.createButton("1", getChangeOwnerClickListener("1")));
		tblChangeOwnerInner.add(changeOwnerNone = ComponentFactory.createButton("none", getChangeOwnerClickListener("none")));
		tblChangeOwnerInner.add(ComponentFactory.createButton("Cancel", clHidePanel));
		
		tblChangeOwner.add("Select new owner");
		tblChangeOwner.row();
		tblChangeOwner.add(tblChangeOwnerInner);
		
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
		
		btImg = new Image(Assets.getTextureRegion("building-button"));
		buildingsToggle = new Button(btImg, Assets.SKIN.getStyle("image", ButtonStyle.class));
		buildingsToggle.setClickListener( new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	showPanel(buildingsTable,buildingsTable.parent==null);
            }
        } );
		
		clearButton = ComponentFactory.createButton(Assets.getTextureRegion("cancel"),
				new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	showPanel(tblClearMap,tblClearMap.parent==null);
            }
        });
		
		loadButton = ComponentFactory.createButton(Assets.getTextureRegion("open-button"), "image",
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
        });
		
		saveButton = ComponentFactory.createButton(Assets.getTextureRegion("save-button"), "image",
				new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	popupSaveMap.show();
            }
        });
		
		newButton = ComponentFactory.createButton("NEW", new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	popupNewMap.show();
            }
        });
		
		addActor(permanentTable);
		
		
		/**
		 * The "extra" panel that appears on top of the permanent one
		 */
		panelTable = new Table();
		panelTable.center();
		panelTable.setBackground(new NinePatch(Assets.getTextureRegion("BottomLargePanel")));
		panelTable.visible = false;
		addActor(panelTable);
		
		
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		/**
		 * POPUPS
		 */
		popupSaveMap = new Popup("Save map", ComponentFactory.createTextField("Map name", false));
		
		final TextField saveMapTextfield = ComponentFactory.createTextField("Map name", false);
		Button saveMapOKButton = ComponentFactory.createButton("Ok", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				String mapName = saveMapTextfield.getText();
				if(mapName.length() > 0){
					MapEditor.getInstance().saveMap(mapName);
					Popup.currentPopup.clearInputs();
					Popup.currentPopup.close();
				}
			}
		});
		
		popupSaveMap.setComponents(
				saveMapTextfield,
				new Row(saveMapOKButton, ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER)));
		
				
		Table table1 = new Table();
		Table table2 = new Table();
		
		Label[] labels1 = new Label[Map.MAX_SIZE-Map.MIN_SIZE+1];
		Label[] labels2 = new Label[Map.MAX_SIZE-Map.MIN_SIZE+1];
		
		table1.defaults().expandX();
		for (int i = Map.MIN_SIZE; i <= Map.MAX_SIZE; i++) {
		    table1.row();
		    Label label = new Label(new Integer(i).toString(), new LabelStyle(Assets.UI_FONT, Color.WHITE));
		    table1.add(label).center().padTop(5).padBottom(5);
		    labels1[i-Map.MIN_SIZE]= label; 
		}
		
		table2.defaults().expandX();
		for (int i = Map.MIN_SIZE; i <= Map.MAX_SIZE; i++) {
		    table2.row();
		    Label label = new Label(new Integer(i).toString(), new LabelStyle(Assets.UI_FONT, Color.WHITE));
		    table2.add(label).center().padTop(5).padBottom(5);label.getStyle().fontColor = Color.BLUE;
		    labels2[i-Map.MIN_SIZE]= label; 
		}
		
		final SnapScrollPane scroll1 = new SnapScrollPane(table1, labels1);
		final SnapScrollPane scroll2 = new SnapScrollPane(table2, labels2);

		Table flickTable1 = new Table();
		flickTable1.add(scroll1).fill().expand();
		flickTable1.setBackground(Assets.SKIN.getPatch("single-border"));
		Table flickTable2 = new Table();
		flickTable2.add(scroll2).fill().expand();
		flickTable2.setBackground(Assets.SKIN.getPatch("single-border"));
			
		Table flickTable = new Table(Assets.SKIN);
		flickTable.defaults().height((int)(Main.percentHeight*12f));
		flickTable.add(new Label("Width", new LabelStyle(Assets.MENU_FONT, Color.WHITE)));
		flickTable.add(new Label("Height", new LabelStyle(Assets.MENU_FONT, Color.WHITE)));
		flickTable.row().height((int)(Main.percentHeight*16f)).width((int)(Main.percentHeight*16f));
		flickTable.add(flickTable1);
		flickTable.add(flickTable2);
		
		
		popupNewMap = new Popup("New map");
		Button newMapOKButton = ComponentFactory.createButton("Ok", new ClickListener() {
			@Override
			public void click(Actor actor, float x, float y) {
				int mapWidth = Map.MIN_SIZE + (int)(scroll1.getScrollPercentY()*(Map.MAX_SIZE-Map.MIN_SIZE));
    			int mapHeight = Map.MIN_SIZE + (int)(scroll2.getScrollPercentY()*(Map.MAX_SIZE-Map.MIN_SIZE));
    			MapEditor.getInstance().actionHandler.createNewMap(mapWidth, mapHeight);
				Popup.currentPopup.close();
			}
		});
		
		popupNewMap.setComponents(flickTable,
		        new Row(newMapOKButton, ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER)));
	}
	
	private void updateSize(float width) {
		size = (int)(width / 6f);
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
		updateSize(width);
		
		permanentTable.height((int)(size*1.05)).left().bottom();
		permanentTable.defaults().size(size);
		permanentTable.add(clearButton);
		permanentTable.add(terrainToggle);
		permanentTable.add(buildingsToggle);
		permanentTable.add(loadButton);
		permanentTable.add(saveButton);
		permanentTable.add(newButton).height((int)(size/1.2f));
		
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
			return new MapsPopup(new MapsPopupHandler() {
				@Override
				public void mapSelected(String map) {
					MapEditor.getInstance().initMap(Assets.loadMap(map, false));
				}
			}, false, "Load map", mapStrings);
		}
		return null;
	}
	
	private ClickListener getChangeOwnerClickListener(final String owner){
		return new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	MapEditor.getInstance().actionHandler.changeOwner(owner);
            	showPanel(null,false);
            }
        };
	}
}
