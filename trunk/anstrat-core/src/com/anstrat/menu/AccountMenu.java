package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Menu to handle login, upgrade, register and logout 
 * @author Ekis
 *
 */
public class AccountMenu extends MenuScreen {
	private static AccountMenu me;
	private static AccountMenuPopupHandler popupHandler = new AccountMenuPopupHandler();
	
	public Popup connectingPopup = new Popup(popupHandler, "Connecting",
			new Label("Connecting...",Assets.SKIN),
			new TextButton("Cancel",Assets.SKIN));
	
	public Popup loginPopup = new Popup(popupHandler, "Login", 
			new Label("Please enter your username and password.", Assets.SKIN),
			ComponentFactory.createTextField("Login","username",false),
			ComponentFactory.createTextField("Password","password",true),
			new Row(new TextButton("Cancel",Assets.SKIN), new TextButton("Ok",Assets.SKIN)));
	
	public Popup registerPopup = new Popup(popupHandler, "Register",
			new Label("Please enter your desired username, password and displayed name.", Assets.SKIN),
			ComponentFactory.createTextField("Login","username",false),
			ComponentFactory.createTextField("Password","password",true),
			ComponentFactory.createTextField("Displayed name","displayedInput",false),
			new Row(new TextButton("Cancel",Assets.SKIN), new TextButton("Ok",Assets.SKIN)));
	
	private AccountMenu()
	{
		super();

		contents.register( "fastLoginButton",ComponentFactory.createMenuButton("Quick Login",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	quickPlay();
            }
        }));

		contents.register( "loginButton",ComponentFactory.createMenuButton("Login",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	loginPopup.show();
            }
        }));

        TextButton registerButton = ComponentFactory.createMenuButton("Register",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Popup.showGenericPopup("Register", "Please use Quick Login.");
            	//AccountMenu.registerPopup.show();
            }
        });
        
        Assets.SKIN.setEnabled(registerButton, false);
        
        contents.register( "registerButton", registerButton);

        contents.register("debugMenuButton", ComponentFactory.createMenuButton("Debug",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	Main.getInstance().setScreen(DebugMenu.getInstance());
            }
        }));
        
        contents.register( "login", ComponentFactory.createLoginLabel());
        
        contents.padTop((int) (3*Main.percentHeight));
        contents.parse( 	"* spacing:"+(int)(2*Main.percentWidth)+" padding:0 align:top width:"+BUTTON_WIDTH+" height:"+BUTTON_HEIGHT+
    					"[fastLoginButton]"+
    					"---"+
    					"[loginButton]"+
    					"---"+
    					"[registerButton]"+
    					"---"+
    					"[debugMenuButton] expand:y"+
    					"---"+
    					"{*align:center [login]}");
	}
	
	public static AccountMenu getInstance() {
		if(me == null){
			me = new AccountMenu();
		}
		return me;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}

	public void clearInputs() {
		registerPopup.clearInputs();
		loginPopup.clearInputs();
	}
	
	/**
	 * Starts a Quick Play game
	 */
	public void quickPlay(){
		if(!Main.getInstance().network.isLoggedIn())
			Main.getInstance().network.quickLogin();
		else
			Popup.showGenericPopup("Quick login refused", "Already logged in.");
	}
}
