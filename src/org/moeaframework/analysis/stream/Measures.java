package org.moeaframework.analysis.stream;

import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

// For the sake of simplicity, we assume all streams contain at least one element.
public class Measures {
	
	private Measures() {
		super();
	}
	
	public static <T> Function<Stream<T>, Integer> count() {
		return (stream) -> (int)stream.count();
	}
	
	public static <T> Function<Stream<T>, T> any() {
		return (stream) -> stream.findAny().get();
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
		return (stream) -> stream.mapToDouble(x -> x.doubleValue()).average().getAsDouble();
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
