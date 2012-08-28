package com.anstrat.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import com.anstrat.ai.ScriptAI;
import com.anstrat.command.Command;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.geography.Map;
import com.anstrat.gui.GEngine;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.menu.MainMenu;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * A single game instance, containing a State.
 * 
 * Create a new one when you start a new game.
 * 
 * These are the objects stored stored on the phone for continuing earlier games.
 * @author Anton
 *
 */
public class GameInstance implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static GameInstance activeGame;
	
	public State state;
	private Collection<Integer> aiPlayerIDs;
	
	public GameInstance(long gameID, Map map, Player[] players){
		state = new State(map, players);
	}
	
	protected GameInstance(Map map, Player[] players, long randomSeed){
		state = new State(map, players, randomSeed);
	}
	
	public void setAIPlayers(Integer... aiPlayerIDs){
		this.aiPlayerIDs = Arrays.asList(aiPlayerIDs);
	}
	
	/**
	 * Switch to the ingame screen using this game. This is the only way you should move to the ingame screen through.
	 */
	public void showGame(boolean startZoom){
		activeGame = this;
		
		// Assign AI if needed (needs to be done here to work with deserialization, as the AI engine is not serialized)
		if(aiPlayerIDs != null){
			for(int i : aiPlayerIDs){
				Player p = state.players[i];
				p.assignAI(new ScriptAI(p));
			}
		}
		
		State.activeState = state;
		GEngine.getInstance().init(state, startZoom);
		Main.getInstance().setScreen(GEngine.getInstance());
		GEngine.getInstance().userInterface.updateCurrentPlayer();
		Gdx.app.log("GameInstane", "Game shown using GameInstance");
	}
	
	public int getTurnNumber(){
		return this.state.turnNr;
	}
	
	public void resign(){
		// Show game ended, if in game 
		if(Main.getInstance().getScreen() instanceof GEngine){
			Player user = getUserPlayer();
			Player winner = state.players[user.playerId == 0 ? 1 : 0];
			
			new Popup("Game over", true,
				new Label(String.format("Player %s has won the game!", winner.getDisplayName()), Assets.SKIN), 
				ComponentFactory.createButton("OK", new ClickListener() {
					@Override
					public void click(Actor actor, float x, float y) {
						Main.getInstance().setScreen(MainMenu.getInstance());
						Popup.getCurrentPopup().close();
					}
				})).show();
		}
		
		Main.getInstance().games.endGame(this);
	}
	
	public boolean isAiGame(){
		return this.aiPlayerIDs != null;
	}
	
	public Player getUserPlayer(){
		if(aiPlayerIDs == null) return state.getCurrentPlayer();
		
		// Find user player
		for(Player p : state.players){
			if(!aiPlayerIDs.contains(p.playerId)) return p;
		}
		
		// Should NEVER happen
		throw new RuntimeException("Game only contains AI's!");
	}
	
	public boolean isUserCurrentPlayer(){
		int currentPlayerIndex = (getTurnNumber() - 1) % state.players.length;
		
		return getUserPlayer().playerId == currentPlayerIndex;
	}
	
	protected boolean isActiveGame(){
		return Main.getInstance().getScreen() instanceof GEngine && activeGame == this;
	}
	
	public void onCommandExecute(Command command){
		
	}
}
