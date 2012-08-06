package com.anstrat.menu;

import java.util.ArrayList;
import java.util.List;

import com.anstrat.core.Assets;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class NetworkDependentTracker {
	
	public static List<Button> networkButtons = new ArrayList<Button>();
	public static List<Label> loginLabels;
	public static boolean hasNetwork = true;
	
	public static void enableNetworkButtons(){
		hasNetwork = true;
		for(Button b : networkButtons)
			Assets.SKIN.setEnabled(b, true);
	}
	public static void disableNetworkButtons(){
		hasNetwork = false;
		for(Button b : networkButtons)
			Assets.SKIN.setEnabled(b, false);
	}
	
	public static void registerNetworkButton(Button newButton){
		if(loginLabels == null) loginLabels = new ArrayList<Label>();
		networkButtons.add(newButton);
		
		if(!hasNetwork) Assets.SKIN.setEnabled(newButton, false);
	}
	
	public static void dispose(){
		loginLabels    = null;
		networkButtons = null;
	}
	
	public static void registerLabel(Label label)
	{
		if(loginLabels == null) loginLabels = new ArrayList<Label>();
		
		//Copy another registered label so we have current text. 
		if(!loginLabels.isEmpty())
			label.setText(loginLabels.get(0).getText());
		
		loginLabels.add(label);
		
	}
	
	public static void changeLogin(String dispname){
		if(dispname==null || dispname.equals(""))
		{
			for(Label loginLabel : loginLabels)
				loginLabel.setText("Connecting...");
		}
		else
		{
			for(Label loginLabel : loginLabels)
				loginLabel.setText("Logged in as: "+dispname);
		}
	}
}