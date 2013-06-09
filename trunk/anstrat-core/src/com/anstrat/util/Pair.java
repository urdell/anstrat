package com.anstrat.util;

/**
 * Replacement for AbstractMap.SimpleEntry that does not exist in the Android SDK till api level 9.
 * @author eriter
 */
public final class Pair<A,B> {
	public final A a;
	public final B b;
	
	public Pair(A a, B b){
		this.a = a;
		this.b = b;
	}
}
