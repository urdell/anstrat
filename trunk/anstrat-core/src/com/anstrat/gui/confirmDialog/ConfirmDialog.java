package com.anstrat.gui.confirmDialog;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.gameCore.Building;
import com.anstrat.gameCore.Combat;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.Path;
import com.anstrat.gui.GEngine;
import com.anstrat.guiComponent.ColorTable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class ConfirmDialog {

	public static final int TOP_RIGHT = 0;
	public static final int BOTTOM_RIGHT = 1;
	public static final int TOP_LEFT = 3;
	public static final int BOTTOM_LEFT = 4;
	
	public static final float width = Gdx.graphics.getWidth();
	public static final int distanceToEdge = (int) (width/500*15);
	public static final int backgroundMargin = (int) (width/500*26);
	public static final float topLabelSize = ConfirmRow.ROW_HEIGHT-(width/500*5);
	
	public List<ConfirmRow> rows = new ArrayList<ConfirmRow>();
	TextureRegion background = Assets.getTextureRegion("confirm-bottom");
	TextureRegion topLabel = Assets.getTextureRegion("confirm-move");
	TextureRegion okButton = Assets.getTextureRegion("Ok-button");
	TextureRegion cancelButton = Assets.getTextureRegion("cancel");
	ColorTable colorTable;
	public Rectangle okBounds, cancelBounds, dialogBounds;
	public boolean showingConfirmButtons = false;
	public static ConfirmRow apcost = new XxxPicRow(Assets.getTextureRegion("apcost"));
	
	/**
	 * Top left coordinates
	 */
	float x, y; 
		
	public ConfirmDialog(int position){
		
		switch(position){
		case TOP_RIGHT:
			x=Gdx.graphics.getWidth()-ConfirmRow.ROW_WIDTH-distanceToEdge;
			y = Gdx.graphics.getHeight()*0.85f-distanceToEdge;
			break;
		case TOP_LEFT:
			x=distanceToEdge;
			y = Gdx.graphics.getHeight()*0.85f-distanceToEdge;
			break;
		case BOTTOM_RIGHT:
			x=Gdx.graphics.getWidth()-ConfirmRow.ROW_WIDTH-distanceToEdge;
			y = Gdx.graphics.getHeight()*0.4f-distanceToEdge+getHeight();
			break;
		case BOTTOM_LEFT:
			x=distanceToEdge;
			y = Gdx.graphics.getHeight()*0.4f-distanceToEdge+getHeight();
			break;
		default:
			x=Gdx.graphics.getWidth()-ConfirmRow.ROW_WIDTH-distanceToEdge;
			y = Gdx.graphics.getHeight()-distanceToEdge;
		}
		colorTable = new ColorTable(new Color(75/255f, 40/255f, 28/255f, 1f)); //brown wood
		colorTable.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("single-border")));
		
		refreshBounds();
	}
	
	/**
	 * Drawn from bottom left
	 * @param batch
	 */
	public void draw(SpriteBatch batch){
		
		float width = ConfirmRow.ROW_WIDTH;
		float incHeight = ConfirmRow.ROW_HEIGHT;
		batch.begin();
		
		batch.draw(background, x-backgroundMargin, y-getHeight()-backgroundMargin, getWidth()+backgroundMargin*2, getHeight()+backgroundMargin-topLabelSize);
		batch.draw(topLabel, x-backgroundMargin, y-topLabelSize, getWidth()+backgroundMargin*2, topLabelSize+backgroundMargin);
		//Assets.SKIN.getPatch("double-border").draw(batch, x, y-getHeight(), getWidth(), getHeight());
		
		//colorTable.draw(batch, 1f);
		
		
		for(int i=0; i<rows.size(); i++){
			rows.get(i).draw(x, y-i*incHeight-incHeight, batch);
		}
		if(showingConfirmButtons){
			batch.draw(okButton, okBounds.x, okBounds.y, okBounds.width, okBounds.height);
			batch.draw(cancelButton, x+width/2, y-(rows.size()+2)*incHeight, width/2, incHeight*2);
		}
		
		batch.end();
	}
	public float getWidth(){
		return ConfirmRow.ROW_WIDTH;
	}
	public float getHeight(){
		if(showingConfirmButtons)		
			return ConfirmRow.ROW_HEIGHT * (rows.size()+2); // button row is below with twice size
		else
			return ConfirmRow.ROW_HEIGHT * (rows.size());
	}
	public void refreshBounds(){
		okBounds = new Rectangle(x, y-getHeight(), 2*ConfirmRow.ROW_HEIGHT, 2*ConfirmRow.ROW_HEIGHT);
		cancelBounds = new Rectangle(x+2*ConfirmRow.ROW_HEIGHT, y-getHeight(), 2*ConfirmRow.ROW_HEIGHT, 2*ConfirmRow.ROW_HEIGHT);
		colorTable.setX(x-backgroundMargin);
		colorTable.setY(y-getHeight()-backgroundMargin);
		colorTable.setWidth(getWidth()+2*backgroundMargin);
		colorTable.setHeight(getHeight()+2*backgroundMargin);
		dialogBounds = new Rectangle(x, y-getHeight(), getWidth(), getHeight());
	}
	
	/**
	 * @param quadrant
	 * @return valid output even for faulty input
	 */
	public static int invertQuadrant(int quadrant){
		switch(quadrant){
		case TOP_LEFT:
			return BOTTOM_RIGHT;
		case TOP_RIGHT:
			return BOTTOM_LEFT;
		case BOTTOM_LEFT:
			return TOP_RIGHT;
		case BOTTOM_RIGHT:
			return TOP_LEFT;
		default:
			return TOP_RIGHT;
		}
	}
	
	public static ConfirmDialog abilityConfirm(int dialogPosition, String topLabel, ConfirmRow... confirmRows){
		ConfirmDialog confirmDialog = new ConfirmDialog(dialogPosition);
		
		confirmDialog.topLabel = Assets.getTextureRegion(topLabel==null || topLabel.equals("")?"confirm-ability":topLabel);
		confirmDialog.rows.add(new TextRow(""));//empty row to make room for top label
		for(ConfirmRow row : confirmRows){
			confirmDialog.rows.add(row);
		}
		confirmDialog.refreshBounds();
		return confirmDialog;
	}
	
	public static ConfirmDialog moveConfirm(Unit unit, Path path, int dialogPosition){
		ConfirmDialog confirmDialog = new ConfirmDialog(dialogPosition);
		
		confirmDialog.topLabel = Assets.getTextureRegion("confirm-move");
		confirmDialog.rows.add(new TextRow(""));//empty row to make room for top label
		confirmDialog.rows.add(new APRow(unit, path.getPathCost(unit.getUnitType())));
		
		confirmDialog.refreshBounds();
		
		GEngine.getInstance().confirmOverlay.showMove(unit.tileCoordinate, path);
		
		return confirmDialog;
	}
	public static ConfirmDialog attackConfirm(Unit attacker, Unit target, int dialogPosition){
		ConfirmDialog confirmDialog = new ConfirmDialog(dialogPosition);
		
		confirmDialog.topLabel = Assets.getTextureRegion("confirm-attack");
		confirmDialog.rows.add(new TextRow(""));//empty row to make room for top label
		confirmDialog.rows.add(new DamageRow(Combat.minDamage(attacker, target), Combat.maxDamage(attacker, target)));
		confirmDialog.rows.add(apcost);
		confirmDialog.rows.add(new APRow(attacker, attacker.getAPCostAttack()));
		
		confirmDialog.refreshBounds();
		GEngine.getInstance().confirmOverlay.showAttack(attacker.tileCoordinate, target.tileCoordinate, attacker.getAPCostAttack());
		
		return confirmDialog;
	}
	public static ConfirmDialog captureConfirm(Unit unit, Building building, int dialogPosition){
		ConfirmDialog confirmDialog = new ConfirmDialog(dialogPosition);
		
		confirmDialog.topLabel = Assets.getTextureRegion("confirm-capture");
		confirmDialog.rows.add(new TextRow(""));//empty row to make room for top label
		confirmDialog.rows.add(new APRow(unit, 4));
		confirmDialog.rows.add(new FlagRow(building, unit));
		
		confirmDialog.refreshBounds();
		
		return confirmDialog;
	}
	public static ConfirmDialog buyConfirm(UnitType type, int dialogPosition){
		ConfirmDialog confirmDialog = new ConfirmDialog(dialogPosition);
		
		confirmDialog.topLabel = Assets.getTextureRegion("confirm-create");
		confirmDialog.rows.add(new TextRow(""));//empty row to make room for top label
		//confirmDialog.rows.add(new TextRow(type.name));
		confirmDialog.rows.add(new CostRow(State.activeState.getCurrentPlayer().gold, type.cost, true));
		
		confirmDialog.refreshBounds();
		
		return confirmDialog;
	}
	
}
