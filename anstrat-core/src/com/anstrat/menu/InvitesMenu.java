package com.anstrat.menu;

public class InvitesMenu extends MenuScreen{

	private static InvitesMenu me;
	private InvitesMenu(){
		
	}
	public static synchronized InvitesMenu getInstance() {
		if(me == null){
			me = new InvitesMenu();
		}
		return me;
	}
	
}
