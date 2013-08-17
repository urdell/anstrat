package com.anstrat.guiComponent;

import java.util.List;

import com.anstrat.core.Main;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TabPane extends Table {
	private List<Actor> tabs;
	private Table content = new Table();
	
	public TabPane(List<String> labels, List<Actor> tabs){
		this.tabs = tabs;
		
		for (int i = 0; i < labels.size(); i++) {
			addButton(labels.get(i), i);
		}
		
		this.row();
		this.add(content).fill().expand().colspan(labels.size());
		content.top();
		setActiveTab(0);
	}
	
	private void setActiveTab(int index){		
		Actor tab = this.tabs.get(index);
		this.content.clear();
		this.content.add(tab).fillX().expandX();
	}
	
	private void addButton(String text, final int index) {
		Button button = ComponentFactory.createButton(text);
		final TabPane pane = this;

		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				pane.setActiveTab(index);
			}
		});
		
		add(button).size(Main.percentWidth * 40, Main.percentHeight * 8f);
	}
}