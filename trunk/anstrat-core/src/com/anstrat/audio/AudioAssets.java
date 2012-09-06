package com.anstrat.audio;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class AudioAssets {
	
	private static Map<String,Music> music;
	private static Map<String,Sound> sounds;
	
	private static FileHandle[] musicFiles = new FileHandle[]{
		Gdx.files.internal("music/vikingstitle.mp3")
	};
	
	private static FileHandle[] soundFiles = new FileHandle[]{
		
	};
	
	public static void load()
	{
		music = new HashMap<String,Music>();
		sounds = new HashMap<String,Sound>();
		
		/*
		FileHandle[] musicFiles = Gdx.files.internal("music").list();
		System.out.println(Gdx.files.local(Gdx.files.getLocalStoragePath()+"/music").exists());
		System.out.println(Gdx.files.local(Gdx.files.getLocalStoragePath()+"/music").isDirectory());
		System.out.println(Gdx.files.getLocalStoragePath()+"music");
		FileHandle[] soundFiles = Gdx.files.internal("music").list("xxx");
		Gdx.app.log("Audio", String.format("Loading %d audio files.", musicFiles.length+soundFiles.length));
		*/
		
		for(FileHandle musicFile : musicFiles)
		{
			Music m = Gdx.audio.newMusic(musicFile);
			m.setLooping(true);
			
			String mname = musicFile.nameWithoutExtension().toLowerCase(Locale.ENGLISH);
			music.put(mname, m);
			
			Gdx.app.log("Audio", String.format("Loaded track: %s.",mname));
		}
		
		for(FileHandle soundFile : soundFiles)
		{
			String sname = soundFile.nameWithoutExtension().toLowerCase(Locale.ENGLISH);
			sounds.put(sname, Gdx.audio.newSound(soundFile));
			
			Gdx.app.log("Audio", String.format("Loaded sound effect: %s.",sname));
		}
	}
	
	public static void dispose()
	{
		for(Music m : music.values())
			m.dispose();
		
		for(Sound s : sounds.values())
			s.dispose();
	}
	
	public static Sound getSound(String soundName){
		Sound s = sounds.get(soundName.toLowerCase(Locale.ENGLISH));
		
		if(s==null)
		{
			FileHandle fh = Gdx.files.internal("music/"+soundName+"mp3");
			if(fh.exists())
			{
				s = Gdx.audio.newSound(Gdx.files.internal("music/"+soundName+".mp3"));
				sounds.put(soundName, s);
			}
		}
		
		if(s==null)
			Gdx.app.log("Audio", String.format("Sound effect not found: %s.",soundName));
			
		return s;
	}
	
	public static Music getMusic(String musicName){
		Music m = music.get(musicName.toLowerCase(Locale.ENGLISH));
		
		if(m==null)
		{
			FileHandle fh = Gdx.files.internal("music/"+musicName+"xxx");
			if(fh.exists())
			{
				m = Gdx.audio.newMusic(Gdx.files.internal("music/"+musicName+".mp3"));
				m.setLooping(true);
				music.put(musicName, m);
			}
		}
		
		if(m==null)
			Gdx.app.log("Audio", String.format("Music file not found: %s.",musicName));
			
		return m;
	}
}