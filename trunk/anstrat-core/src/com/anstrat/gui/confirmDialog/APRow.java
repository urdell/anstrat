package com.anstrat.gui.confirmDialog;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.APPieDisplay;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class APRow extends ConfirmRow{
	
	private int currentAP, maxAP, apReg, nextAttackCost, spentAP;

	public APRow(Unit unit, int spentAP){
		this.currentAP = unit.currentAP;
		this.maxAP = unit.getMaxAP();
		this.apReg = unit.getAPReg();
		this.nextAttackCost = unit.getAPCostAttack();
		this.spentAP = spentAP;
	}
	
	
	@Override
	public void draw(float x, float y, SpriteBatch batch) {
		APPieDisplay.draw(x+ROW_HEIGHT*0.5f, y, ROW_HEIGHT, currentAP, maxAP, apReg, nextAttackCost, batch, true);
		TextureRegion arrow = Assets.getTextureRegion("rightArrow");
		batch.draw(arrow, x+ROW_HEIGHT*1.5f, y, ROW_HEIGHT, ROW_HEIGHT);
		APPieDisplay.draw(x+ROW_WIDTH-ROW_HEIGHT*1.5f, y, ROW_HEIGHT, currentAP-spentAP, maxAP, apReg, nextAttackCost, batch, true);
		
	}

	
	
	
}
