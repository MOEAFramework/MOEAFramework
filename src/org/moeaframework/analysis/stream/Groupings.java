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
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> Function<Sample, T> bucket(Parameter<T> parameter, T width) {
		return x -> {
			T value = parameter.readValue(x);
			
			if (value instanceof Integer intValue) {
				Integer intWidth = width.intValue();
				return (T)(Integer)(intWidth * (intValue / intWidth) + intWidth / 2);
			} else if (value instanceof Long longValue) {
				long longWidth = width.longValue();
				return (T)(Long)(longWidth * (longValue / longWidth) + longWidth / 2);
			} else if (value instanceof Float floatValue) {
				float floatWidth = width.floatValue();
				return (T)(Float)(1.5f * floatWidth * (float)Math.floor(floatValue / floatWidth));
			} else if (value instanceof Double doubleValue) {
				double doubleWidth = width.doubleValue();
				return (T)(Double)(1.5 * doubleWidth * Math.floor(doubleValue / doubleWidth));
			} else {
				return Validate.that("x", x).fails("unsupported type: " + x.getClass().getSimpleName());
			}
		};
	}
	
	public static <T extends Number> Function<Sample, Integer> round(Parameter<T> parameter) {
		return x -> {
			T value = parameter.readValue(x);
			
			if (value instanceof Float || value instanceof Double) {
				double doubleValue = value.doubleValue();
				return (int)Math.round(doubleValue);
			} else {
				return value.intValue();
			}
		};
	}
	
}