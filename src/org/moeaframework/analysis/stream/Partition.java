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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.stream.Streams;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * A stream of key-value pairs.  Duplicate keys are permitted.
 * 
 * @param <K> the type of the partition key
 * @param <V> the type of the partition value
 */
public interface Partition<K, V> extends Formattable<Pair<K, V>> {
	
	/**
	 * Returns the number of key-value pairs in this partition.
	 * 
	 * @return the number of items
	 */
	public int size();
	
	/**
	 * Returns a {@link Stream} of the key-value pairs in this partition.
	 * 
	 * @return the stream
	 */
	public Stream<Pair<K, V>> stream();
	
	/**
	 * Returns {@code true} if this partition is empty.
	 * 
	 * @return {@code true} if this partition is empty; {@code false} otherwise
	 */
	public default boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * Returns the keys in this partition as a list.
	 * 
	 * @return the list of keys
	 */
	public default List<K> keys() {
		return stream().map(Pair::getKey).toList();
	}
	
	/**
	 * Returns the keys in this partition as an array.
	 * 
	 * @param generator generator for creating the array
	 * @return the array of keys
	 */
	public default K[] keys(IntFunction<K[]> generator) {
		return stream().map(Pair::getKey).toArray(generator);
	}
	
	/**
	 * Returns the values in this partition as a list.
	 * 
	 * @return the list of values
	 */
	public default List<V> values() {
		return stream().map(Pair::getValue).toList();
	}
	
	/**
	 * Returns the values in this partition as an array.
	 * 
	 * @param generator generator for creating the array
	 * @return the array of values
	 */
	public default V[] values(IntFunction<V[]> generator) {
		return stream().map(Pair::getValue).toArray(generator);
	}
	
	/**
	 * Applies a function to each value in the partition, returning a partition of the results.
	 * 
	 * @param <R> the result type
	 * @param map the mapping function
	 * @return the partition of results
	 */
	public default <R> Partition<K, R> map(Function<V, R> map) {
		return new ImmutablePartition<>(stream().map(x -> Pair.of(x.getKey(), map.apply(x.getValue()))));
	}
	
	/**
	 * Sorts the partition using the natural ordering of keys.
	 * 
	 * @return the sorted stream
	 * @throws ClassCastException if the key type is not {@link Comparable}
	 */
	public default Partition<K, V> sorted() {
		return new ImmutablePartition<>(stream().sorted());
	}

	/**
	 * Sorts the partition based on their keys.
	 * 
	 * @param comparator the comparator used to sort keys
	 * @return the sorted partition
	 */
	public default Partition<K, V> sorted(Comparator<K> comparator) {
		return new ImmutablePartition<>(stream().sorted((x, y) -> comparator.compare(x.getKey(), y.getKey())));
	}
	
	/**
	 * Returns the first key-value pair from this partition.
	 * 
	 * @return the selected key-value pair
	 * @throws NoSuchElementException if the partition is empty
	 */
	public default Pair<K, V> first() {
		return stream().findFirst().get();
	}
	
	/**
	 * Returns any key-value pair from this partition.
	 * 
	 * @return the selected key-value pair
	 * @throws NoSuchElementException if the partition is empty
	 */
	public default Pair<K, V> any() {
		return stream().findAny().get();
	}
	
	/**
	 * Returns the singular key-value pair contained in this partition, or if empty, returns the given default value.
	 * 
	 * @param defaultKey the default key
	 * @param defaultValue the default value
	 * @return the single key-value pair or default value
	 */
	public default Pair<K, V> singleOrDefault(K defaultKey, V defaultValue) {
		if (size() == 0) {
			return Pair.of(defaultKey, defaultValue);
		} else {
			return single();
		}
	}
	
	/**
	 * Asserts this partition contains exactly one key-value pair, returning said item.
	 * 
	 * @return the key-value pair
	 * @throws NoSuchElementException if the partition was empty or contained more than one key-value pair
	 */
	public default Pair<K, V> single() {
		if (size() != 1) {
			throw new NoSuchElementException("expected partition to contain exactly one key-value pair, but found " + size());
		}
		
		return any();
	}
	
	/**
	 * Filters this partition, keeping only those keys evaluating to {@code true}.
	 * 
	 * @param predicate the predicate function based on the key
	 * @return the resulting partition
	 */
	public default Partition<K, V> filter(Predicate<K> predicate) {
		return new ImmutablePartition<>(stream().filter(x -> predicate.test(x.getKey())));
	}

	/**
	 * Applies a grouping function to the keys in this partition.  Keys with the same grouping key are grouped
	 * together.
	 * 
	 * @param <G> the type of the grouping key
	 * @param group the grouping function
	 * @return the resulting groups
	 */
	public default <G> Groups<G, K, V> groupBy(Function<K, G> group) {
		return new Groups<>(stream()
				.collect(Collectors.groupingBy(x -> group.apply(x.getKey())))
				.entrySet().stream()
				.map(x -> Pair.of(x.getKey(), new ImmutablePartition<K, V>(x.getValue()))));
	}
	
	/**
	 * Applies a binary reduction operator to the values in this partition.  See {@link Stream#reduce(BinaryOperator)}
	 * for more details.
	 * 
	 * @param op the binary reduction operator
	 * @return the final result from the reduction operator
	 * @throws NoSuchElementException if the partition is empty
	 */
	public default V reduce(BinaryOperator<V> op) {
		return stream().map(Pair::getValue).reduce(op).get();
	}
	
	/**
	 * Applies a binary reduction operator to the values in this partition.  See
	 * {@link Stream#reduce(Object, BinaryOperator)} for more details.
	 * 
	 * @param identity the initial value supplied to the binary operator
	 * @param op the binary reduction operator
	 * @return the final result from the reduction operator
	 */
	public default V reduce(V identity, BinaryOperator<V> op) {
		return stream().map(Pair::getValue).reduce(identity, op);
	}
	
	/**
	 * Retains only the unique key-value pairs in this partition.
	 * 
	 * @return the resulting partition
	 */
	public default Partition<K, V> distinct() {
		return new ImmutablePartition<>(stream().distinct());
	}
	
	/**
	 * Applies a measurement function to the values in this partition.
	 * 
	 * @param <R> the return value
	 * @param measure the measurement function
	 * @return the measured value
	 */
	public default <R> R measure(Function<Stream<V>, R> measure) {
		return measure.apply(stream().map(Pair::getValue));
	}
	
	/**
	 * Invokes a method for each key-value pair in this partition.
	 * 
	 * @param consumer the method to invoke
	 */
	public default void forEach(BiConsumer<K, V> consumer) {
		stream().forEach(x -> consumer.accept(x.getKey(), x.getValue()));
	}
	
	/**
	 * Similar to {@link #forEach(BiConsumer)} except the index is included.
	 * 
	 * @param consumer the method to invoke
	 */
	public default void enumerate(BiConsumer<Integer, Pair<K, V>> consumer) {
		int index = 0;
		Iterator<Pair<K, V>> iterator = stream().iterator();
		
		while (iterator.hasNext()) {
			Pair<K, V> item = iterator.next();
			consumer.accept(index, item);
			index += 1;
		}
	}
	
	@Override
	public default TabularData<Pair<K, V>> asTabularData() {
		return TabularData.of(stream().toList());
	}
	
	/**
	 * Creates an empty partition.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @return the constructed partition
	 */
	public static <K, V> Partition<K, V> of() {
		return new ImmutablePartition<>();
	}
	
	/**
	 * Creates a partition with the contents of a {@link Stream}.  The values are also used as the keys.
	 * 
	 * @param <V> the type of the stream
	 * @param stream the source stream
	 * @return the constructed partition
	 */
	public static <V> Partition<V, V> of(Stream<V> stream) {
		return of(Groupings.exactValue(), stream);
	}
	
	/**
	 * Creates a partition with the contents of a {@link IntStream}.  The values are also used as the keys.
	 * 
	 * @param stream the source stream
	 * @return the constructed partition
	 */
	public static Partition<Integer, Integer> of(IntStream stream) {
		return of(stream.boxed());
	}
	
	/**
	 * Creates a partition with the contents of a {@link DoubleStream}.  The values are also used as the keys.
	 * 
	 * @param stream the source stream
	 * @return the constructed partition
	 */
	public static Partition<Double, Double> of(DoubleStream stream) {
		return of(stream.boxed());
	}
	
	/**
	 * Creates a partition with the contents of an {@link Iterable}.  The values are also used as the keys.
	 * 
	 * @param <V> the type of the iterable
	 * @param iterable the iterable
	 * @return the constructed partition
	 */
	public static <V> Partition<V, V> of(Iterable<V> iterable) {
		return of(Groupings.exactValue(), iterable);
	}
	
	/**
	 * Creates a partition with the contents of an array.  The values are also used as the keys.
	 * 
	 * @param <V> the type of the array
	 * @param array the array
	 * @return the constructed partition
	 */
	public static <V> Partition<V, V> of(V[] array) {
		return of(Groupings.exactValue(), array);
	}
	
	/**
	 * Creates a partition with the contents of a {@link Map}.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param map the source map
	 * @return the constructed partition
	 */
	public static <K, V> Partition<K, V> of(Map<K, V> map) {
		return new ImmutablePartition<>(map.entrySet().stream().map(Pair::of));
	}
	
	/**
	 * Creates a partition with the contents of a {@link Stream}.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param key the function returning the key for each value
	 * @param stream the source stream
	 * @return the constructed partition
	 */
	public static <K, V> Partition<K, V> of(Function<V, K> key, Stream<V> stream) {
		return new ImmutableDataStream<>(stream).keyedOn(key);
	}
	
	/**
	 * Creates a partition with the contents of a {@link IntStream}.
	 * 
	 * @param <K> the type of the keys
	 * @param key the function returning the key for each value
	 * @param stream the source stream
	 * @return the constructed partition
	 */
	public static <K> Partition<K, Integer> of(Function<Integer, K> key, IntStream stream) {
		return of(key, stream.boxed());
	}
	
	/**
	 * Creates a partition with the contents of a {@link DoubleStream}.
	 * 
	 * @param <K> the type of the keys
	 * @param key the function returning the key for each value
	 * @param stream the source stream
	 * @return the constructed partition
	 */
	public static <K> Partition<K, Double> of(Function<Double, K> key, DoubleStream stream) {
		return of(key, stream.boxed());
	}
	
	/**
	 * Creates a partition with the contents of an {@link Iterable}.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param key the function returning the key for each value
	 * @param iterable the source iterable
	 * @return the constructed partition
	 */
	public static <K, V> Partition<K, V> of(Function<V, K> key, Iterable<V> iterable) {
		return of(key, Streams.of(iterable));
	}
	
	/**
	 * Creates a partition with the contents of an array.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param key the function returning the key for each value
	 * @param array the source array
	 * @return the constructed partition
	 */
	public static <K, V> Partition<K, V> of(Function<V, K> key, V[] array) {
		return of(key, Streams.of(array));
	}
	
	/**
	 * Creates a partition by "zipping" together two iterables.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param keys the iterable of keys
	 * @param values the iterable of values
	 * @return the constructed partition
	 */
	public static <K, V> Partition<K, V> zip(Iterable<K> keys, Iterable<V> values) {
		return new ImmutablePartition<>(Iterators.zip(keys, values));
	}
	
	/**
	 * Creates a partition by "zipping" together two arrays.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param keys the array of keys
	 * @param values the array of values
	 * @return the constructed partition
	 */
	public static <K, V> Partition<K, V> zip(K[] keys, V[] values) {
		return zip(Arrays.asList(keys), Arrays.asList(values));
	}
	
}