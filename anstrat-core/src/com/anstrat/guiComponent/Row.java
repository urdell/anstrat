package com.anstrat.guiComponent;

import com.anstrat.core.Main;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Kalle
 *
 */
public class Row extends Table {
	public Row(Actor a, Actor b){
		add(a).minWidth(1).height(Main.percentHeight*8f);
		add(b).minWidth(1).height(Main.percentHeight*8f);
		pack();
	}
	
	public void setListeners(ClickListener cl, TextFieldListener tl){
		for(Actor a : this.getChildren())
			if(a instanceof Button)
				((Button)a).addListener(cl);
			else if(a instanceof TextField)
				((TextField)a).setTextFieldListener(tl);
	}
}
