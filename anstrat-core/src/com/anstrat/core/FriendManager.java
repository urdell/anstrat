package com.anstrat.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FriendManager {
	
	private List<String> friendsList = new ArrayList<String>();
	private FileHandle friendsFile;
	
	public FriendManager(FileHandle filehandle) {
		friendsFile = filehandle;
	}
	
	public List<String> getFriends() {
		return friendsList;
	}
	
	public void createFriend(String name) {
		if (!friendsList.contains(name))
			friendsList.add(name);
	}
	
	public void removeFriend(String name) {
		friendsList.remove(name);
	}
	
	public void clearFriends() {
		friendsList.clear();
	}
	
	public void saveFriends(){
		Serialization.writeObject(new FriendList(friendsList), friendsFile);
	}
	
	public void loadFriends(){
		Object obj = Serialization.readObject(friendsFile);
		
		if(obj == null){
			Gdx.app.log("FriendManager", "No previous friend found. Sorry mate..");
		}
		else{
			friendsList = ((FriendList)obj).friends;
		}
	}
	
	// Class used only to serialize/deserialize friends ( :D )
	private static class FriendList implements Serializable {
		private static final long serialVersionUID = 1L;
		private List<String> friends;
		
		public FriendList(List<String> friends){
			this.friends = friends;
		}
		@Override
		public String toString() {
			return String.format("%s(size = %d)", this.getClass().getSimpleName(), friends.size());
		}
	}
}
