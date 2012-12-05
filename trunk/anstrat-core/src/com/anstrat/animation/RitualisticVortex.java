package com.anstrat.animation;

import java.util.LinkedList;
import java.util.List;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class RitualisticVortex extends Animation {

	private GUnit source;
	private List<GUnit> targets = new LinkedList<GUnit>();
	private com.badlogic.gdx.graphics.g2d.Animation sourceAnimation, targetAnimation;
	private float animationStateTime = 0;
	private float draw_ap_time;
	
	public RitualisticVortex(Unit source, List<Unit> targets){
		sourceAnimation = Assets.getAnimation("fallen-ability");
		targetAnimation = Assets.getAnimation("fallen-ability-effect");
		
		GEngine engine = GEngine.getInstance();
		this.source = engine.getUnit(source);
		for(Unit unit : targets)
			this.targets.add(engine.getUnit(unit));
		length = sourceAnimation.animationDuration;
		lifetimeLeft = length;
		draw_ap_time = targetAnimation.animationDuration;
	}
	
	@Override
	public void run(float deltaTime) {
		if(source.getAlpha() > 0f)
			source.setAlpha(0f);
	}

	@Override
	public boolean isVisible() {
		return Fog.isVisible(source.unit.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
	
	@Override
	public void draw(float deltaTime, SpriteBatch batch){
		
		super.draw(deltaTime, batch);
		
		Vector2 srcposition = null;
		
		srcposition = source.getPosition();
		
		Color oldcolor = batch.getColor();
		
		batch.setColor(oldcolor.r, oldcolor.g, oldcolor.b, 1.0f);
		
		TextureRegion srcregion = sourceAnimation.getKeyFrame(animationStateTime, true);
		batch.draw(srcregion, srcposition.x - source.scale*srcregion.getRegionWidth() / 2f, 
				srcposition.y - source.scale*srcregion.getRegionHeight() / 2f - 15, 
				source.scale*srcregion.getRegionWidth(), source.scale*srcregion.getRegionHeight());
		
		if(animationStateTime <= draw_ap_time)
		{
			TextureRegion trgregion = targetAnimation.getKeyFrame(animationStateTime, true);
			
			for(GUnit target : targets)
			{
				Vector2 offsets = source.getPosition().cpy().sub(target.getPosition()).mul(animationStateTime/draw_ap_time);
				batch.draw(trgregion, target.getPosition().x - trgregion.getRegionWidth() / 2f + offsets.x, 
						target.getPosition().y - trgregion.getRegionHeight() / 2f + offsets.y);
			}
		}
		
		batch.setColor(oldcolor);
		
		animationStateTime += deltaTime;
	}
	
	@Override
	public void postAnimationAction()
	{
		GEngine.getInstance().gUnits.remove(source.unit.id);
	}
}