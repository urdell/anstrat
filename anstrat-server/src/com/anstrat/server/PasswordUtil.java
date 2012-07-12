package com.anstrat.server;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {
	
	private static final int NUM_ITERATIONS = 1000;
	
	public static boolean authenticate(String password, byte[] databaseBlob){
		// Separate encrypted password and salt
		byte[] encryptedPassword = new byte[20];	// SHA1 = 160 bit = 20 byte
		byte[] salt = new byte[8];
		
		System.arraycopy(databaseBlob, 0, encryptedPassword, 0, 20);
		System.arraycopy(databaseBlob, 20, salt, 0, 8);
		
		return Arrays.equals(encryptedPassword, getEncryptedPassword(password, salt));
	}
	
	// Returns a byte array of the hashed password with the salt appended
	public static byte[] generateDatabaseBlob(String password){
		byte[] salt = generateSalt();
		byte[] encryptedPassword = getEncryptedPassword(password, salt);
		byte[] blob = new byte[encryptedPassword.length + salt.length];
		
		System.arraycopy(encryptedPassword, 0, blob, 0, encryptedPassword.length);
		System.arraycopy(salt, encryptedPassword.length, blob, 0, salt.length);
		return blob;
	}
	
	private static byte[] getEncryptedPassword(String password, byte[] salt){
		// 160 is the key length, as SHA-1 generates a 160 bit hash
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, NUM_ITERATIONS, 160);
		
		try {
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded();
		} catch (Exception e){
			throw new IllegalStateException("Failed to encrypt password.", e);
		}
	}
	
	private static byte[] generateSalt() {
		// Use SecureRandom rather than Random for a cryptographically strong random number
		SecureRandom random = null;
		
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Failed to generate salt.", e);
		}
		
		// 8 byte salt as recommended by RSA PKCS5
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		return salt;
	}
}
