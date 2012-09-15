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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

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
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("double-border")));
		
		this.type = type;
		name = new Label(type.name, Assets.SKIN);
		cost = new Label(String.valueOf(type.manaCost), Assets.SKIN);
		description = new Label(type.description, new LabelStyle(new BitmapFont(),Color.WHITE));
		description.setWrap(true);
		//TODO 
		image = new Image();
		//statDisplay = new ValueDisplay(ValueDisplay.VALUE_UNIT_ATTACK)
		Image costIcon = new Image(Assets.getTextureRegion("mana"));
		
		float imageHeight = Gdx.graphics.getHeight()/5f;
		float imageWidth = Gdx.graphics.getWidth()/3f;
		
		this.defaults().top();
		this.add(name).center().padTop(-3f*Main.percentHeight).padBottom(-2f*Main.percentHeight);
		this.row();
		this.add(image).left().width(imageWidth).height(imageHeight);
		this.row();
		this.add(description).fill().expand();
		this.row();
		
		Table inner = new Table();
		inner.defaults().left();
		inner.add(costIcon).height(3f*Main.percentHeight).width(6f*Main.percentWidth);
		inner.add(cost);
		
		this.add(inner).left().height(3f*Main.percentHeight);
	}
	
	public void setSize(float width, float height){
		this.setWidth(width);
		this.setHeight(height);
		description.setWidth(width);
		description.layout();
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		batch.draw(background, getX()+2*Main.percentWidth, getY()+Main.percentHeight, getWidth()-4*Main.percentWidth, getHeight()-2*Main.percentHeight);
		super.draw(batch, parentAlpha);
	}

	@Override
	public Actor hit(float x, float y, boolean touchable) {
		return x > 0 && x < getWidth() && y > 0 && y < getHeight() ? this : null;
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
