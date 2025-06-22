/* Copyright 2009-2025 David Hadka
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
	 * Buckets an integer-valued parameter into groups with similar values.
	 * 
	 * @param parameter the integer-valued parameter
	 * @param width the width of the bucket
	 * @return the grouping function
	 */
	public static Function<Sample, Integer> bucket(Parameter<Integer> parameter, int width) {
		return bucket(width).compose(x -> parameter.readValue(x));
	}
	
	/**
	 * Buckets a long-valued parameter into groups with similar values.
	 * 
	 * @param parameter the long-valued parameter
	 * @param width the width of the bucket
	 * @return the grouping function
	 */
	public static Function<Sample, Long> bucket(Parameter<Long> parameter, long width) {
		return bucket(width).compose(x -> parameter.readValue(x));
	}
	
	/**
	 * Buckets an double-valued parameter into groups with similar values.
	 * 
	 * @param parameter the double-valued parameter
	 * @param width the width of the bucket
	 * @return the grouping function
	 */
	public static Function<Sample, Double> bucket(Parameter<Double> parameter, double width) {
		return bucket(width).compose(x -> parameter.readValue(x));
	}
	
	/**
	 * Buckets an integer stream into groups with similar values.
	 * 
	 * @param width the width of the bucket
	 * @return the grouping function
	 */
	public static Function<Integer, Integer> bucket(int width) {
		return x -> width * (x / width) + width / 2;
	}
	
	/**
	 * Buckets a long stream into groups with similar values.
	 * 
	 * @param width the width of the bucket
	 * @return the grouping function
	 */
	public static Function<Long, Long> bucket(long width) {
		return x -> width * (x / width) + width / 2;
	}
	
	/**
	 * Buckets a double stream into groups with similar values.
	 * 
	 * @param width the width of the bucket
	 * @return the grouping function
	 */
	public static Function<Double, Double> bucket(double width) {
		return x -> width * Math.floor(x / width) + width / 2.0;
	}
	
	/**
	 * Groups numeric values by their rounded value.
	 * 
	 * @param parameter the parameter
	 * @return the grouping function
	 */
	public static Function<Sample, Integer> round(Parameter<Double> parameter) {
		return round().compose(x -> parameter.readValue(x));
	}
	
	/**
	 * Groups numeric values by their rounded value.
	 * 
	 * @return the grouping function
	 */
	public static Function<Double, Integer> round() {
		return x -> (int)Math.round(x);
	}
	
}