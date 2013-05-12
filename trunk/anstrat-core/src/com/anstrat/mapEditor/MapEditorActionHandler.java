package com.anstrat.mapEditor;


import com.anstrat.core.Assets;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.geography.Map;
import com.anstrat.geography.TerrainType;
import com.anstrat.gui.GBuilding;
import com.anstrat.gui.GTile;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MapEditorActionHandler {
	
	public Object selected = null;
	public GTile selectedTile = null;
	
	/**
	 * Select building or terrain.
	 */
	public void select(Object object){
		if(object instanceof Integer || object instanceof TerrainType || object == null){
			selected = object;
			if(object!=null)
				MapEditor.getInstance().userInterface.changeSelectionType(object instanceof TerrainType);
		}
		else
			Gdx.app.error("Map Editor", String.format("ERROR: Tried to select object which is neither an Integer nor a TerrainType."));
	}

	/**
	 * Change the tile with new terrain
	 * @param tile the tile to be changed
	 * @param terrain the terrain that the tile should have
	 */
	private void changeTile(GTile gTile, TerrainType terrain) {
		gTile.tile.terrain = terrain;
		gTile.tile.coordinates = gTile.tile.coordinates; 
		gTile.setTexture(terrain);
	}
	
	/**
	 * Call this when a tile is clicked
	 * @param gTile
	 */
	public void click(GTile gTile) {
		MapEditor mapEd = MapEditor.getInstance(); 
		selectedTile = null;
		
		if(selected == null && gTile!=null)
			changeOwnerPopup(gTile);
		// Terrain
		else if (selected instanceof TerrainType) {
			TerrainType type = (TerrainType) selected;
			// remove existing building
			Building existing = mapEd.map.getBuildingByTile(gTile.tile.coordinates);
			if(existing != null){
				mapEd.gBuildings.remove(existing.id);
				mapEd.map.setBuilding(existing.tileCoordinate, null);
			}
			
			if (!(gTile.tile.terrain == type)) {
				changeTile(gTile, type);
			}
		}
		// Building
		else if (selected instanceof Integer) {
			Integer type = (Integer) selected;
			
			Building existing = mapEd.map.getBuildingByTile(gTile.tile.coordinates);
			if(existing != null){
				// Change owner if we try to place identical building on tile
				if(type==existing.type){
					changeOwnerPopup(gTile);
					return;
				}
				
				// Remove old building
				mapEd.gBuildings.remove(existing.id);
			}
			
			
			// Handle castle
			int controller = -1;
			if (type == Building.TYPE_CASTLE) {
				if (mapEd.nextPlayerToRecieveCastle == Player.PLAYER_1_ID) {
					controller = Player.PLAYER_1_ID;
					mapEd.nextPlayerToRecieveCastle = Player.PLAYER_2_ID;
				}	
				else if (mapEd.nextPlayerToRecieveCastle == Player.PLAYER_2_ID) {
					controller = Player.PLAYER_2_ID;
					mapEd.nextPlayerToRecieveCastle = Player.PLAYER_1_ID;
				}
				else 
					return;
				if (mapEd.map.getPlayersCastle(controller) != null && 
						mapEd.map.buildingList.containsKey(mapEd.map.getPlayersCastle(controller).id)) {
					GTile tile = mapEd.gMap.getTile(mapEd.map.getPlayersCastle(controller).tileCoordinate);
					tile.tile.terrain = TerrainType.FIELD;
					tile.setTexture(TerrainType.FIELD);
					mapEd.gBuildings.remove(mapEd.map.getPlayersCastle(controller).id);
					mapEd.map.buildingList.remove(mapEd.map.getPlayersCastle(controller).id);
				}
			}
				
			int id = mapEd.map.nextBuildingId++;
			Building b = new Building(type, id, controller);
				
			mapEd.map.setBuilding(gTile.tile.coordinates, b);
			mapEd.gBuildings.put(b.id, new GBuilding(b,mapEd.gMap));
		}
	}
	
	/**
	 * Shows panel for changing owner of the building occupying specified tile.
	 */
	private void changeOwnerPopup(GTile tile){
		Building building = MapEditor.getInstance().map.getBuildingByTile(tile.tile.coordinates);
		if(building!=null){
			selectedTile = tile;
			MapEditorUI ui = MapEditor.getInstance().userInterface;
			int owner = building.controllerId;
			
			ui.changeOwner0.setText(owner==0?("[ "+0+" ]"):String.valueOf(0));
			ui.changeOwner1.setText(owner==1?("[ "+1+" ]"):String.valueOf(1));
			
			TextButton none = ui.changeOwnerNone;
			none.setText(owner==-1?"[ none ]":"none");
			Assets.SKIN.setEnabled(none, building.type!=Building.TYPE_CASTLE);
			ui.showChangeOwner();
		}
	}
	
	/**
	 * Changes owner of a building. 
	 */
	public void changeOwner(String newOwner){
		Map map = MapEditor.getInstance().map;
		Building b = map.getBuildingByTile(selectedTile.tile.coordinates);
		
		if(newOwner.equals("none") && b.type == Building.TYPE_CASTLE){
			return;
		}
		
		int contr = newOwner.equals("none") ? -1 : Integer.parseInt(newOwner);
		
		// make sure both castles don't have the same owner by switching
		if(b.type == Building.TYPE_CASTLE && map.getPlayersCastle(contr) !=null && map.buildingList.containsKey(map.getPlayersCastle(contr).id)){
			map.getPlayersCastle(contr).controllerId = contr==1?0:1;
		}
		
		b.controllerId = contr;
	}
	
	public void createNewMap(int width, int height){
		
		String widthErrorMsg = getErrorMessage(width, "width");
		String heightErrorMsg = getErrorMessage(height, "height");
		
		if(widthErrorMsg == null && heightErrorMsg == null){
			// Create map
	    	MapEditor.getInstance().initMap(new Map(width, height));
		}
		else{
			Popup.showGenericPopup("Invalid size", widthErrorMsg != null ? widthErrorMsg : heightErrorMsg);
		}
	}
	
	private String getErrorMessage(int value, String name){
		if(value < Map.MIN_SIZE) return String.format("Map %s cannot be less than %d.", name, Map.MIN_SIZE);
		if(value > Map.MAX_SIZE) return String.format("Map %s cannot be larger than %d.", name, Map.MAX_SIZE);
		return null;
	}
	
	/**
	 * Clears map
	 */
	public void clearMap(){
		MapEditor maped = MapEditor.getInstance();
    	maped.initMap(new Map(maped.map.getXSize(),maped.map.getYSize()));
	}
}
