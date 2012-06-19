package com.anstrat.guiComponent;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.core.User;
import com.anstrat.menu.NetworkDependentTracker;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class ComponentFactory {
	
	public static TextButton createMenuButton(String caption){
		return createMenuButton(caption, Assets.MENU_FONT, null);
	}
	public static TextButton createMenuButton(String caption, ClickListener cl){
		return createMenuButton(caption, Assets.MENU_FONT, cl);
	}
	public static TextButton createMenuButton(String caption, BitmapFont font, ClickListener cl){
		TextButtonStyle style = Assets.SKIN.getStyle("menu", TextButtonStyle.class);
		style.font = font;
		TextButton button = new TextButton(caption, style);
		if(cl!=null)
			button.setClickListener(cl);
		return button;
	}
	public static TextButton createButton(String caption, String name, ClickListener cl){
		TextButton temp = createButton(caption, name);
		temp.setClickListener(cl);
		return temp;
	}
	public static TextButton createButton(String caption, String name){
		return new TextButton(caption, Assets.SKIN.getStyle(TextButtonStyle.class), name);
	}
	public static Button createButton(TextureRegion image, ClickListener cl){
		Button temp = new Button(image);
		temp.setClickListener(cl);
		return temp;
	}
	public static Button createButton(TextureRegion image, String style, ClickListener cl){
		Button temp = new Button(new Image(image), Assets.SKIN.getStyle(style, ButtonStyle.class));
		temp.setClickListener(cl);
		return temp;
	}
	
	public static TextField createTextField(String messageText, String name, boolean isPassword){
		TextField tf = new TextField("", messageText, Assets.SKIN.getStyle(TextFieldStyle.class), name);
		tf.setPasswordMode(isPassword);
		tf.setPasswordCharacter('*');
		return tf;
	}
	
	public static Label createLoginLabel(){
		User user = Main.getInstance().user;
        Label label = new Label((user==null || user.displayName==null || user.displayName.equals(""))?"Connecting...":user.displayName, 
        				new LabelStyle(Assets.UI_FONT,Color.WHITE));
        NetworkDependentTracker.registerLabel(label);
        return label;
	}
	
	/**
	 * TOOLS
	 */
	public static String getTextFieldValue(Group g, String name){
		Actor a = g.findActor(name);
		if(a instanceof TextField)
			return ((TextField)a).getText();
		System.err.println("setText: TextField '"+name+"' not found.");
		return null;
	}
	public static void setText(Group g, String name, String value){
		if(value==null)
			value = "";
		Actor a = g.findActor(name);
		if(a instanceof TextField)
			((TextField)a).setText(value);
		else if(a instanceof TextButton)
			((TextButton)a).setText(value);
		else if(a instanceof Label)
			((Label)a).setText(value);
		else
			System.err.println("setText: '"+name+"' not found.");
		return;
	}
}
