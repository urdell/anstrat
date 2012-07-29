package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.TableLayout;

public class AbilityTypeCard extends Table {
	private Texture background;
	private Label name, cost, description;
	private Image image;
	public PlayerAbilityType type;
	
	public AbilityTypeCard(PlayerAbilityType type){
		super();
		
		// Create single colored background
		Pixmap p = new Pixmap(8, 8, Format.RGB565);
		p.setColor(new Color(0f, 0f, 0.8f, 1f));
		p.fill();
		background = new Texture(p);
		//Border
		this.setBackground(Assets.SKIN.getPatch("double-border"));
		
		this.type = type;
		name = new Label(type.name, Assets.SKIN);
		cost = new Label(String.valueOf(type.manaCost), Assets.SKIN);
		description = new Label(type.description, new LabelStyle(new BitmapFont(),Color.WHITE));
		description.setWrap(true);
		//TODO 
		image = new Image();
		//statDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_ATTACK)
		Image costIcon = new Image(Assets.getTextureRegion("mana"));
		
		TableLayout layout = this.getTableLayout();
		
		int imageHeight = Gdx.graphics.getHeight()/5;
		int imageWidth = Gdx.graphics.getWidth()/3;
		
		layout.defaults().top();
		layout.add(name).center().padTop((int)(-3*Main.percentHeight)).padBottom((int)(-2*Main.percentHeight));
		layout.row();
		layout.add(image).left().width(imageWidth).height(imageHeight);
		layout.row();
		layout.add(description).fill().expand();
		layout.row();
		
		Table inner = new Table();
		inner.defaults().left();
		inner.add(costIcon).height((int)(3*Main.percentHeight)).width((int)(6*Main.percentWidth));
		inner.add(cost);
		
		layout.add(inner).left().height((int)(3*Main.percentHeight));
	}
	
	public void setSize(float width, float height){
		this.width = width;
		this.height = height;
		description.width = width;
		description.layout();
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		batch.draw(background, x+2*Main.percentWidth, y+Main.percentHeight, width-4*Main.percentWidth, height-2*Main.percentHeight);
		super.draw(batch, parentAlpha);
	}

	@Override
	public Actor hit(float x, float y) {
		return x > 0 && x < width && y > 0 && y < height ? this : null;
	}
	
	public void setDisabled(boolean disabled){
		if(disabled)
			cost.setColor(Color.RED);
		else
			cost.setColor(Color.WHITE);
	}
	
	public void setType(PlayerAbilityType type) {
		this.type = type;
		name.setText(type.name);
		cost.setText(String.valueOf(type.manaCost));
		description.setText(type.description);
		//TODO image.setRegion(something something);
	}
}
