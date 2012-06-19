package com.anstrat.popup;

public abstract class PopupListener implements PopupHandler {

	private boolean manualClose = false;
	
	public PopupListener(boolean manualClose){
		this.manualClose = manualClose;
	}
	public PopupListener(){}
	
	@Override
	public void handlePopupAction(String text) {
		if(!text.equalsIgnoreCase(Popup.CANCEL))
			handle(text);
		
		if(!manualClose || text.equalsIgnoreCase(Popup.CANCEL))
			Popup.currentPopup.close();
	}
	
	/**
	 * Handle popup action (cancel + popup close is taken care of)
	 * @param text Message to handle.
	 */
	public abstract void handle(String text);
}
