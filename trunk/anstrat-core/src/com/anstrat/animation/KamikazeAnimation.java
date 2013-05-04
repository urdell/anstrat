package com.anstrat.animation;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.gameCore.Fog;
import com.anstrat.gameCore.Unit;
import com.anstrat.gui.GEngine;
import com.anstrat.gui.GUnit;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class KamikazeAnimation extends Animation {

	private Unit source, target;
	private GUnit gsource, gtarget;
	private com.badlogic.gdx.graphics.g2d.Animation boomAnimation;
	private boolean started = false, impact = false;
	private Vector2 startPos, offsets;
	private float timeElapsed = 0f, baseScale = 0.85f;
	private int damage;
	private Sprite sprite = null;
	
	public KamikazeAnimation(Unit source, Unit target, int damage){
		this.source = source; this.target = target;
		GEngine ge = GEngine.getInstance();
		this.gsource = ge.getUnit(source);
		this.gtarget = ge.getUnit(target);
		boomAnimation = Assets.getAnimation("hawk-ability");
		length = boomAnimation.animationDuration;
		lifetimeLeft = length;
		startPos = gsource.getPosition();
		offsets = gtarget.getPosition().cpy().sub(startPos);
		this.damage = damage;
	}
	
	@Override
	public void run(float deltaTime){
		timeElapsed += deltaTime;
		
		if(!started){
			started = true;
			gsource.playCustom(boomAnimation, false);
			GEngine.getInstance().gUnits.remove(gsource.unit.id);
		}
		
		if(!impact && lifetimeLeft < 2f * length / 3f){
			baseScale *= 1.3f;
			sprite = new Sprite(boomAnimation.getKeyFrame(timeElapsed));
			sprite.setScale(baseScale);
			impact = true;
			gtarget.updateHealthbar();
			FloatingNumberAnimation fanimation = new FloatingNumberAnimation(target.tileCoordinate, damage, 40f, Color.RED);
			GEngine.getInstance().animationHandler.runParalell(fanimation);
			if(target.currentHP <= 0){
				DeathAnimation dm = new DeathAnimation(target, offsets.cpy().nor());
				GEngine.getInstance().animationHandler.runParalell(dm);
			}
			else
				gtarget.playHurt();
				
		}
	}
	
	@Override
	public void draw(float dt, SpriteBatch sb){
		Vector2 temp = new Vector2();
		if(lifetimeLeft > 2f * length / 3f){
			temp = startPos.cpy().add(offsets.cpy().mul(2f*timeElapsed/length*0.75f));
			/* 0.75f - do not traverse the entire path */
		}
		else
			temp = startPos.cpy().add(offsets);
		TextureRegion tr = boomAnimation.getKeyFrame(timeElapsed);
		if(sprite==null){
			sprite = new Sprite(tr);
			sprite.setScale(baseScale);
		}
		sprite.setPosition(temp.x - 1f * tr.getRegionWidth() * baseScale / 2, 
				temp.y - 1f * tr.getRegionHeight() * baseScale / 2);
		sprite.setRotation((float)getRotationAngle());
		sprite.setRegion(tr);
		sprite.draw(sb);
	}
	
	private double getRotationAngle() {
        float dx = (int) (gtarget.getPosition().x-gsource.getPosition().x);
        float dy = (int) (gtarget.getPosition().y-gsource.getPosition().y);
        float x = Math.abs(dx);
        float y = Math.abs(dy);
        double res;
        if (y == 0) {
            if ( dx < 0 ){
                res = 270;
            }
            else {
                res = 90;
            }
        }
        else if (x != 0) {
            res = Math.toDegrees(Math.atan(y/x));
            if (dx < 0 && dy < 0) {
                res += 270;
            }
            else if (dx < 0 && dy > 0) {
                res = 90 - res;
                res += 180;
            }
            else if (dx > 0 && dy > 0) {
                res += 90;
            }
            else { //normal
                res = 90-res;
            }
        }
        else {
            if (dy > 0) {
                res = 180;
            }
            else {
                res = 0;
            }
        }
        return res;
    }
	
	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return Fog.isVisible(source.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId) ||
				Fog.isVisible(target.tileCoordinate,  GameInstance.activeGame.getUserPlayer().playerId);
	}
}