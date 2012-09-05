package com.anstrat.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class AudioAssets {
	
	public static Music menuMusic;
	
	public static void load()
	{
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/vikingstitle.mp3"));
		menuMusic.setLooping(true);
	}
	
	public static void dispose()
	{
		menuMusic.dispose();
	}
}