package com.anstrat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class TestUtil {

	public static void writeObject(Object serializable, String filename) throws FileNotFoundException, IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
		out.writeObject(serializable);
		out.close();
	}
	
	public static Object readObject(String filename) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
		Object obj = in.readObject();
		in.close();
		return obj;
	}
}
