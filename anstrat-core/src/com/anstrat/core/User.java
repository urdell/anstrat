package com.anstrat.core;

import java.io.Serializable;

import com.badlogic.gdx.files.FileHandle;

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static long globalUserID = 1337;
	public String username;
	public String password;
	public String displayName;

	public void toFile(FileHandle handle){
		Serialization.writeObject(this, handle);
	}
	
	public static User fromFile(FileHandle handle){
		Object obj = Serialization.readObject(handle);
		return obj instanceof User ? (User)obj : null;
	}
}
