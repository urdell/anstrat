package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class PlayerSelecter extends Table {
	private TeamSelecter teamTable;
	
	/** TODO: Add god selecter **/
	private int god = 0;
	
	private TextField playerNameButton;
	
	/** Player selecter without name */
	public PlayerSelecter(){
		this(false, null);
	}
	
	/** Player selection with name **/
	public PlayerSelecter(String defaultName){
		this(true, defaultName);
	}
	
	private PlayerSelecter(boolean nameSelection, String defaultName){
		setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		
		playerNameButton = ComponentFactory.createTextField(defaultName, false);
		teamTable = new TeamSelecter(); 
				
		defaults().height(Main.percentHeight*8f);
		
		if(nameSelection){
			add(playerNameButton).fillX().expandX();
			row();
		}
		
		add(teamTable).fillX().expandX();
	}
	
	public String getPlayerName(){
		return playerNameButton.getText();
	}
	
	public int getGod(){
		return god;
	}
	
	public int getTeam(){
		return teamTable.getTeam();
	}
}
