package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.TeamPopup;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class PlayerSelecter extends Table {
	private int team = 0;
	
	/** TODO: Add god selecter **/
	private int god = 0;
	
	private TextField playerNameButton;
	
	public PlayerSelecter(String defaultName){
		setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		
		playerNameButton = ComponentFactory.createTextField(defaultName, false);
		Table teamTable = new Table();
		
		final CheckBox teamVV = ComponentFactory.createCheckBox("");;
		final CheckBox teamDD = ComponentFactory.createCheckBox("");
		
		Image vv =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.SWORD)));
		Image dd =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.JOTUN)));
		
		vv.setScaleX(0.6f);
		dd.setScaleX(0.6f);
		
		teamVV.setChecked(true);
		teamDD.setChecked(false);
		
		teamVV.addListener(new ClickListener() {
			 @Override
	         public void clicked(InputEvent event, float x, float y) {
	         	teamVV.setChecked(true);
	         	teamDD.setChecked(false);
	         	team = TeamPopup.TEAM_VV;
	         }
		});
		
		teamDD.addListener(new ClickListener() {
			 @Override
	         public void clicked(InputEvent event, float x, float y) {
	         	teamVV.setChecked(false);
	         	teamDD.setChecked(true);
	         	team = TeamPopup.TEAM_DD;
	         }
		});
		
		teamTable.add(teamVV);
		teamTable.add(vv);
		teamTable.add(teamDD);
		teamTable.add(dd);

		defaults().height(Main.percentHeight*8f);
		add(playerNameButton).fillX().expandX();
		row();
		add(teamTable).fillX().expandX();
	}
	
	public String getPlayerName(){
		return playerNameButton.getText();
	}
	
	public int getGod(){
		return god;
	}
	
	public int getTeam(){
		return team;
	}
}
