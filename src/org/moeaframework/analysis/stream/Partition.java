package org.moeaframework.analysis.stream;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

public interface Partition<K, V> extends DataStream<Pair<K, V>> {
	
	public <R> Partition<K, R> map(Function<V, R> mapping);
	
	public Partition<K, V> sorted();

	public Partition<K, V> sorted(Comparator<K> comparator);
	
	public <T> Partition<K, V> filter(Predicate<K> preicate);

	public <T> Groups<T, K, V> groupBy(Function<K, T> grouping);
		
	public <R> R measure(Function<Stream<V>, R> measure);
	
	public V reduce(BinaryOperator<V> op);
	
	public V reduce(V identity, BinaryOperator<V> op);
	
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
	
	public default Pair<K, V> single() {
		return stream().findAny().get();
	}
	
	public default List<K> keys() {
		return stream().map(Pair::getKey).toList();
	}
	
	public default List<V> values() {
		return stream().map(Pair::getValue).toList();
	}
	
}