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
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

/**
 * Standard collection of measurement functions.
 * <p>
 * <b>Implementation note:</b> All functions assume the underlying stream contains at least one element, otherwise
 * {@link java.util.NoSuchElementException} is thrown.  This simplifies the interface since we don't need to return
 * {@link java.util.Optional} when it's most likely not necessary.
 */
public class Measures {
	
	private Measures() {
		super();
	}
	
	public static <T> Function<Stream<T>, Integer> count() {
		return (stream) -> (int)stream.count();
	}
	
	public static <T extends Number> Function<Stream<T>, Double> sum() {
		return (stream) -> stream.mapToDouble(x -> x.doubleValue()).sum();
	}
	
	public static <T extends Comparable<T>> Function<Stream<T>, T> min() {
		return (stream) -> stream.min((x, y) -> x.compareTo(y)).get();
	}
	
	public static <T extends Comparable<T>> Function<Stream<T>, T> max() {
		return (stream) -> stream.max((x, y) -> x.compareTo(y)).get();
	}
	
	public static <T extends Number> Function<Stream<T>, Double> average() {
		return (stream) -> new Mean().evaluate(stream.mapToDouble(x -> x.doubleValue()).toArray());
	}
	
	public static <T extends Number> Function<Stream<T>, Double> median() {
		return (stream) -> new Median().evaluate(stream.mapToDouble(x -> x.doubleValue()).toArray());
	}
	
	public static <T extends Number> Function<Stream<T>, Double> percentile(double percentile) {
		return (stream) -> new Percentile(percentile).evaluate(stream.mapToDouble(x -> x.doubleValue()).toArray());
	}
	
	public static <T extends Number> Function<Stream<T>, StatisticalSummary> stats() {
		return (stream) -> new DescriptiveStatistics(stream.mapToDouble(x -> x.doubleValue()).toArray());
	}

}
