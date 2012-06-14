package com.anstrat.gameCore;

import java.io.Serializable;

import com.anstrat.geography.TileCoordinate;

public class Building implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_TEMPLE  = 0;
	public static final int TYPE_VILLAGE = 1;
	public static final int TYPE_CASTLE  = 2;
	
	public int type;
	public int controllerId = -1;
	public int id;
	public TileCoordinate tileCoordinate;
	public String typeName;
	public int goldIncome = 0;
	public int manaIncome = 0;
	public int capturePointReg = 1;
	public int captureCost;
	public int captureCostRemaining;
	
	public Building(int type, int id, int controllerId)
	{
		switch(type) {
		case TYPE_TEMPLE:
			typeName = "Temple";
			goldIncome = 2;
			manaIncome = 5;
			captureCost = 5;
			break;
		case TYPE_VILLAGE:
			typeName = "Village";
			goldIncome = 5;
			manaIncome = 2;
			captureCost = 5;
			break;
		case TYPE_CASTLE:
			typeName = "Capital";
			goldIncome = 8;
			manaIncome = 8;
			captureCost = 12;
			break;
		default:
			typeName = "Village";
			goldIncome = 5;
			manaIncome = 2;
			captureCost = 5;
		}
		captureCostRemaining = captureCost;
		this.type = type;
		tileCoordinate = null;
		this.id = id;
		this.controllerId = controllerId;
	}
	/*public Building(int type, int id){
		Player player = State.activeState.getCurrentPlayer();
		int controllerId = player.playerId;
		this(type, id, controllerId);
		
	}*/
}
