package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Invite;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.guiComponent.Row;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameInvitePopup extends Popup {
	private TeamSelecter team = new TeamSelecter();
	
	public GameInvitePopup(final Invite invite) {
		super("Game invite");
		final Popup popup = this;
		
		final Button accept = ComponentFactory.createButton("Accept", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				invite.accept(team.getTeam(), 0);
				popup.close();
	        }
	    });
		
		final Button decline = ComponentFactory.createButton("Decline", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				invite.decline();
				popup.close();
	        }	
		});
		
		Label label = new Label(String.format("%s has challenged you to a game!", invite.otherPlayerName), Assets.SKIN);

		add(label).padBottom(Main.percentHeight*8f).align(Align.center);
		row();
		add(team).fillX().expandX().height(Main.percentHeight*8f).padBottom(Main.percentHeight*8f);
		add(new Row(accept, decline));
	}
}
