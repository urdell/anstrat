package com.anstrat.network;

import java.io.Serializable;

import com.anstrat.core.Serialization;
import com.badlogic.gdx.files.FileHandle;

public class User implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
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
