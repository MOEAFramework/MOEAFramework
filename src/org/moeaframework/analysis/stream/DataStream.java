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
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
public interface DataStream<V> extends Formattable<V> {
	
	public int size();
	
	public Stream<V> stream();

	public default List<V> values() {
		return stream().toList();
	}
	
	public default V[] values(IntFunction<V[]> generator) {
		return stream().toArray(generator);
	}
	
	public default <R> DataStream<R> map(Function<V, R> map) {
		return new ImmutableDataStream<R>(stream().map(map));
	}
	
	public default DataStream<V> sorted() {
		return new ImmutableDataStream<V>(stream().sorted());
	}

	public default DataStream<V> sorted(Comparator<V> comparator) {
		return new ImmutableDataStream<V>(stream().sorted(comparator));
	}
	
	public default V first() {
		return stream().findFirst().get();
	}
	
	public default V any() {
		return stream().findAny().get();
	}
	
	public default V singleOrDefault(V defaultValue) {
		if (size() == 0) {
			return defaultValue;
		} else {
			return single();
		}
	}
	
	public default V single() {
		if (size() != 1) {
			throw new UnsupportedOperationException("expected data stream to contain exactly one item, but found " +
					size());
		}
		
		return any();
	}
	
	public default DataStream<V> filter(Predicate<V> predicate) {
		return new ImmutableDataStream<V>(stream().filter(predicate));
	}
	
	public default <K> Groups<K, K, V> groupBy(Function<V, K> group) {
		return keyedOn(group).groupBy(x -> x);
	}
	
	public default <K> Partition<K, V> keyedOn(Function<V, K> key) {
		return new ImmutablePartition<K, V>(stream().map(x -> Pair.of(key.apply(x), x)));
	}
			
	public default V reduce(BinaryOperator<V> op) {
		return stream().reduce(op).get();
	}
	
	public default V reduce(V identity, BinaryOperator<V> op) {
		return stream().reduce(identity, op);
	}
	
	public default DataStream<V> distinct() {
		return new ImmutableDataStream<V>(stream().distinct());
	}
	
	public default <R> R measure(Function<Stream<V>, R> measure) {
		return measure.apply(stream());
	}
	
	public default void forEach(Consumer<V> consumer) {
		stream().forEach(consumer);
	}
	
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
		TabularData<V> table = new TabularData<V>(stream().toList());
		table.addColumn(new Column<V, V>("Data", x -> x));
		return table;
	}
	
	public static <V> DataStream<V> of(List<V> list) {
		return of(list.stream());
	}
	
	public static <V> DataStream<V> of(Stream<V> stream) {
		return new ImmutableDataStream<V>(stream);
	}
	
	public static <V> DataStream<V> of(Iterable<V> iterable) {
		return of(Streams.of(iterable));
	}
	
	public static <V> DataStream<V> of(V[] array) {
		return of(Streams.of(array));
	}
	
	public static DataStream<Integer> range(int count) {
		return new ImmutableDataStream<Integer>(IntStream.range(0, count).boxed());
	}
	
	public static DataStream<Integer> range(int startInclusive, int endExclusive) {
		return new ImmutableDataStream<Integer>(IntStream.range(startInclusive, endExclusive).boxed());
	}
	
	public static <V> DataStream<V> repeat(int count, Supplier<V> supplier) {
		return new ImmutableDataStream<V>(IntStream.range(0, count).mapToObj(i -> supplier.get()));
	}
	
	public static <V> DataStream<V> enumerate(int count, IntFunction<V> function) {
		return new ImmutableDataStream<V>(IntStream.range(0, count).mapToObj(i -> function.apply(i)));
	}

}
