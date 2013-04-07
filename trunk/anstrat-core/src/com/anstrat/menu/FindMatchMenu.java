package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.popup.Popup;
import com.anstrat.popup.TeamPopup;
import com.anstrat.popup.TeamPopup.TeamPopupListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class FindMatchMenu extends MenuScreen {
	private static FindMatchMenu me;
	
	
	private int god = PlayerAbilityType.GOD_ODIN, team = TeamPopup.TEAM_VV;
	private MapSelecter mapSelecter;
	
	private FindMatchMenu(){
		/*
		Table settings = new Table(Assets.SKIN);
		settings.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		mapSelecter = new MapSelecter(true, false);
		
		final CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
		
		Button godButton = ComponentFactory.createButton("Team", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Popup popup = new TeamPopup(team, "Select your team", new TeamPopupListener() {
					@Override
					public void onChosen(int teamChosen) {
						System.out.println("hej findmatchmenu someting");
						team = teamChosen;
					}
				});
				
				popup.show();
			}
		});
		
		settings.add("Find Match");
		settings.row();
		settings.add(mapSelecter).fillX().expandX();
		settings.row();
		settings.add(fog).fillX().expandX();
		settings.row();
		settings.add(godButton).height(BUTTON_HEIGHT).fillX().expandX();
		        
		TextButton goButton = ComponentFactory.createMenuButton( "GO!",new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Main.getInstance().network.findRandomGame(new GameOptions(
						god, 
						team, 
						fog.isChecked(),
						mapSelecter.getMapSelection(), 
						mapSelecter.getMapName(), 
						null ));
				new GameOptions(map, mapType, god, team, fog)
			}
		});
		
		contents.padTop(3f*Main.percentHeight).center();
		contents.defaults().space(Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(settings);
		contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom(BUTTON_HEIGHT*1.3f);
		contents.row();
		contents.add().expandY();
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(ComponentFactory.createLoginLabel());
		contents.add(centerLogin);
		*/
		contents.add("Redo with same style as hotseat menu");
	}
	
	public static synchronized FindMatchMenu getInstance() {
		if(me == null){
			me = new FindMatchMenu();
		}
		return me;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}
