package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ColorTable;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class TeamPopup extends Popup{

	public static final String BUY_TEXT = "Buy";
	public static final String CANCEL_TEXT = "Cancel";
	public static final Color  COLOR_UNAVAILABLE = Color.DARK_GRAY;
	
	public static final int TEAM_VV = 0;
	public static final int TEAM_DD = 1;
	
	private Button okButton;
	private Button[] gods;
	private Label godLabel, teamLabel;
	private Button[] teams;
	private NinePatch[] godSilhouettes;
	private NinePatch[] teamSilhouettes;
	private ColorTable godTable;
	private ColorTable teamTable;
	
	private  int selectedGod = 0;
	private  int selectedTeam = 0;
	
	private TeamPopupListener listener;
	
	public TeamPopup(int god, int team, String title, TeamPopupListener teamPopupListener) {
		super(title);
		this.listener = teamPopupListener;
		okButton = ComponentFactory.createButton("Ok!", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		gods = new Button[4];
		teams = new Button[2];
		godSilhouettes = new NinePatch[4];
		teamSilhouettes = new NinePatch[2];
		
		for(int i=0; i<godSilhouettes.length; i++){
			godSilhouettes[i] = new NinePatch(GUnit.getTextureRegion(UnitType.BERSERKER));
			gods[i] = new Button(new Image(godSilhouettes[i]), Assets.SKIN.get("image",ButtonStyle.class));
			gods[i].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					selectButton((Button)event.getListenerActor());
			    }
			});
		}
		
		teamSilhouettes[0] = new NinePatch(GUnit.getTextureRegion(UnitType.SWORD));
		teamSilhouettes[1] = new NinePatch(GUnit.getTextureRegion(UnitType.JOTUN));
		for(int i=0; i<teamSilhouettes.length; i++){
			teams[i] = new Button(new Image(teamSilhouettes[i]), Assets.SKIN.get("image",ButtonStyle.class));
			teams[i].addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					selectButton((Button)event.getListenerActor());
			    }
			});
		}
		
		int unitWidth = (int)(Main.percentWidth*100/6*1.3);
		
		// The silhouettes of the purchasable units
		godTable = new ColorTable(Color.BLUE);
		teamTable = new ColorTable(Color.BLUE);
		NinePatch tableBackgroundPatch = Assets.SKIN.getPatch("border-thick-updown");
		godTable.setBackground(new NinePatchDrawable(tableBackgroundPatch));
		teamTable.setBackground(new NinePatchDrawable(tableBackgroundPatch));
		godTable.defaults().size(unitWidth);
		teamTable.defaults().size(unitWidth);
		
		godTable.add(gods[0]);
		godTable.add(gods[1]);
		godTable.add(gods[2]);
		godTable.add(gods[3]);
		
		teamTable.add(teams[0]);
		teamTable.add(teams[1]);
		
		godLabel = new Label("",Assets.SKIN);
		teamLabel = new Label("",Assets.SKIN);
		
		//this.setBackground(Assets.SKIN.getPatch("empty")); // Overrides the default background with an empty one
		this.add(godTable).padTop(-tableBackgroundPatch.getTopHeight()/3f);
		this.row();
		this.add(godLabel);
		this.row();
		this.add(teamTable).padTop(-tableBackgroundPatch.getTopHeight()/3f);
		this.row();
		this.add(teamLabel);
		//this.add().expand().uniform();
		this.row();
		this.add(okButton).bottom().padTop(25);
		
		selectButton(gods[god]);
		selectButton(teams[team]);
	}
	
	private void selectButton(Button actor) {
		
		for(int i = 0; i < gods.length; i++) {
			if (gods[i].equals(actor)) {
				selectedGod = i;
				godLabel.setText(getGodName(i));
				listener.onChosen(selectedGod, selectedTeam);
				return;
			}
		}
		
		for (int i = 0; i < teams.length; i++) {
			if (teams[i].equals(actor)) {
				selectedTeam = i;
				teamLabel.setText(getTeamName(i));
				listener.onChosen(selectedGod, selectedTeam);
				return;
			}
		}
		
	}
	
	private String getGodName(int god) {
		if(god == PlayerAbilityType.GOD_ODIN)
			return "Odin";
		if(god == PlayerAbilityType.GOD_THOR)
			return "Thor";
		if(god == PlayerAbilityType.GOD_LOKI)
			return "Loki";
		if(god == PlayerAbilityType.GOD_HEL)
			return "Hel";
		
		return "";
	}
	
	private String getTeamName(int team) {
		if (team == TEAM_VV) 
			return "Vengeful Vikings";
		if (team == TEAM_DD) 
			return "Dreadful Douchebags";
		
		return "";
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		super.draw(batch, parentAlpha);
	}
	
	public static interface TeamPopupListener {
		public void onChosen(int god, int team);
	}
}
