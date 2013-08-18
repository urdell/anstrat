package com.anstrat.audio;

import com.anstrat.core.Options;
import com.badlogic.gdx.audio.Sound;

public class DelayedSound {
	
	protected Sound sound;
	private boolean finished;
	private float delay;
	
	public DelayedSound(Sound sound, float delay){
		this.sound = sound;
		this.delay = delay;
		this.finished = false;
	}
	
	public DelayedSound(Sound sound){
		this(sound,0f);
	}
	
	public void run(float delta){
		if(!finished)
		{
			delay -= delta;
			
			if(delay<0f)
			{
				//if(Options.soundOn)
					//sound.play();
				finished = true;
			}
		}
	}
	
	public DelayedSound clone()
	{
		return new DelayedSound(sound, delay);
	}
}
