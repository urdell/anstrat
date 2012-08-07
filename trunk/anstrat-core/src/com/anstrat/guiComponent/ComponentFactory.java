package com.anstrat.guiComponent;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;

public class ComponentFactory {
	
    private static TextFieldListener tl = new TextFieldListener() {
    	@Override
		public void keyTyped (TextField textField, char key) {
			if (key == '\n')
				textField.getOnscreenKeyboard().show(false);
			else if (key == '\t')
				textField.next(false);
		}
	};
	
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
	public static TextButton createButton(String caption, ClickListener cl){
		TextButton temp = createButton(caption);
		temp.setClickListener(cl);
		return temp;
	}
	public static TextButton createButton(String caption){
		return new TextButton(caption, Assets.SKIN.getStyle(TextButtonStyle.class));
	}
	public static Button createButton(TextureRegion image, String style){
		Button temp = new Button(Assets.SKIN.getStyle(style, ButtonStyle.class));
		//temp.set
		return temp;
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
	
	public static TextField createTextField(String messageText, boolean isPassword){
		TextField tf = new TextField("", messageText, Assets.SKIN.getStyle(TextFieldStyle.class));
		tf.setPasswordMode(isPassword);
		tf.setPasswordCharacter('*');
		tf.setTextFieldListener(tl);
		return tf;
	}
	
	public static Label createLoginLabel(){
		return new Label("Network disabled.", Assets.SKIN);
		/*
		User user = Main.getInstance().network.getUser();
        Label label = new Label((user==null || user.displayName==null || user.displayName.equals(""))?"Connecting...":user.displayName, 
        				new LabelStyle(Assets.UI_FONT,Color.WHITE));
        NetworkDependentTracker.registerLabel(label);
        return label;
        */
	}
}
