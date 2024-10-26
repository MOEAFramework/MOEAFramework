package org.moeaframework.analysis.stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TabularData;

public class ImmutablePartition<K, V> implements Partition<K, V> {
	
	protected final List<Pair<K, V>> content;
	
	public ImmutablePartition() {
		this(new ArrayList<>());
	}
	
	public ImmutablePartition(List<Pair<K, V>> content) {
		super();
		this.content = content;
	}
	
	public ImmutablePartition(Stream<Pair<K, V>> stream) {
		this(stream.toList());
	}
	
	public int size() {
		return content.size();
	}
	
	public Pair<K, V> get(int index) {
		return content.get(index);
	}
	
	@Override
	public Stream<Pair<K, V>> stream() {
		return content.stream();
	}
	
	public List<K> keys() {
		return stream().map(Pair::getKey).toList();
	}
	
	public List<V> values() {
		return stream().map(Pair::getValue).toList();
	}
	
	public <R> Partition<K, R> map(Function<V, R> op) {
		return new ImmutablePartition<K, R>(stream().map(x -> Pair.of(x.getKey(), op.apply(x.getValue()))));
	}
	
	public V reduce(BinaryOperator<V> op) {
		return stream().map(Pair::getValue).reduce(op).get();
	}
	
	public V reduce(V identity, BinaryOperator<V> op) {
		return stream().map(Pair::getValue).reduce(identity, op);
	}
	
	@Override
	public Partition<K, V> sorted() {
		return new ImmutablePartition<K, V>(stream().sorted());
	}
	
	public Partition<K, V> sorted(Comparator<K> comparator) {
		return new ImmutablePartition<K, V>(stream().sorted((x, y) -> comparator.compare(x.getKey(), y.getKey())));
	}
	
	@Override
	public <T> Partition<K, V> filter(Predicate<K> predicate) {
		return new ImmutablePartition<K, V>(stream().filter(x -> predicate.test(x.getKey())));
	}
	
	@Override
	public Partition<K, V> distinct() {
		return new ImmutablePartition<K, V>(stream().distinct());
	}
	
	@Override
	public <R> R measure(Function<Stream<V>, R> measure) {
		return measure.apply(stream().map(Pair::getValue));
	}

	@Override
	public <T> Groups<T, K, V> groupBy(Function<K, T> grouping) {
		return new Groups<T, K, V>(stream()
				.collect(Collectors.groupingBy(x -> grouping.apply(x.getKey())))
				.entrySet().stream()
				.map(x -> Pair.of(x.getKey(), new ImmutablePartition<K, V>(x.getValue()))));
	}
	
	@Override
	public TabularData<Pair<K, V>> asTabularData() {
		TabularData<Pair<K, V>> table = new TabularData<Pair<K, V>>(content);
		table.addColumn(new Column<Pair<K, V>, K>("Key", Pair::getKey));
		table.addColumn(new Column<Pair<K, V>, V>("Value", Pair::getValue));
		return table;
	}
	
}