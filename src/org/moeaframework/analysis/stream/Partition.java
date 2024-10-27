package org.moeaframework.analysis.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
 * A partition is a collection of key-value pairs.  The keys serve as the identifier of the value, however unlike a
 * {@link java.util.Map}, the keys are not unique.
 * 
 * @param <K> the type of the partition key
 * @param <V> the type of the partition value
 */
public interface Partition<K, V> extends Streamable<Pair<K, V>>, Formattable<Pair<K, V>> {
	
	public int size();
	
	public Pair<K, V> get(int index);
	
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
	
	public default <R> Partition<K, R> map(Function<V, R> op) {
		return new ImmutablePartition<K, R>(stream().map(x -> Pair.of(x.getKey(), op.apply(x.getValue()))));
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
	
	public default Partition<K, V> filter(Predicate<K> predicate) {
		return new ImmutablePartition<K, V>(stream().filter(x -> predicate.test(x.getKey())));
	}

	public default <T> Groups<T, K, V> groupBy(Function<K, T> grouping) {
		return new Groups<T, K, V>(stream()
				.collect(Collectors.groupingBy(x -> grouping.apply(x.getKey())))
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