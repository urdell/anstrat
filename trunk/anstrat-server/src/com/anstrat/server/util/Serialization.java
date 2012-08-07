package com.anstrat.server.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class Serialization {
	public static byte[] serialize(Serializable object){
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			
			return baos.toByteArray();
			
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] data){
		try{
			ByteArrayInputStream stream = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(stream);
			return (T)ois.readObject();
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
}
