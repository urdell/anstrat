package com.anstrat.popup;

import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.esotericsoftware.tablelayout.Cell;

/**
 * @author Kalle
 */
public class Popup extends Window {
	
	public static final int WIDTH = (int)(90*Main.percentWidth);
	
	private static PopupGestureHandler gestureHandler;
	private static PopupInputMultiplexer inputMultiplexer;
	private static Stage stage;
	private static Stack stack;
	protected static Sprite overlay;
	
	public static UnitInfoPopup unitInfoPopup;
	
	public static final ClickListener POPUP_CLOSE_BUTTON_HANDLER = new ClickListener() {
		@Override
		public void click(Actor actor, float x, float y) {
			getCurrentPopup().close();
		}
	};
	
	public boolean handlesBackspace = false;
	public boolean drawOverlay = true;

	public Popup(String title, boolean handlesBackspace, Actor... actors) {
		this(title, actors);
		this.handlesBackspace = handlesBackspace;
	}
	
	public Popup(Actor... actors){
		this("", actors);
	}
	
	public Popup(String title, Actor... actors) {
		super(Assets.SKIN);
		this.setTitle(title);

		top();
		setComponents(actors);
		this.layout();
	}
	
	/**
	 * Returns whether a TextField is focused or not.
	 */
	public boolean textFieldSelected(){
		return stage.getKeyboardFocus() instanceof TextField;
	}
	
	/** 
	 * Sets the actors of this popup.
	 */
	public void setComponents(Actor... actors){
	    this.clear();
		for(Actor a : actors)
			add(a);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Cell add (Actor actor) {
		row();
		Cell cell = super.add(actor).width(WIDTH).center().expandX().fillX();
		if(actor instanceof TextButton)
			cell.height((int)Main.percentHeight*8);
		else if(actor instanceof Label)
			((Label)actor).setWrap(true);
		return cell;
	}
	
	/** Clears all text inputs in the popup. */
	public void clearInputs(){
		for(Actor a : this.children){
			if(a instanceof TextField) ((TextField)a).setText("");
		}
	}
	
	/**
	 * Shows the popup, on top of any other popups present.
	 */
	public void show(){
		// Take control of all input
		gestureHandler.setOverridesInput(true);
		inputMultiplexer.setOverridesInput(true);
		
		stack.addActor(this);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		stage.unfocusAll();
	}
	
	/**
	 * Close the popup
	 */
	public void close(){
		stack.removeActor(this);
		
		if(stack.getActors().isEmpty()){
			// Last Popup gone - return control
			gestureHandler.setOverridesInput(false);
			inputMultiplexer.setOverridesInput(false);
		}
		
		stage.unfocusAll();
		Gdx.input.setOnscreenKeyboardVisible(false);
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		
		// Only let the currently active popup draw the overlay
		if(drawOverlay && getCurrentPopup() == this) overlay.draw(batch);
		super.draw(batch, parentAlpha);
		
		// Only actually draws debug if it's enabled on the table layout
		drawDebug(stage);
	}
	
	public void resize(int width, int height) {
		System.out.println("Lol");
		overlay.setSize(width, height);
		pack();
		
		stack.height = stack.getPrefHeight();
		stack.width = stack.getPrefWidth();
		stack.x = (width - stack.width) / 2f;
		stack.y = (height - stack.height) / 2f;
	}
	
	public static void initPopups(Stage stage){
		gestureHandler = new PopupGestureHandler();
		inputMultiplexer = new PopupInputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		
		// Add processors in front of all other processors
		Main.getInstance().gestureMultiplexer.addProcessor(0, gestureHandler);
		Main.getInstance().addProcessor(0, inputMultiplexer);
		
		//popupQueue = new ArrayList<Popup>();
		Popup.stage = stage;
		
		Popup.stack = new Stack();
		stage.addActor(Popup.stack);
		
		overlay = new Sprite(Assets.WHITE);
		
		Color bcolor = Color.DARK_GRAY;
		bcolor.a = 0.8f;
		overlay.setColor(bcolor);
		
		unitInfoPopup = new UnitInfoPopup();
	}
	
	/**
	 * Shows a generic {@link Popup}
	 */
	public static void showGenericPopup(String title, String message){
		Button ok = ComponentFactory.createButton("Ok", POPUP_CLOSE_BUTTON_HANDLER);
		Label label = new Label(message, Assets.SKIN);
		label.setWrap(true);
		
		Popup p = new Popup(title);
		p.add(label).expandY();
		p.add(ok);
		p.show();
	}
	
	public static void disposePopups(){
		overlay = null;
		gestureHandler = null;
		unitInfoPopup = null;
	}
	
	public static Popup getCurrentPopup(){
		List<Actor> actors = stack.getActors();
		int size = actors.size();
		return size == 0 ? null : (Popup) actors.get(size - 1);
	}
}
