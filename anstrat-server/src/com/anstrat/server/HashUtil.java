package com.anstrat.server;

import java.security.MessageDigest;
import java.util.Formatter;

/**
 * Helper class for hash functions.
 * @author eriter
 *
 */
public class HashUtil {
	
	private static final String PASSWORD_SALT = "0517455ec701b4b870b70ddb13ae3f27e9393325797348cbe7cfa636c65d3174";
	private static final int NUM_ITERATIONS = 1000; // TODO Update to a random value before final release.
	
	/**
	 * For maximum security it is recommended that the number of iterations should be at least 1000
	 * according to the RSA PKCS5 standard.
	 * @param algorithm the algorithm to use, for example SHA-256, SHA-512 or MD5
	 * @param numIterations the number of extra operations
	 * @return a hex string representing the hashed input string
	 */
	public static String getHash(String algorithm, String password){
		
		try{
			
			byte[] salt = PASSWORD_SALT.getBytes("UTF-8");
			
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.reset();
			digest.update(salt);
			byte[] input = digest.digest(password.getBytes("UTF-8"));
	   
			for (int i = 0; i < NUM_ITERATIONS; i++) {
				digest.reset();
				input = digest.digest(input);
			}
	   
			return toHexString(input);
		}
		catch(Exception e){
			throw new RuntimeException("Failed to compute hash.", e);
		}
	}
	
	/**
	 * Converts a byte array to a hex string.
	 * @param bytes The array to be converted.
	 * @return The hex string representation of the byte array.
	 */
	public static String toHexString(byte[] bytes){
		StringBuilder builder = new StringBuilder(bytes.length * 2);
		Formatter formatter = new Formatter(builder);
		
		for(byte b : bytes){
			formatter.format("%02X", b);
		}
		
		return builder.toString().toLowerCase();
	}
}
