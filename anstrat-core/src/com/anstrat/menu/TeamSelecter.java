package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.popup.TeamPopup;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TeamSelecter extends Table {
	private int team;
	
	public TeamSelecter(){	
		
		final CheckBox teamGood = new CheckBox("", createCheckboxStyle("teamgood-on", "teamgood-off"));
		final CheckBox teamBad = new CheckBox("", createCheckboxStyle("teambad-on", "teambad-off"));
		
		//Image vv =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.SWORD)));
		//Image dd =  new Image(new NinePatch(GUnit.getTextureRegion(UnitType.JOTUN)));
		
		//vv.setScaleX(0.6f);
		//dd.setScaleX(0.6f);
		
		teamGood.setChecked(true);
		team = TeamPopup.TEAM_VV;
		
		teamBad.setChecked(false);
		
		teamGood.addListener(new ClickListener() {
			 @Override
	         public void clicked(InputEvent event, float x, float y) {
	         	teamGood.setChecked(false);
	         	teamBad.setChecked(true);
	         	team = TeamPopup.TEAM_VV;
	         }
		});
		
		teamBad.addListener(new ClickListener() {
			 @Override
	         public void clicked(InputEvent event, float x, float y) {
				teamBad.setChecked(false);
	         	teamGood.setChecked(true);
	         	team = TeamPopup.TEAM_DD;
	         }
		});
		
		add(teamGood);
		add(teamBad);
	}
	
	public int getTeam(){
		return team;
	}
	
	private static CheckBoxStyle createCheckboxStyle(String on, String off){
		return new CheckBoxStyle(
				new TextureRegionDrawable(Assets.getTextureRegion(on)),
				new TextureRegionDrawable(Assets.getTextureRegion(off)),
				Assets.MENU_FONT,
				Color.WHITE);
	}
}
