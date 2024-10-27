package org.moeaframework.analysis.stream;

import java.util.function.Function;

import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.validate.Validate;

public class Groupings{
	
	private Groupings() {
		super();
	}
	
	public static <T> Function<Sample, T> exactValue(Parameter<T> parameter) {
		return x -> parameter.readValue(x);
	}
	
	public static <T> Function<T, T> exactValue() {
		return x -> x;
	}
	
	public static <T extends Number> Function<Sample, T> bucket(Parameter<T> parameter, T width) {
		return bucket(width).compose(x -> parameter.readValue(x));
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> Function<T, T> bucket(T width) {
		return x -> {
			if (x instanceof Integer intValue) {
				Integer intWidth = width.intValue();
				return (T)(Integer)(intWidth * (intValue / intWidth) + intWidth / 2);
			} else if (x instanceof Long longValue) {
				long longWidth = width.longValue();
				return (T)(Long)(longWidth * (longValue / longWidth) + longWidth / 2);
			} else if (x instanceof Float floatValue) {
				float floatWidth = width.floatValue();
				return (T)(Float)(floatWidth * (float)Math.floor(floatValue / floatWidth) + floatWidth / 2.0f);
			} else if (x instanceof Double doubleValue) {
				double doubleWidth = width.doubleValue();
				return (T)(Double)(doubleWidth * Math.floor(doubleValue / doubleWidth) + doubleWidth / 2.0);
			} else {
				return Validate.that("x", x).fails("unsupported type: " + x.getClass().getSimpleName());
			}
		};
	}
	
	public static <T extends Number> Function<Sample, Integer> round(Parameter<T> parameter) {
		return round().compose(x -> parameter.readValue(x));
	}
	
	public static <T extends Number> Function<T, Integer> round() {
		return x -> {
			if (x instanceof Float || x instanceof Double) {
				double doubleValue = x.doubleValue();
				return (int)Math.round(doubleValue);
			} else {
				return x.intValue();
			}
		};
	}
	
}