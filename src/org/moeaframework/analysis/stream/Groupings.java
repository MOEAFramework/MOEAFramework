/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.analysis.stream;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.util.validate.Validate;

/**
 * Collection of grouping functions, to be used with {@link DataStream#groupBy(Function)} or
 * {@link Partition#groupBy(Function)}.
 */
public class Groupings{
	
	private Groupings() {
		super();
	}
	
	/**
	 * Group  items by combining two grouping functions.
	 * 
	 * @param <T> the type of each value
	 * @param <L> the type of the left key
	 * @param <R> the type of the right key
	 * @param left the left grouping function
	 * @param right the right grouping function
	 * @return the grouping function
	 */
	public static <T, L, R> Function<T, Pair<L, R>> pair(Function<T, L> left, Function<T, R> right) {
		return x -> Pair.of(left.apply(x), right.apply(x));
	}
	
	/**
	 * Groups items by their exact parameter value.
	 * 
	 * @param <T> the type of the parameter
	 * @param parameter the parameter
	 * @return the grouping function
	 */
	public static <T> Function<Sample, T> exactValue(Parameter<T> parameter) {
		return x -> parameter.readValue(x);
	}
	
	/**
	 * Groups items by their exact value.
	 * 
	 * @param <T> the type of each value
	 * @return the grouping function
	 */
	public static <T> Function<T, T> exactValue() {
		return x -> x;
	}
	
	/**
	 * Groups items into buckets of a fixed width based on their parameter value.
	 * 
	 * @param <T> the type of parameter
	 * @param parameter the parameter
	 * @param width the bucket width
	 * @return the grouping function
	 * @see #bucket(Number)
	 */
	public static <T extends Number> Function<Sample, T> bucket(Parameter<T> parameter, T width) {
		return bucket(width).compose(x -> parameter.readValue(x));
	}
	
	/**
	 * Groups items into buckets of a fixed width.  The resulting group key will be a representative value for the
	 * bucket, typically the midpoint between the bucket's lower and upper bounds.
	 * 
	 * @param <T> the type of each value
	 * @param width the bucket width
	 * @return the grouping function
	 */
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
	
	/**
	 * Groups numeric values by their rounded value.
	 * 
	 * @param <T> the type of the parameter
	 * @param parameter the parameter
	 * @return the grouping function
	 */
	public static <T extends Number> Function<Sample, Integer> round(Parameter<T> parameter) {
		return round().compose(x -> parameter.readValue(x));
	}
	
	/**
	 * Groups numeric values by their rounded value.
	 * 
	 * @param <T> the type of each value
	 * @return the grouping function
	 */
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