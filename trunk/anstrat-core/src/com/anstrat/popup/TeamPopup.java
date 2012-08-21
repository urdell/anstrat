package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gui.GUnit;
import com.anstrat.guiComponent.ColorTable;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TeamPopup extends Popup{

	public static final String BUY_TEXT = "Buy";
	public static final String CANCEL_TEXT = "Cancel";
	public static final Color  COLOR_UNAVAILABLE = Color.DARK_GRAY;
	
	public static final int GOD_ODIN = 0;
	public static final int GOD_THOR = 1;
	public static final int GOD_LOKI = 2;
	public static final int GOD_HEL = 3;
	
	public static final int TEAM_VV = 0;
	public static final int TEAM_DD = 1;
	
	private Button okButton;
	private Button[] gods;
	private Label god, team;
	private Button[] teams;
	private NinePatch[] godSilhouettes;
	private NinePatch[] teamSilhouettes;
	private ColorTable godTable;
	private ColorTable teamTable;
	
	public static int selectedGod = 0;
	public static int selectedTeam = 0;
	
	public TeamPopup(String title) {
		super(title);
		
		okButton = ComponentFactory.createButton("Ok!", Popup.POPUP_CLOSE_BUTTON_HANDLER);
		
		gods = new Button[4];
		teams = new Button[2];
		godSilhouettes = new NinePatch[4];
		teamSilhouettes = new NinePatch[2];
		
		for(int i=0; i<godSilhouettes.length; i++){
			godSilhouettes[i] = new NinePatch(GUnit.getTextureRegion(UnitType.BERSERKER));
			gods[i] = new Button(new Image(godSilhouettes[i]), Assets.SKIN.getStyle("image",ButtonStyle.class));
			gods[i].setClickListener(new ClickListener() {
				@Override
			    public void click(Actor actor,float x,float y ){
			        selectButton((Button)actor);
			    }
			});
		}
		
		teamSilhouettes[0] = new NinePatch(GUnit.getTextureRegion(UnitType.SWORD));
		teamSilhouettes[1] = new NinePatch(GUnit.getTextureRegion(UnitType.JOTUN));
		for(int i=0; i<teamSilhouettes.length; i++){
			teams[i] = new Button(new Image(teamSilhouettes[i]), Assets.SKIN.getStyle("image",ButtonStyle.class));
			teams[i].setClickListener(new ClickListener() {
				@Override
			    public void click(Actor actor,float x,float y ){
			        selectButton((Button)actor);
			    }
			});
		}
		
		int unitWidth = (int)(Main.percentWidth*100/6*1.3);
		int pad = (int)(-unitWidth*0.15);
		
		// The silhouettes of the purchasable units
		godTable = new ColorTable(Color.BLUE);
		teamTable = new ColorTable(Color.BLUE);
		NinePatch tableBackgroundPatch = Assets.SKIN.getPatch("border-thick-updown");
		godTable.setBackground(tableBackgroundPatch);
		teamTable.setBackground(tableBackgroundPatch);
		godTable.defaults().size(unitWidth).padLeft(pad).padRight(pad);
		teamTable.defaults().size(unitWidth).padLeft(pad).padRight(pad);
		
		godTable.add(gods[0]);
		godTable.add(gods[1]);
		godTable.add(gods[2]);
		godTable.add(gods[3]);
		
		teamTable.add(teams[0]);
		teamTable.add(teams[1]);
		
		god = new Label(Assets.SKIN);
		team = new Label(Assets.SKIN);
		
		//this.setBackground(Assets.SKIN.getPatch("empty")); // Overrides the default background with an empty one
		this.add(godTable).width(Gdx.graphics.getWidth()).padTop((int)(-tableBackgroundPatch.getTopHeight()/3));
		this.row();
		this.add(god).expandX().fillX();
		this.row();
		this.add(teamTable).width(Gdx.graphics.getWidth()).padTop((int)(-tableBackgroundPatch.getTopHeight()/3));
		this.row();
		this.add(team).expandX().fillX();
		this.row();
		this.add().expand().uniform();
		this.row();
		this.add(okButton).width(Gdx.graphics.getWidth()).expandY().bottom();
		
		selectButton(gods[0]);
		selectButton(teams[0]);
		
		
	}
	
	private void selectButton(Button actor) {
		for(int i = 0; i < gods.length; i++) {
			if (gods[i].equals(actor)) {
				selectedGod = i;
				god.setText(getGodName(i));
				return;
			}
		}
		
		for (int i = 0; i < teams.length; i++) {
			if (teams[i].equals(actor)) {
				selectedTeam = i;
				team.setText(getTeamName(i));
				return;
			}
		}
		
	}
	
	private String getGodName(int god) {
		if(god == GOD_ODIN)
			return "Odin";
		if(god == GOD_THOR)
			return "Thor";
		if(god == GOD_LOKI)
			return "Loki";
		if(god == GOD_HEL)
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
	public void resize(int width, int height){
		overlay.setSize(width, height);
		this.width = width;
		this.height = height;
		this.x = this.y = 0;
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		super.draw(batch, parentAlpha);
	}
	
	
}
