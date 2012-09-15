package com.anstrat.guiComponent;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

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
		TextButtonStyle style = Assets.SKIN.get("menu", TextButtonStyle.class);
		style.font = font;
		TextButton button = new TextButton(caption, style);
		if(cl!=null)
			button.addListener(cl);
		return button;
	}
	public static TextButton createButton(String caption, ClickListener cl){
		TextButton temp = createButton(caption);
		temp.addListener(cl);
		return temp;
	}
	public static TextButton createButton(String caption){
		return new TextButton(caption, Assets.SKIN.get(TextButtonStyle.class));
	}
	public static Button createButton(TextureRegion image, String style){
		Button temp = new Button(Assets.SKIN.get(style, ButtonStyle.class));
		//temp.set
		return temp;
	}
	public static Button createButton(TextureRegion image, ClickListener cl){
		Button temp = new Button(new TextureRegionDrawable(image));
		temp.addListener(cl);
		return temp;
	}
	public static Button createButton(TextureRegion image, String style, ClickListener cl){
		Button temp = new Button(new Image(image), Assets.SKIN.get(style, ButtonStyle.class));
		temp.addListener(cl);
		return temp;
	}
	
	public static TextField createTextField(String messageText, boolean isPassword){
		TextField tf = new TextField("", Assets.SKIN.get(TextFieldStyle.class));
		tf.setMessageText(messageText);
		tf.setPasswordMode(isPassword);
		tf.setPasswordCharacter('*');
		tf.setTextFieldListener(tl);
		return tf;
	}
	
	public static CheckBox createCheckBox(String messageText){
		CheckBox tf = new CheckBox(messageText, Assets.SKIN.get(CheckBoxStyle.class));
		return tf;
	}
	
	public static Button createNetworkMenuButton(String caption, ClickListener cl){
		Button button = createMenuButton(caption, cl);
		Main.getInstance().network.registerNetworkButton(button);
		return button;
	}
	
	private static Label networkLabel;
	public static synchronized Label createLoginLabel(){
		if(networkLabel == null){
			networkLabel = new Label("",Assets.SKIN);
			Main.getInstance().network.setNetworkLabel(networkLabel);
		}
		
		return networkLabel;
	}
}
