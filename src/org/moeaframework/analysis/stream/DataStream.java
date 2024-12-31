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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.stream.Streams;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * A stream of values.
 * 
 * @param <V> the type of each value
 */
public interface DataStream<V> extends Formattable<V>, Iterable<V> {
	
	/**
	 * Returns the number of values in this data stream.
	 * 
	 * @return the number of values
	 */
	public int size();
	
	/**
	 * Returns a {@link Stream} of the values in this data stream.
	 * 
	 * @return the stream
	 */
	public Stream<V> stream();
	
	/**
	 * Returns {@code true} if this data stream is empty.
	 * 
	 * @return {@code true} if this data stream is empty; {@code false} otherwise
	 */
	public default boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns the values in this stream as a list.
	 * 
	 * @return the list of values
	 */
	public default List<V> values() {
		return stream().toList();
	}
	
	/**
	 * Returns the values in this stream as an array.
	 * 
	 * @param generator generator for creating the array
	 * @return the array of values
	 */
	public default V[] values(IntFunction<V[]> generator) {
		return stream().toArray(generator);
	}
	
	/**
	 * Applies a function to each value in the stream, returning a partition of the results.
	 * 
	 * @param <R> the result type
	 * @param map the mapping function
	 * @return the partition of results
	 */
	public default <R> Partition<V, R> map(Function<V, R> map) {
		return new ImmutablePartition<>(stream().map(x -> Pair.of(x, map.apply(x))));
	}
	
	/**
	 * Sorts the stream using the natural ordering of values.
	 * 
	 * @return the sorted stream
	 * @throws ClassCastException if the value type is not {@link Comparable}
	 */
	public default DataStream<V> sorted() {
		return new ImmutableDataStream<>(stream().sorted());
	}

	/**
	 * Sorts the stream.
	 * 
	 * @param comparator the comparator used to sort values
	 * @return the sorted stream
	 */
	public default DataStream<V> sorted(Comparator<V> comparator) {
		return new ImmutableDataStream<>(stream().sorted(comparator));
	}
	
	/**
	 * Returns the first value from this stream.
	 * 
	 * @return the selected value
	 * @throws NoSuchElementException if the stream is empty
	 */
	public default V first() {
		return stream().findFirst().get();
	}
	
	/**
	 * Returns any value from this stream.
	 * 
	 * @return the selected value
	 * @throws NoSuchElementException if the stream is empty
	 */
	public default V any() {
		return stream().findAny().get();
	}
	
	/**
	 * Returns the singular value contained in this stream, or if empty, returns the given default value.
	 * 
	 * @param defaultValue the default value
	 * @return the single value or default value
	 */
	public default V singleOrDefault(V defaultValue) {
		if (size() == 0) {
			return defaultValue;
		} else {
			return single();
		}
	}
	
	/**
	 * Asserts this stream contains exactly one value, returning said value.
	 * 
	 * @return the value
	 * @throws NoSuchElementException if the stream was empty or contained more than one value
	 */
	public default V single() {
		if (size() != 1) {
			throw new NoSuchElementException("Expected data stream to contain exactly one value, but found " + size());
		}
		
		return any();
	}
	
	/**
	 * Skips the first {@code n} values in the stream.
	 * 
	 * @param n the number of values to skip
	 * @return the resulting data stream
	 */
	public default DataStream<V> skip(int n) {
		return new ImmutableDataStream<>(stream().skip(n));
	}
	
	/**
	 * Filters this stream, keeping only those values evaluating to {@code true}.
	 * 
	 * @param predicate the predicate function
	 * @return the resulting data stream
	 */
	public default DataStream<V> filter(Predicate<V> predicate) {
		return new ImmutableDataStream<>(stream().filter(predicate));
	}
	
	/**
	 * Applies a grouping function to the values in this data stream.  Values with the same grouping key are grouped
	 * together.
	 * 
	 * @param <K> the type of the grouping key
	 * @param group the grouping function
	 * @return the resulting groups
	 */
	public default <K> Groups<K, K, V> groupBy(Function<V, K> group) {
		return keyedOn(group).groupBy(x -> x);
	}
	
	/**
	 * Converts this data stream into a {@link Partition} by assigning a key to each value.
	 * 
	 * @param <K> the type of the key
	 * @param key the function mapping values to their key
	 * @return the partition
	 */
	public default <K> Partition<K, V> keyedOn(Function<V, K> key) {
		return new ImmutablePartition<>(stream().map(x -> Pair.of(key.apply(x), x)));
	}
	
	/**
	 * Applies a binary reduction operator to the values in this stream.  See {@link Stream#reduce(BinaryOperator)}
	 * for more details.
	 * 
	 * @param op the binary reduction operator
	 * @return the final result from the reduction operator
	 * @throws NoSuchElementException if the stream is empty
	 */
	public default V reduce(BinaryOperator<V> op) {
		return stream().reduce(op).get();
	}
	
	/**
	 * Applies a binary reduction operator to the values in this stream.  See
	 * {@link Stream#reduce(Object, BinaryOperator)} for more details.
	 * 
	 * @param identity the initial value supplied to the binary operator
	 * @param op the binary reduction operator
	 * @return the final result from the reduction operator
	 */
	public default V reduce(V identity, BinaryOperator<V> op) {
		return stream().reduce(identity, op);
	}
	
	/**
	 * Retains only the unique values in this stream.
	 * 
	 * @return the resulting data stream
	 */
	public default DataStream<V> distinct() {
		return new ImmutableDataStream<>(stream().distinct());
	}
	
	/**
	 * Applies a measurement function to this stream.
	 * 
	 * @param <R> the return value
	 * @param measure the measurement function
	 * @return the measured value
	 */
	public default <R> R measure(Function<Stream<V>, R> measure) {
		return measure.apply(stream());
	}
	
	/**
	 * Similar to {@link #forEach(Consumer)} except the index is included.
	 * 
	 * @param consumer the method to invoke
	 */
	public default void enumerate(BiConsumer<Integer, V> consumer) {
		int index = 0;
		Iterator<V> iterator = stream().iterator();
		
		while (iterator.hasNext()) {
			V item = iterator.next();
			consumer.accept(index, item);
			index += 1;
		}
	}
	
	@Override
	public default TabularData<V> asTabularData() {
		TabularData<V> table = new TabularData<>(stream().toList());
		table.addColumn(new Column<>("Data", x -> x));
		return table;
	}
	
	/**
	 * Creates an empty data stream.
	 * 
	 * @param <V> the type of the stream
	 * @return the constructed data stream
	 */
	public static <V> DataStream<V> of() {
		return new ImmutableDataStream<>();
	}
	
	/**
	 * Creates a data stream with the contents of a {@link Stream}.
	 * 
	 * @param <V> the type of the stream
	 * @param stream the source stream
	 * @return the constructed data stream
	 */
	public static <V> DataStream<V> of(Stream<V> stream) {
		return new ImmutableDataStream<>(stream);
	}
	
	/**
	 * Creates a data stream with the contents of a {@link IntStream}.
	 * 
	 * @param stream the source stream
	 * @return the constructed data stream
	 */
	public static DataStream<Integer> of(IntStream stream) {
		return of(stream.boxed());
	}
	
	/**
	 * Creates a data stream with the contents of a {@link DoubleStream}.
	 * 
	 * @param stream the source stream
	 * @return the constructed data stream
	 */
	public static DataStream<Double> of(DoubleStream stream) {
		return of(stream.boxed());
	}
	
	/**
	 * Creates a data stream with the contents of an {@link Iterable}.
	 * 
	 * @param <V> the type of the iterable
	 * @param iterable the iterable
	 * @return the constructed data stream
	 */
	public static <V> DataStream<V> of(Iterable<V> iterable) {
		return of(Streams.of(iterable));
	}
	
	/**
	 * Creates a data stream with the contents of an array.
	 * 
	 * @param <V> the type of the array
	 * @param array the array
	 * @return the constructed data stream
	 */
	public static <V> DataStream<V> of(V[] array) {
		return of(Streams.of(array));
	}
	
	/**
	 * Constructs a data stream containing the integer values {@code [0, ..., count-1]}.
	 * 
	 * @param count the size of the returned stream
	 * @return the constructed data stream
	 */
	public static DataStream<Integer> range(int count) {
		return new ImmutableDataStream<>(IntStream.range(0, count).boxed());
	}
	
	/**
	 * Constructs a data stream containing the integer values between the given start (inclusive) and end (exclusive).
	 * 
	 * @param startInclusive the starting value
	 * @param endExclusive the ending value (exclusive)
	 * @return the constructed data stream
	 */
	public static DataStream<Integer> range(int startInclusive, int endExclusive) {
		return new ImmutableDataStream<>(IntStream.range(startInclusive, endExclusive).boxed());
	}
	
	/**
	 * Constructs a data stream generated by invoking a {@link Supplier} a fixed number of times.
	 * 
	 * @param <V> the type returned by the supplier
	 * @param count the number of invocations of the supplier
	 * @param supplier the supplier
	 * @return the constructed data stream
	 */
	public static <V> DataStream<V> repeat(int count, Supplier<V> supplier) {
		return new ImmutableDataStream<>(IntStream.range(0, count).mapToObj(i -> supplier.get()));
	}
	
	/**
	 * Similar to {@link #repeat(int, Supplier)} except the index from {@code [0, ..., count-1]} is passed as an
	 * argument to the function.
	 * 
	 * @param <V> the type returned by the function
	 * @param count the number of invocations of the function
	 * @param function the function taking the index as the first argument
	 * @return the constructed data stream
	 */
	public static <V> DataStream<V> enumerate(int count, IntFunction<V> function) {
		return new ImmutableDataStream<>(IntStream.range(0, count).mapToObj(i -> function.apply(i)));
	}

}
