package com.anstrat.core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Serialization {

	public static void writeObject(Serializable obj, FileHandle handle){
		
		ObjectOutputStream out = null;
		
		try{
			out = new ObjectOutputStream(handle.write(false));
			out.writeObject(obj);
			Gdx.app.log("Serialization", String.format("Serialized object '%s' to file '%s'.", obj, handle.name()));
		}
		catch(Exception e){
			Gdx.app.log("Serialization", String.format("Failed to serialize object '%s' to file '%s' due to '%s'.", obj, handle.name(), e.getMessage()));
		}
		finally{
			try{ out.close(); } catch(Exception e){/* Don't care */}
		}
	}
	
	public static Object readObject(FileHandle handle){
		ObjectInputStream in = null;
		
		try{
			in = new ObjectInputStream(handle.read());
			return in.readObject();
		}
		catch(Exception e){
			Gdx.app.log("Serialization", String.format("Failed to deserialize object from file '%s' due to '%s'.", handle.name(), e.getMessage()));
			return null;
		}
		finally{
			try{ in.close(); } catch(Exception e){/* Don't care */}
		}
	}
}
