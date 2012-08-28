package com.anstrat.network;

import java.io.Serializable;

import com.anstrat.core.Serialization;
import com.badlogic.gdx.files.FileHandle;

public class User implements Serializable {
	
	private static final long serialVersionUID = 4L;
	
	public final long userID;
	public final String password;
	public final String displayName;
	
	public final boolean usingDefaultName;

	public User(long userID, String password, String displayName){
		this.userID = userID;
		this.password = password;
		this.displayName = displayName != null ? displayName : "Unnamed" + userID;
		this.usingDefaultName = displayName == null;
	}
	
	public void toFile(FileHandle handle){
		Serialization.writeObject(this, handle);
	}
	
	public static User fromFile(FileHandle handle){
		Object obj = Serialization.readObject(handle);
		return obj instanceof User ? (User)obj : null;
	}
}
