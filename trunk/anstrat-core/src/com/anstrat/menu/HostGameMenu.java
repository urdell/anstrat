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
	
	private TextField name, password;//, timeLimit;
	private MapList list;
	
	
	private HostGameMenu() {
		super();
		
        contents.register( "topLabel", new Label("Host game:", Assets.SKIN) );
        
        Table settings = new Table(Assets.SKIN);
        settings.setBackground(Assets.SKIN.getPatch("single-border"));
        name = ComponentFactory.createTextField("Game name", false);
        settings.register("name", name);
        password = ComponentFactory.createTextField("Game password", true);
        settings.register("password", password);
        // TODO put back in when wR0Ks
        /*
        timeLimit = ComponentFactory.createTextField("Time limit (in minutes)", null, false);
        settingsLayout.register("limit", timeLimit);
        */
        settings.parse("* height:"+(int)(Main.percentHeight*10) +
				"'Name: '[name] fill:x expand:x" +
				"---" +
				"'Password: ' [password] fill:x expand:x"); /*+
				"---" +
				"'Time limit: ' [limit] fill:x expand:x");
				*/
        contents.register("settings", settings);
                
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
        		String mapName = list.getSelected();
        		
        		if (mapName == null) {
        			mapName = "random";
        		}
        		
        		// Start a new game with the map, or a random map if map is null
        		if(!mapName.equalsIgnoreCase("RANDOM"))
        		{
        			// TODO insert correct value
        			map = Assets.loadMap(mapName);
        			Main.getInstance().hostCustomGame(604800000l, name.getText(), password.getText(), map);
        		}
        		
        		if (map == null || map.name.equalsIgnoreCase("RANDOM")) {
        			int width = list.randWidth;
            		int height = list.randHeight;
            		
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
            			Main.getInstance().hostGameRandom(width, height, 604800000l, name.getText(), password.getText());
            		}
        		}
		   }
        } );
        
        contents.register("play", goButton);
        
        contents.register("login", ComponentFactory.createLoginLabel());
        
        list = new MapList(goButton);
        list.setMaps(true, Assets.getMapList(true, true));
        contents.register("maps", list);
        
        contents.padTop((int) (3*Main.percentHeight));
        contents.parse( "* spacing:"+(int)Main.percentWidth+" padding:0 align:top width:"+BUTTON_WIDTH+
    					"[settings]"+
    					"---" +
    					"{'Map'} align:center height:"+(int)(Main.percentHeight*4) +
    					"---" +
    					"[maps] fill:y expand:y" +
    					"---"+
    					"{* height:"+BUTTON_HEIGHT+" width:"+BUTTON_WIDTH+
    					"[play] paddingBottom:"+(int) (BUTTON_HEIGHT*1.3) +
    					"---"+
    					"{[login] align:center}}");
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
