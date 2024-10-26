package org.moeaframework.analysis.stream;

import java.util.function.Function;

public class Mappings {
	
	private Mappings() {
		super();
	}
	
	public static <T> Function<T, T> identity() {
		return x -> x;
	}

	public static <T> Function<Number, Double> toDouble() {
		return x -> x.doubleValue();
	}

}
