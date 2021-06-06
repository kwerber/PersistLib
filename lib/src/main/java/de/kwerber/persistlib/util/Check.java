package de.kwerber.persistlib.util;

public class Check {

	public static void notNullNotEmpty(String s) {
		if (s == null || s.isBlank()) {
			throw new IllegalArgumentException("string must neither be null nor empty: " + s);
		}
	}

	public static void notNull(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("object must not be null");
		}
	}

}
