package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.UnitType;
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
	private Label teamLabel;
	private Button[] teams;
	private NinePatch[] teamSilhouettes;
	private ColorTable teamTable;
	
	private  int selectedTeam = 0;
	
	private TeamPopupListener listener;
	
	public TeamPopup(int team, String title, TeamPopupListener teamPopupListener) {
		super(title);
		this.listener = teamPopupListener;
		okButton = ComponentFactory.createButton("Ok!", Popup.POPUP_CLOSE_BUTTON_HANDLER);
	
		teams = new Button[2];
		teamSilhouettes = new NinePatch[2];
		
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
		teamTable = new ColorTable(Color.BLUE);
		NinePatch tableBackgroundPatch = Assets.SKIN.getPatch("border-thick-updown");
		teamTable.setBackground(new NinePatchDrawable(tableBackgroundPatch));
		teamTable.defaults().size(unitWidth);
		
		teamTable.add(teams[0]);
		teamTable.add(teams[1]);
		
		teamLabel = new Label("",Assets.SKIN);
		
		//this.setBackground(Assets.SKIN.getPatch("empty")); // Overrides the default background with an empty one
		this.add(teamTable).padTop(-tableBackgroundPatch.getTopHeight()/3f);
		this.row();
		this.add(teamLabel);
		//this.add().expand().uniform();
		this.row();
		this.add(okButton).bottom().padTop(25);
		
		selectButton(teams[team]);
	}
	
	private void selectButton(Button actor) {
		
		for (int i = 0; i < teams.length; i++) {
			if (teams[i].equals(actor)) {
				selectedTeam = i;
				teamLabel.setText(getTeamName(i));
				listener.onChosen(selectedTeam);
				return;
			}
		}
		
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
		public void onChosen(int team);
	}
}
