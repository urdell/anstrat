package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.GameInstanceType;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Player;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class GameInstanceView extends Table {
	
	public GameInstanceView(final GameInstance instance){
		Label turn = new Label("Turn " + instance.getTurnNumber(), Assets.SKIN);
    	Label players = new Label(formatOpponent(instance), Assets.SKIN);
    	
    	setTouchable(Touchable.enabled);
    	setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("games-frame")));

    	defaults().left().fillX().expandX();
    	
    	Table top = new Table();
    	top.defaults().left().height(4f*Main.percentHeight);
    	top.add(players);
    	
    	Table bottom = new Table();
    	bottom.add(turn).pad(3f+Main.percentWidth);
    	
    	add(top);
    	row();
    	add(bottom);
    	
    	addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	instance.showGame(false);
	        }
		});
	}
	
	private static String formatOpponent(GameInstance instance){
		String opponent = null;
    	
    	if(instance.getGameType() == GameInstanceType.AI || instance.getGameType() == GameInstanceType.NETWORK){
    		StringBuffer opponents = new StringBuffer("");
    		boolean first = true;
    		Player userPlayer = instance.getUserPlayer();
    		
        	for(Player p : instance.state.players){
        		if(p != userPlayer){
        			if(!first) opponents.append(", ");
        			opponents.append(p.getDisplayName());
        		}
        	}
        	
        	opponent = opponents.toString();
    	}
    	else {
    		opponent = instance.state.players[0].getDisplayName() + " vs. " +instance.state.players[1].getDisplayName();
    	}
    	
    	return opponent;
	}
}
