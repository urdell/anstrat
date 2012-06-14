package com.anstrat.menu;

import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.Popup;
import com.anstrat.popup.PopupHandler;

public class AccountMenuPopupHandler implements PopupHandler {
	public void handlePopupAction(String text)
	{
		AccountMenu am = AccountMenu.getInstance();
		
		if(text.equalsIgnoreCase("Cancel")){
			am.clearInputs();
			Popup.currentPopup.close();
			return;
		}
		
		if(Popup.currentPopup == am.loginPopup && text.equals("Ok")){
			String username = ComponentFactory.getTextFieldValue(am.loginPopup, "username");
			String password = ComponentFactory.getTextFieldValue(am.loginPopup, "password");
			
			am.clearInputs();
			
			Main.getInstance().login(username, password);
			
			Popup.currentPopup.close();
			am.clearInputs();
			am.connectingPopup.show();
			
			return;
		}
		
		if(Popup.currentPopup==am.registerPopup && text.equals("Ok")){
			String username = ComponentFactory.getTextFieldValue(am.registerPopup, "username");
			String password = ComponentFactory.getTextFieldValue(am.registerPopup, "password");
			String displayed = ComponentFactory.getTextFieldValue(am.registerPopup,"displayedInput");
			
			am.clearInputs();
			
			System.out.println("Sending register request "+username+":"+password+":"+displayed);
			Main.getInstance().network.register(username, password, displayed);
			
			Popup.currentPopup.close();
			am.clearInputs();
			am.connectingPopup.show();
			
			return;
		}
	}
}
