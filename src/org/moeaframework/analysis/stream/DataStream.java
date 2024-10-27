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
 * Interface for a data stream containing only values.
 * 
 * @param <V> the type of each value
 */
public interface DataStream<V> extends Streamable<V>, Formattable<V> {
	
	public int size();
	
	public V get(int index);
	
	public default List<V> values() {
		return stream().toList();
	}
	
	public default V[] values(IntFunction<V[]> generator) {
		return stream().toArray(generator);
	}
	
	public default <R> DataStream<R> map(Function<V, R> mapping) {
		return new ImmutableDataStream<R>(stream().map(mapping));
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
	
	public default DataStream<V> filter(Predicate<V> predicate) {
		return new ImmutableDataStream<V>(stream().filter(predicate));
	}
	
	public default <K> Groups<K, K, V> groupBy(Function<V, K> grouping) {
		return keyedOn(grouping).groupBy(x -> x);
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
		table.addColumn(new Column<V, V>("Data", Mappings.identity()));
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
