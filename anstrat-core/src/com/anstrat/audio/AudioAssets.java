package com.anstrat.audio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.anstrat.core.Options;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class AudioAssets {

	private static Map<String,Music> music;
	private static Map<String,Sound> baseSounds;
	private static Map<String,DelayedSound> sounds;
	private static Music currentTrack = null;
	private static Music nextTrack = null;
	private static final float FADE_DELAY = 3.0f;
	private static float FADE_PROGRESS = -1.0f;
	private static float CURRENT_VOLUME = 0.0f;

	private static void loadMusic(FileHandle musicHandle){
		try {
			Element root = new XmlReader().parse(musicHandle);
			int trackCount = root.getChildCount();
			int trackFail = 0;

			for (int i = 0; i < trackCount; i++) {
				String trackName = root.getChild(i).get("title");
				String trackFile = root.getChild(i).get("filename");

				FileHandle fh = Gdx.files.internal("audio/music/"+trackFile);
				if(fh.exists())
				{
					Music m = Gdx.audio.newMusic(fh);
					if(!trackName.equalsIgnoreCase("victory") && !trackName.equalsIgnoreCase("defeat"))
						m.setLooping(true);
					music.put(trackName.toLowerCase(Locale.ENGLISH), m);
				}
				else
				{
					Gdx.app.log("Audio", String.format("Failed to load %s.", fh));
					trackFail++;
				}
			}

			Gdx.app.log("Audio", String.format("Successfully loaded %d tracks from '%s'.", trackCount-trackFail, musicHandle));

		} catch (IOException e) {
			throw new IllegalArgumentException(String.format("Failed to parse tracks from '%s'.", musicHandle.name()), e);
		}
	}

	private static void loadSfxs(FileHandle sfxsHandle){
		try {
			Element root = new XmlReader().parse(sfxsHandle);
			int sfxCount = root.getChildCount();
			int sfxFail = 0;
			int uniques = 0;
			float sfxDelay = 0;

			for (int i = 0; i < sfxCount; i++) {
				String sfxName = root.getChild(i).get("name");
				String sfxFile = root.getChild(i).get("filename");
				try
				{
					sfxDelay = root.getChild(i).getFloat("delay");
				}
				catch(GdxRuntimeException gre)
				{
					;; //swallow
				}

				FileHandle fh = Gdx.files.internal("audio/sfx/"+sfxFile);
				if(fh.exists())
				{
					if(!baseSounds.containsKey(sfxFile))
					{
						uniques++;
						baseSounds.put(sfxFile.toLowerCase(Locale.ENGLISH), Gdx.audio.newSound(fh));
					}

					sounds.put(sfxName.toLowerCase(Locale.ENGLISH), 
							new DelayedSound(baseSounds.get(sfxFile.toLowerCase(Locale.ENGLISH)), sfxDelay));
				}
				else
				{
					Gdx.app.log("Audio", String.format("Failed to load %s.", fh));
					sfxFail++;
				}
			}

			Gdx.app.log("Audio", String.format("Successfully loaded %d (%d unique) sfxs from '%s'.", sfxCount-sfxFail,
					uniques, sfxsHandle));

		} catch (IOException e) {
			throw new IllegalArgumentException(String.format("Failed to parse sfxs from '%s'.", sfxsHandle.name()), e);
		}
	}

	public static void load(FileHandle musicHandle, FileHandle sfxsHandle)
	{
		music = new HashMap<String,Music>();
		baseSounds = new HashMap<String,Sound>();
		sounds = new HashMap<String,DelayedSound>();

		loadMusic(musicHandle);
		loadSfxs(sfxsHandle);
	}

	public static void dispose()
	{
		for(Music m : music.values()){
			m.stop();
			m.dispose();
		}

		for(Sound s : baseSounds.values()){
			s.stop();
			s.dispose();
		}

		music = null;
		baseSounds = null;
		sounds = null;
		currentTrack = null;
		nextTrack = null;
	}

	public static DelayedSound getSound(String soundName){
		DelayedSound s = sounds.get(soundName.toLowerCase(Locale.ENGLISH));

		if(s==null)
		{
			Gdx.app.log("Audio", String.format("Sound effect not found: %s.",soundName));
			return null;
		}

		return s.clone();
	}

	public static void playSound(String soundName)
	{
		if(Options.soundOn)
		{
			DelayedSound s = sounds.get(soundName.toLowerCase(Locale.ENGLISH));

			if(s==null)
			{
				Gdx.app.log("Audio", String.format("Sound effect not found: %s.",soundName));
				return;
			}

			//s.sound.play();
		}
	}

	public static void playMusic(String musicName)
	{
		if(Options.soundOn)
		{
			Music m = music.get(musicName.toLowerCase(Locale.ENGLISH));

			if(m==null)
			{
				Gdx.app.log("Audio", String.format("Music file not found: %s.",musicName));
				return;
			}

			nextTrack = m;
			FADE_PROGRESS = 0.0f;
			
			if(musicName.equalsIgnoreCase("victory") || musicName.equalsIgnoreCase("defeat"))
				FADE_PROGRESS = FADE_DELAY / 2.0f;
		}
	}

	public static void stopMusic()
	{
		for(Music track : music.values())
			track.pause();
	}

	public static void update(float delta)
	{
		if(FADE_PROGRESS >= 0.0f)
			FADE_PROGRESS += delta;
		
		if(FADE_PROGRESS >= 0.0f && FADE_PROGRESS < FADE_DELAY / 2.0f)
		{
			if(currentTrack == null || currentTrack.equals(nextTrack))
				FADE_PROGRESS = FADE_DELAY / 2.0f;
			else
			{
				CURRENT_VOLUME = 1.0f - FADE_PROGRESS / ( FADE_DELAY / 2.0f );
				currentTrack.setVolume(CURRENT_VOLUME);
			}
		}
		else if(FADE_PROGRESS >= FADE_DELAY / 2.0f)
		{
			if(nextTrack != null)
			{
				if(currentTrack != null)
					currentTrack.pause();
				currentTrack = nextTrack;
				nextTrack = null;
			}

			if(FADE_PROGRESS > FADE_DELAY)
			{
				currentTrack.setVolume(1.0f);
				FADE_PROGRESS = -1.0f;
			}
			else
			{
				float new_vol_calc = (FADE_PROGRESS - FADE_DELAY / 2.0f) / ( FADE_DELAY / 2.0f );
				if( new_vol_calc > CURRENT_VOLUME)
					CURRENT_VOLUME = new_vol_calc;
				currentTrack.setVolume(CURRENT_VOLUME);
				if(!currentTrack.isPlaying() && Options.soundOn)
					currentTrack.play();
			}

		}
	}
}