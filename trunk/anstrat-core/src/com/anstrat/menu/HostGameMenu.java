package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.MapList;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class HostGameMenu extends MenuScreen  {
	private static HostGameMenu me;
	
	private MapList mapList;
	
	private HostGameMenu(){
        
		Table settings = new Table(Assets.SKIN);
		settings.setBackground(Assets.SKIN.getPatch("single-border"));
		
		final TextField name = ComponentFactory.createTextField("Game name", false);
		final TextField password = ComponentFactory.createTextField("Game password", true);
		//TextField timeLimit = ComponentFactory.createTextField("Time limit (in minutes)", false);
		
		settings.defaults().height((int)(Main.percentHeight*10));
		settings.add("Name:");
		settings.add(name).fillX().expandX();
		settings.row();
		settings.add("Password:");
		settings.add(password).fillX().expandX();
		//settings.add("Time limit:");
		//settings.add(timeLimit).fillX().expandX();
		        
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
		    public void click(Actor actor,float x,float y ){
				//TODO put back in whne wrko
				/*
				long time = 0;
				//timeLimit not filled in -> no limit, time=0
				if(timeLimit.getText().length()!=0){
		    		try{
		    			time = Long.parseLong(timeLimit.getText());
		    			if(time < 0)
		    				throw new Exception();
		    		}
		    		catch (Exception e){
		    			Popup.showGenericPopup("Error", "'"+timeLimit.getText()+"' is not a valid time limit.");
		    			return;
		    		}
		    		//Time to minutes.
		    		time *= 60000;
				}
				*/
				
				Map map = null;
				String mapName = mapList.getSelected();
				if(mapName == null) mapName = "random";
				
				// Start a new game with the map, or a random map if map is null
				if(!mapName.equalsIgnoreCase("RANDOM")){
					// TODO insert correct value
					map = Assets.loadMap(mapName);
					//Main.getInstance().network.hostCustomGame(604800000l, name.getText(), password.getText(), map);
				}
				
				if (map == null || map.name.equalsIgnoreCase("RANDOM")) {
					int width = mapList.randWidth;
		    		int height = mapList.randHeight;
		    		
		    		if(width < Map.MIN_SIZE)
		    		{
		    			Popup.showGenericPopup("Error", "Map width must be at least "+Map.MIN_SIZE+".");
		    		}
		    		else if(width > Map.MAX_SIZE)
		    		{
		    			Popup.showGenericPopup("Error", "Map width must not be above "+Map.MAX_SIZE+".");
		    		}
		    		else if(height < Map.MIN_SIZE)
		    		{
		    			Popup.showGenericPopup("Error", "Map height must be at least "+Map.MIN_SIZE+".");
		    		}
		    		else if(height > Map.MAX_SIZE)
		    		{
		    			Popup.showGenericPopup("Error", "Map height must not be above "+Map.MAX_SIZE+".");
		    		}
		    		else
		    		{
		    			//TODO insert right value frmo box
		    			Gdx.app.log("Main", "Using random map");
		    			//Main.getInstance().network.hostGameRandom(width, height, 604800000l, name.getText(), password.getText());
		    		}
				}
		   }
		} );
		
		Label login = ComponentFactory.createLoginLabel();
		
		mapList = new MapList(goButton);
		mapList.setMaps(true, Assets.getMapList(true, true));
		
		contents.padTop((int) (3*Main.percentHeight)).center();
		contents.defaults().space((int)Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(settings);
		contents.row();
		Table centerMap = new Table(Assets.SKIN);
		centerMap.add("Map");
		contents.add(centerMap).height((int)(Main.percentHeight*4));
		contents.row();
		contents.add(mapList).fillY().expandY();
		contents.row();
		
		Table contentsInner = new Table();
		contentsInner.defaults().height(BUTTON_HEIGHT).width(BUTTON_WIDTH);
		contentsInner.add(goButton).padBottom((int) (BUTTON_HEIGHT*1.3));
		contentsInner.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(login);
		contentsInner.add(centerLogin);
		contents.add(contentsInner);
	}
	
	public static synchronized HostGameMenu getInstance() {
		if(me == null){
			me = new HostGameMenu();
		}
		return me;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
