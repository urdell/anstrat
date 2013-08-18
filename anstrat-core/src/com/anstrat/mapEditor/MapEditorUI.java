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
import com.anstrat.popup.NewMapPopup;
import com.anstrat.popup.MapsPopup;
import com.anstrat.popup.MapsPopup.MapsPopupHandler;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MapEditorUI extends UI {

	private Button terrainToggle, buildingsToggle, clearButton, newButton, saveButton, loadButton;
	private Image ttImg, btImg;
	
	public Popup popupLoadMap,	popupSaveMap, popupNewMap;

	private Table permanentTable;
	private float size;
	public Table tblChangeOwner, panelTable;
	public TextButton changeOwner0, changeOwner1, changeOwnerNone;
	
	public MapEditorUI(MapEditor editor, SpriteBatch batch, OrthographicCamera camera) {
		super(batch, camera);
		
		/**
		 * COMMON STUFF
		 */
		TerrainType[] terrainTypes = TerrainType.values();
		updateSize(Gdx.graphics.getWidth());
		float bsize = size/1.2f;
		float padding = size-bsize;
		ClickListener clHidePanel = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	showPanel(null,false);
            }
        };
		
		
		/**
		 * BUILDING BUTTONS
		 */
		final Table buildingsTable = new Table();
		buildingsTable.padTop((size-bsize)/2f);
		buildingsTable.defaults().center().space(padding);
		
		Button greenVillage = ComponentFactory.createButton(Assets.getTextureRegion("village-green"), "image", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	MapEditor.getInstance().actionHandler.select(Building.TYPE_GREENVILLAGE);
            }
        });
		Button rockyVillage = ComponentFactory.createButton(Assets.getTextureRegion("village-rocky"), "image", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	MapEditor.getInstance().actionHandler.select(Building.TYPE_ROCKVILLAGE);
            }
        });
		Button snowVillage = ComponentFactory.createButton(Assets.getTextureRegion("village-snow"), "image", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	MapEditor.getInstance().actionHandler.select(Building.TYPE_SNOWVILLAGE);
            }
        });
        buildingsTable.add(greenVillage).size(bsize);
        buildingsTable.add(rockyVillage).size(bsize);
        buildingsTable.add(snowVillage).size(bsize);
        

        Button castle = ComponentFactory.createButton(Assets.getTextureRegion("castle"), "image", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	MapEditor.getInstance().actionHandler.select(Building.TYPE_CASTLE);
            }
        });
        buildingsTable.add(castle).size(bsize);
        buildingsTable.setHeight(size);
		
		
		/**
		 * TERRAIN BUTTONS
		 */
		final Table terrainTable = new Table();
		
		terrainTable.padTop((size-bsize)/2f).padBottom((size-bsize)/2f).center();
		terrainTable.defaults().space(padding);
		int cnt = 0;
		for(final TerrainType t : terrainTypes){
			if(t==TerrainType.CASTLE || t==TerrainType.GREENVILLAGE || t==TerrainType.ROCKVILLAGE || t==TerrainType.SNOWVILLAGE)
				continue;
			Button button = ComponentFactory.createButton(GTile.getTextures(t)[0], "image", new ClickListener() {
		        @Override
		        public void clicked(InputEvent event, float x, float y) {
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
            public void clicked(InputEvent event, float x, float y) {
            	MapEditor.getInstance().actionHandler.clearMap();
            	showPanel(null, false);
            }
        };
        
		Table tblClearMapInner = new Table();
		tblClearMapInner.defaults().minSize(1f).height(bsize);
		tblClearMapInner.add(ComponentFactory.createButton("Ok", clClearMap));
		tblClearMapInner.add(ComponentFactory.createButton("Cancel", clHidePanel));
		
		tblClearMap.add("Clear map?");
		tblClearMap.row();
		tblClearMap.add(tblClearMapInner).fill();
		
		//Change owner table
		tblChangeOwner    = new Table(Assets.SKIN);

		Table tblChangeOwnerInner = new Table();
		tblChangeOwnerInner.defaults().minSize(1).height(bsize);
		tblChangeOwnerInner.add(changeOwner0 = ComponentFactory.createButton("blue", getChangeOwnerClickListener("blue")));
		tblChangeOwnerInner.add(changeOwner1 = ComponentFactory.createButton("red", getChangeOwnerClickListener("red")));
		tblChangeOwnerInner.add(changeOwnerNone = ComponentFactory.createButton("none", getChangeOwnerClickListener("none")));
		tblChangeOwnerInner.add(ComponentFactory.createButton("Cancel", clHidePanel));
		
		tblChangeOwner.add("Select new owner");
		tblChangeOwner.row();
		tblChangeOwner.add(tblChangeOwnerInner);
		
		
		//Save map table	-- covered by keyboard, need to fix before using
//		final Table tblSaveMap = new Table(Assets.SKIN);
//		final TextField saveMapTextfield = ComponentFactory.createTextField("Map name", false);
//		Button saveMapOKButton = ComponentFactory.createButton("Ok", new ClickListener() {
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				String mapName = saveMapTextfield.getText();
//				if(mapName.length() > 0){
//					MapEditor.getInstance().saveMap(mapName);
//					showPanel(null, false);
//					saveMapTextfield.setText("");
//				}
//			}
//		});
//		tblSaveMap.defaults().minSize(1);
//		tblSaveMap.center().add("Save map").colspan(2);
//		tblSaveMap.row();
//		tblSaveMap.left().add(saveMapTextfield).fillX().expandX().height(bsize);
//		tblSaveMap.right().add(saveMapOKButton).width(bsize*2).height(bsize);
//		tblSaveMap.debug();
		
		
		
		/**
		 * PERMANENT BUTTONS
		 */
		permanentTable = new Table(Assets.SKIN);
		permanentTable.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("BottomLargePanel"))));
		
        ttImg = new Image(Assets.getTextureRegion("terrain-button"));
		terrainToggle = new Button(ttImg, Assets.SKIN.get("image", ButtonStyle.class));
		terrainToggle.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	showPanel(terrainTable,terrainTable.getParent()==null);
            }
        } );
		
		btImg = new Image(Assets.getTextureRegion("building-button"));
		buildingsToggle = new Button(btImg, Assets.SKIN.get("image", ButtonStyle.class));
		buildingsToggle.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	showPanel(buildingsTable,buildingsTable.getParent()==null);
            }
        } );
		
		clearButton = ComponentFactory.createButton(Assets.getTextureRegion("cancel"),
				new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	showPanel(tblClearMap,tblClearMap.getParent()==null);
            }
        });
		
		loadButton = ComponentFactory.createButton(Assets.getTextureRegion("open-button"), "image",
				new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
            public void clicked(InputEvent event, float x, float y) {
            	//showPanel(tblSaveMap,tblSaveMap.getParent()==null);
            	popupSaveMap.show();
            }
        });
		
		newButton = ComponentFactory.createButton("NEW", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	popupNewMap.show();
            }
        });
		
		addActor(permanentTable);
		
		
		/**
		 * The "extra" panel that appears on top of the permanent one
		 */
		panelTable = new Table();
		panelTable.center();
		panelTable.setBackground(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("BottomLargePanel"))));
		panelTable.setVisible(false);
		addActor(panelTable);
		
		
		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		/**
		 * POPUPS
		 */
		popupSaveMap = new Popup("Save map", ComponentFactory.createTextField("Map name", false));

		final TextField saveMapTextfield = ComponentFactory.createTextField("Map name", false);
		saveMapTextfield.setHeight(Main.percentHeight*10);
		Button saveMapOKButton = ComponentFactory.createButton("Ok", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String mapName = saveMapTextfield.getText();
				if(mapName.length() > 0){
					MapEditor.getInstance().saveMap(mapName);
					Popup.getCurrentPopup().clearInputs();
					Popup.getCurrentPopup().close();
				}
			}
		});
		
		popupSaveMap.setComponents(
				saveMapTextfield,
				new Row(saveMapOKButton, ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER)));

		popupNewMap = new NewMapPopup();
		
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
		/*
		final SnapScrollPane scroll1 = new SnapScrollPane(table1, labels1);
		final SnapScrollPane scroll2 = new SnapScrollPane(table2, labels2);

		Table flickTable1 = new Table();
		flickTable1.add(scroll1).fill().expand();
		flickTable1.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		Table flickTable2 = new Table();
		flickTable2.add(scroll2).fill().expand();
		flickTable2.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
			
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
			public void clicked(InputEvent event, float x, float y) {
				int mapWidth = Map.MIN_SIZE + (int)(scroll1.getScrollPercentY()*(Map.MAX_SIZE-Map.MIN_SIZE));
    			int mapHeight = Map.MIN_SIZE + (int)(scroll2.getScrollPercentY()*(Map.MAX_SIZE-Map.MIN_SIZE));
    			MapEditor.getInstance().actionHandler.createNewMap(mapWidth, mapHeight);
				Popup.getCurrentPopup().close();
			}
		});
		popupNewMap = new Popup("New map");
		Button newMapOKButton = ComponentFactory.createButton("Ok");
		Table flickTable = new Table();
//		final ScrollPane scroll = new ScrollPane(table1);
//		scroll.setFlickScroll(true);
//		scroll.addListener(new InputListener() {
//		        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//		                System.out.println("down");
//		                return true;
//		        }
//		        
//		        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//		                System.out.println("up");
//		                scroll.get
//		        }
//			});
//		flickTable.add(scroll).height(MenuScreen.BUTTON_HEIGHT).width(MenuScreen.BUTTON_WIDTH);
		ArrayList<String> sizes = new ArrayList<String>();
		for(int i =Map.MIN_SIZE; i<=Map.MAX_SIZE; i++)
			sizes.add(String.valueOf(i));

		SelectBoxStyle sbst = new SelectBoxStyle (Assets.UI_FONT, Color.WHITE,
				new NinePatchDrawable(Assets.SKIN.getPatch("single-border")), 
				new NinePatchDrawable(Assets.SKIN.getPatch("double-border")),
				new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		SelectBox box = new SelectBox(sizes.toArray(new String[0]),sbst);
		//box.s

		flickTable.add(box);//.width(Main.percentWidth*30).height(Main.percentHeight*10);
		
		
		popupNewMap.setComponents(flickTable,
		        new Row(newMapOKButton, ComponentFactory.createButton("Cancel", Popup.POPUP_CLOSE_BUTTON_HANDLER)));
		        */
	}
	
	/**
	 * Updates 'size' variable, based on screen width.
	 * @param width
	 */
	private void updateSize(float width) {
		size = width / 6f;
	}

	/**
	 * Shows the change owner panel.
	 */
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
		panelTable.setVisible(show);
		if(show && contents!=null){
			panelTable.add(contents).fill().expand().pad(Main.percentHeight);
			panelTable.setHeight(contents.getPrefHeight());
		}
		this.unfocusAll();
	}
	
	/**
	 * @return The height of the UI
	 */
	public float getHeight(){
		return ( (panelTable.isVisible()) ? (panelTable.getY()+panelTable.getHeight()) : permanentTable.getHeight() );
	}
	
 	@Override
	public void resize(float width, float height){
		super.resize(width, height);
		updateSize(width);
		
		permanentTable.setHeight(size*1.05f);
		permanentTable.left().bottom();
		permanentTable.defaults().size(size);
		permanentTable.add(clearButton);
		permanentTable.add(terrainToggle);
		permanentTable.add(buildingsToggle);
		permanentTable.add(loadButton);
		permanentTable.add(saveButton);
		permanentTable.add(newButton).height(size/1.2f);
		permanentTable.pack();
		permanentTable.setBounds(0, 0, width, size*1.05f);

		panelTable.setBounds(0, permanentTable.getHeight() - 0.05f*size, width, permanentTable.getHeight());
	}
 	
 	/**
 	 * Sets image of selected terrain/building on respective toggle, resets the other and closes menu.
 	 * @param isTerrain Did the user click on a terrain button?
 	 */
 	public void changeSelectionType(boolean isTerrain){
 		float bsize = size / 1.2f;
 		if(isTerrain){
	   		ttImg.setDrawable(new TextureRegionDrawable(GTile.getTextures((TerrainType)MapEditor.getInstance().actionHandler.selected)[0]));
	   		btImg.setDrawable(new TextureRegionDrawable(Assets.getTextureRegion("building-button")));
	   		terrainToggle.size(bsize, bsize);
	   		buildingsToggle.size(size, size);
 		}
 		else{
       		btImg.setDrawable(new TextureRegionDrawable(GBuilding.getTextureRegion((Integer)MapEditor.getInstance().actionHandler.selected,0)));
       		ttImg.setDrawable(new TextureRegionDrawable(Assets.getTextureRegion("terrain-button")));
	   		terrainToggle.size(size, size);
	   		buildingsToggle.size(bsize, bsize);
 		}
 		showPanel(null,false);
 		this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
 	}
	
	/**
	 * Did the tap hit the UI? 
	 */
	public boolean tap(float x, float y, int count, int button){
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
            public void clicked(InputEvent event, float x, float y) {
            	MapEditor.getInstance().actionHandler.changeOwner(owner);
            	showPanel(null,false);
            }
        };
	}
}
