package com.anstrat.menu;

import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.TeamPopup;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TeamSelecter extends Table {
	private int team;
	
	public TeamSelecter(){	
		final CheckBox teamVV = ComponentFactory.createCheckBox("");;
		final CheckBox teamDD = ComponentFactory.createCheckBox("");
		
		Image vv =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.SWORD)));
		Image dd =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.JOTUN)));
		
		vv.setScaleX(0.6f);
		dd.setScaleX(0.6f);
		
		teamVV.setChecked(true);
		team = TeamPopup.TEAM_VV;
		
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
		
		add(teamVV);
		add(vv);
		add(teamDD);
		add(dd);
	}
	
	public int getTeam(){
		return team;
	}
}
