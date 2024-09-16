package com.desafiosenior.api_hotel.util;

public class Utils {
	public static <T> boolean assertEqualsWithOr(T actual, @SuppressWarnings("unchecked") T... expectedValues) {
		if (actual == null) return false;
		for (T expected : expectedValues) {
			if (actual.equals(expected)) {
				return true;
			}
		}
		return false;
	}
}
