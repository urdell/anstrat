package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class CreditPopup extends Popup {

	public CreditPopup() {
		
		this.setTitle("Credits");
		this.drawOverlay = false;
		this.add(new Label("Developers: \n Tomas ``Bad boy`` Urdell \n Johnny ``Douche`` Vaeyrynen \n Erik Termander \n Rickard Paulsson \n Andreas ``Scumbag`` Eklund \n Anton Groenlund \n Kalle Persson \n Mattias Karlsson"+ 
				"\n \n Thanks to: \n Mario Zechner & the libgdx team \n \n " + 
				"Fonts: \n \"Goudy Bookletter 1911\" by Barry Schwartz \n \"Crimson\" by Sebastian Kosch \n\"Astonished\" by Misprinted Type", new LabelStyle(Assets.DESCRIPTION_FONT, Color.WHITE)));

	}

	@Override
	public void resize(float width, float height){
		// Force popup to take up the whole window
		this.setSize(width, height);
		super.resize(width, height);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1f, 1f, 1f, 0.5f * parentAlpha);
		super.draw(batch, parentAlpha);
	}
	
	@Override 
	public void show(){
		super.show();
	}

	@Override public void close(){
		GEngine.getInstance().userInterface.setVisible(true);
		super.close();
	}

	public void update(){
		
	}
}
