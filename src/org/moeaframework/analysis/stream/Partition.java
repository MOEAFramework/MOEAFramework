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

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.stream.Streams;
import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * A stream of key-value pairs.  Duplicate keys are permitted.
 * 
 * @param <K> the type of the partition key
 * @param <V> the type of the partition value
 */
public interface Partition<K, V> extends Formattable<Pair<K, V>> {
	
	public int size();
	
	public Stream<Pair<K, V>> stream();
	
	public default List<K> keys() {
		return stream().map(Pair::getKey).toList();
	}
	
	public default K[] keys(IntFunction<K[]> generator) {
		return stream().toArray(generator);
	}
	
	public default List<V> values() {
		return stream().map(Pair::getValue).toList();
	}
	
	public default V[] values(IntFunction<V[]> generator) {
		return stream().toArray(generator);
	}
	
	public default <R> Partition<K, R> map(Function<V, R> map) {
		return new ImmutablePartition<K, R>(stream().map(x -> Pair.of(x.getKey(), map.apply(x.getValue()))));
	}
			
	public default Partition<K, V> sorted() {
		return new ImmutablePartition<K, V>(stream().sorted());
	}

	public default Partition<K, V> sorted(Comparator<K> comparator) {
		return new ImmutablePartition<K, V>(stream().sorted((x, y) -> comparator.compare(x.getKey(), y.getKey())));
	}
	
	public default Pair<K, V> first() {
		return stream().findFirst().get();
	}
	
	public default Pair<K, V> any() {
		return stream().findAny().get();
	}
	
	public default Pair<K, V> singleOrDefault(K defaultKey, V defaultValue) {
		if (size() == 0) {
			return Pair.of(defaultKey, defaultValue);
		} else {
			return single();
		}
	}
	
	public default Pair<K, V> single() {
		if (size() != 1) {
			throw new UnsupportedOperationException("expected partition to contain exactly one item, but found " +
					size());
		}
		
		return any();
	}
	
	public default Partition<K, V> filter(Predicate<K> predicate) {
		return new ImmutablePartition<K, V>(stream().filter(x -> predicate.test(x.getKey())));
	}

	public default <T> Groups<T, K, V> groupBy(Function<K, T> group) {
		return new Groups<T, K, V>(stream()
				.collect(Collectors.groupingBy(x -> group.apply(x.getKey())))
				.entrySet().stream()
				.map(x -> Pair.of(x.getKey(), new ImmutablePartition<K, V>(x.getValue()))));
	}
			
	public default V reduce(BinaryOperator<V> op) {
		return stream().map(Pair::getValue).reduce(op).get();
	}
	
	public default V reduce(V identity, BinaryOperator<V> op) {
		return stream().map(Pair::getValue).reduce(identity, op);
	}
	
	public default Partition<K, V> distinct() {
		return new ImmutablePartition<K, V>(stream().distinct());
	}
	
	public default <R> R measure(Function<Stream<V>, R> measure) {
		return measure.apply(stream().map(Pair::getValue));
	}
	
	public default void forEach(BiConsumer<K, V> consumer) {
		stream().forEach(x -> consumer.accept(x.getKey(), x.getValue()));
	}
	
	public default void enumerate(BiConsumer<Integer, Pair<K, V>> consumer) {
		int index = 0;
		Iterator<Pair<K, V>> iterator = stream().iterator();
		
		while (iterator.hasNext()) {
			Pair<K, V> item = iterator.next();
			consumer.accept(index, item);
			index += 1;
		}
	}
	
	public default TabularData<Pair<K, V>> asTabularData() {
		TabularData<Pair<K, V>> table = new TabularData<Pair<K, V>>(stream().toList());
		table.addColumn(new Column<Pair<K, V>, K>("Key", Pair::getKey));
		table.addColumn(new Column<Pair<K, V>, V>("Value", Pair::getValue));
		return table;
	}
	
	public static <V> Partition<V, V> of(List<V> list) {
		return of(Mappings.identity(), list);
	}
	
	public static <V> Partition<V, V> of(Stream<V> stream) {
		return of(Mappings.identity(), stream);
	}
	
	public static <V> Partition<V, V> of(Iterable<V> iterable) {
		return of(Mappings.identity(), iterable);
	}
	
	public static <V> Partition<V, V> of(V[] array) {
		return of(Mappings.identity(), array);
	}
	
	public static <K, V> Partition<K, V> of(Map<K, V> map) {
		return new ImmutablePartition<K, V>(map.entrySet().stream().map(x -> Pair.of(x)));
	}
	
	public static <K, V> Partition<K, V> of(Function<V, K> key, List<V> list) {
		return of(key, list.stream());
	}
	
	public static <K, V> Partition<K, V> of(Function<V, K> key, Stream<V> stream) {
		return new ImmutableDataStream<V>(stream).keyedOn(key);
	}
	
	public static <K, V> Partition<K, V> of(Function<V, K> key, Iterable<V> iterable) {
		return of(key, Streams.of(iterable));
	}
	
	public static <K, V> Partition<K, V> of(Function<V, K> key, V[] array) {
		return of(key, Streams.of(array));
	}
	
	public static <K, V> Partition<K, V> zip(List<K> keys, List<V> values) {
		return zip(keys, values);
	}
	
	public static <K, V> Partition<K, V> zip(Iterable<K> keys, Iterable<V> values) {
		return new ImmutablePartition<K, V>(Iterators.zip(keys, values));
	}
	
	public static <K, V> Partition<K, V> zip(K[] keys, V[] values) {
		return zip(keys, values);
	}
	
}