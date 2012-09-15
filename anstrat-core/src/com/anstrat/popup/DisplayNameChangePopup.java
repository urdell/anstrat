package com.anstrat.popup;

import java.util.regex.Pattern;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DisplayNameChangePopup extends Popup {
	
	private static final float TIMEOUT_DURATION = 5f;	 // seconds
	private float timePassed;
	private boolean pendingServerResponse;
	
	private Button ok;
	private Label status;
	
	private Runnable onNameChangeSuccessful;
	
	public DisplayNameChangePopup(Runnable onNameChangeSuccessful) {
		super("Pick a name");
		
		this.onNameChangeSuccessful = onNameChangeSuccessful;
		
		final TextField textField = ComponentFactory.createTextField("", false);
		textField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char key) {
				Assets.SKIN.setEnabled(ok, Pattern.matches("^[a-zA-Z0-9\\-_]{3,20}$", textField.getText()));
			}
		});
		
		ok = ComponentFactory.createButton("Ok", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
	        	setPending(true);
	        	Assets.SKIN.setEnabled(ok, false);
	        	Main.getInstance().network.setDisplayName(textField.getText());
	        }
	    });
		Assets.SKIN.setEnabled(ok, false);
		
		Button cancel = ComponentFactory.createButton("Cancel", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
	        	setPending(false);
	        	Popup.getCurrentPopup().close();
	        }
	    });
		
		add(new Label("To play online you need to choose a name.\nYour name can contain letters, digits, dash (-) and underscore (_).", Assets.SKIN)).fillX().expandX();
		row();
		add(textField).expandX().fillX();
		row();
		add(status = new Label("", Assets.SKIN)).expandX().fillX();
		row();
		add(new Row(ok, cancel));
	}
	
	@Override
	public void act(float timeDelta){
		super.act(timeDelta);
		
		if(pendingServerResponse) timePassed += timeDelta;
		
		if(timePassed >= TIMEOUT_DURATION){
			nameChangeError("No response from the server, check your connection.");
		}
	}
	
	public void nameChanged(String displayName){
		setPending(false);
		if(Popup.getCurrentPopup() == this){
			close();
			onNameChangeSuccessful.run();
		}
	}
	
	public void nameChangeError(String message){
		setPending(false);
		Popup.showGenericPopup("Failed to set name", message);
	}
	
	// Sets whether or not we're waiting for a response from the network
	private void setPending(boolean flag){
		pendingServerResponse = flag;
		timePassed = 0f;
		Assets.SKIN.setEnabled(ok, !flag);
		status.setText(flag ? "Waiting for server..." : "");
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
