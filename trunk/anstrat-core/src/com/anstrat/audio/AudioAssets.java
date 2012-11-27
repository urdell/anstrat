package com.anstrat.audio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.anstrat.core.Options;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class AudioAssets {
	
	private static Map<String,Music> music;
	private static Map<String,Sound> baseSounds;
	private static Map<String,DelayedSound> sounds;
	
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
			
			for (int i = 0; i < sfxCount; i++) {
				String sfxName = root.getChild(i).get("name");
				String sfxFile = root.getChild(i).get("filename");
				float sfxDelay = root.getChild(i).getFloat("delay");
				
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
		for(Music m : music.values())
			m.dispose();
		
		for(Sound s : baseSounds.values())
			s.dispose();
		
		music = null;
		baseSounds = null;
		sounds = null;
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
			
			s.sound.play();
		}
	}
	
	public static void playMusic(String musicName)
	{
		stopMusicExceptFor(musicName);

		Music m = music.get(musicName.toLowerCase(Locale.ENGLISH));

		if(m==null)
		{
			Gdx.app.log("Audio", String.format("Music file not found: %s.",musicName));
			return;
		}

		if(!m.isPlaying())
			m.play();
	}

	public static void stopMusic()
	{
		for(Music track : music.values())
			track.pause();
	}

	private static void stopMusicExceptFor(String ignore)
	{
		for(Entry<String,Music> track : music.entrySet())
		{
			if(!track.getKey().equalsIgnoreCase(ignore))
				track.getValue().pause();
		}
	}
}