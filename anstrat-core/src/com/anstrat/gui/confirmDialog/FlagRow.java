package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.APPieDisplay;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FlagRow extends ConfirmRow {
	
	Color oldColor, newColor;
	
	
	public FlagRow(Building building, Unit unit){
		int oldId = building.controllerId;
		if(oldId == -1)
			oldColor = Color.GRAY;
		else
			oldColor = State.activeState.players[oldId].getColor();
		newColor = State.activeState.players[unit.ownerId].getColor();
	}

	@Override
	public void draw(float x, float y, SpriteBatch batch) {
		TextureRegion flag = Assets.getTextureRegion("flagIcon");
		TextureRegion arrow = Assets.getTextureRegion("arrowFat");
		batch.setColor(oldColor);
		batch.draw(flag, x+ROW_HEIGHT*0.5f, y, ROW_HEIGHT, ROW_HEIGHT);
		batch.setColor(Color.WHITE);
		batch.draw(arrow, x+ROW_HEIGHT*1.5f, y, ROW_HEIGHT, ROW_HEIGHT);
		batch.setColor(newColor);
		batch.draw(flag, x+ROW_HEIGHT*2.5f, y, ROW_HEIGHT, ROW_HEIGHT);
		batch.setColor(Color.WHITE);
		
	}

}
